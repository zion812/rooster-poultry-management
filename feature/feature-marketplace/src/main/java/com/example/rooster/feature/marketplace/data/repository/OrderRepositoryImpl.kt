package com.example.rooster.feature.marketplace.data.repository

import com.example.rooster.core.common.Result
import com.example.rooster.feature.marketplace.data.local.dao.OrderDao
import com.example.rooster.feature.marketplace.data.local.model.OrderEntity
import com.example.rooster.feature.marketplace.data.local.model.OrderItemEntity
import com.example.rooster.feature.marketplace.data.local.model.OrderWithItems
import com.example.rooster.feature.marketplace.data.local.model.ShippingAddressEntity
import com.example.rooster.feature.marketplace.data.remote.MarketplaceRemoteDataSource
import com.example.rooster.feature.marketplace.domain.model.CartItem
import com.example.rooster.feature.marketplace.domain.model.Order
import com.example.rooster.feature.marketplace.domain.model.OrderItem
import com.example.rooster.feature.marketplace.domain.model.OrderStatus
import com.example.rooster.feature.marketplace.domain.model.PaymentDetails
import com.example.rooster.feature.marketplace.domain.model.ShippingAddress
import com.example.rooster.feature.marketplace.domain.repository.OrderRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val orderDao: OrderDao,
    private val remoteDataSource: MarketplaceRemoteDataSource,
    private val gson: Gson
) : OrderRepository {

    override suspend fun createOrder(
        buyerId: String,
        items: List<CartItem>,
        shippingAddress: ShippingAddress,
        billingAddress: ShippingAddress?,
        deliveryInstructions: String?
    ): Result<Order> = withContext(Dispatchers.IO) {
        try {
            val orderId = UUID.randomUUID().toString()
            val now = System.currentTimeMillis()

            val orderItems = items.map { cartItem ->
                OrderItem(
                    orderItemId = UUID.randomUUID().toString(),
                    listingId = cartItem.listingId,
                    sellerId = cartItem.sellerId,
                    title = cartItem.title,
                    descriptionSnapshot = null,
                    unitPriceAtPurchase = cartItem.unitPrice,
                    quantityOrdered = cartItem.quantity,
                    totalPrice = cartItem.unitPrice * cartItem.quantity,
                    primaryImageUrl = cartItem.primaryImageUrl
                )
            }
            val subTotal = orderItems.sumOf { it.totalPrice }
            val totalOrderAmount = subTotal

            val orderDomain = Order(
                orderId = orderId,
                buyerId = buyerId,
                items = orderItems,
                subTotalAmount = subTotal,
                shippingCost = 0.0,
                discountAmount = 0.0,
                taxAmount = 0.0,
                totalOrderAmount = totalOrderAmount,
                currency = "INR",
                orderTimestamp = now,
                lastUpdatedTimestamp = now,
                status = OrderStatus.PENDING_PAYMENT,
                shippingAddress = shippingAddress,
                billingAddress = billingAddress ?: shippingAddress,
                paymentDetails = null,
                deliveryInstructions = deliveryInstructions,
                expectedDeliveryDate = null
            )

            val orderEntity = mapDomainToEntity(orderDomain, needsSync = true)
            val orderItemEntities = orderItems.map { mapDomainToOrderItemEntity(it, orderId) }

            orderDao.insertOrderWithItems(orderEntity, orderItemEntities)

            val remoteResult = remoteDataSource.createOrder(orderDomain)
            if (remoteResult is Result.Success) {
                orderDao.updateOrder(orderEntity.copy(needsSync = false))
                Result.Success(orderDomain.copy(orderId = remoteResult.data))
            } else {
                Result.Success(orderDomain)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getOrderDetails(orderId: String): Flow<Result<Order?>> {
        return orderDao.getOrderWithItemsById(orderId).map { orderWithItems ->
            if (orderWithItems != null) {
                Result.Success(mapOrderWithItemsToDomain(orderWithItems))
            } else {
                Result.Success(null)
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun getOrdersForUser(userId: String): Flow<Result<List<Order>>> {
        return orderDao.getOrdersWithItemsForUser(userId).map { list ->
            Result.Success(list.map { mapOrderWithItemsToDomain(it) })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val statusEnum = OrderStatus.valueOf(newStatus)
            val now = System.currentTimeMillis()
            orderDao.updateOrderStatus(orderId, statusEnum.name, now)

            val remoteResult = remoteDataSource.updateOrderStatus(orderId, newStatus)
            remoteResult
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun cancelOrder(orderId: String, reason: String?): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val now = System.currentTimeMillis()
            orderDao.updateOrderStatus(orderId, OrderStatus.CANCELLED_BY_USER.name, now)

            val remoteResult = remoteDataSource.cancelOrder(orderId)
            remoteResult
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getUnsyncedOrders(): List<Order> = withContext(Dispatchers.IO) {
        val unsyncedOrderEntities = orderDao.getUnsyncedOrdersSuspend()
        unsyncedOrderEntities.map { orderEntity ->
            val items = orderDao.getOrderItemsForOrderSuspend(orderEntity.orderId)
            mapOrderWithItemsToDomain(OrderWithItems(orderEntity, items))
        }
    }

    override suspend fun syncOrder(order: Order): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val remoteResult = remoteDataSource.createOrder(order)

            if (remoteResult is Result.Success) {
                val entity = mapDomainToEntity(order, needsSync = false)
                val orderItemEntities = order.items.map { mapDomainToOrderItemEntity(it, order.orderId) }
                orderDao.insertOrderWithItems(entity, orderItemEntities)
                Result.Success(Unit)
            } else if (remoteResult is Result.Error) {
                Timber.e(remoteResult.exception, "Failed to sync order ${order.orderId} to remote.")
                Result.Error(remoteResult.exception)
            } else {
                Timber.w("Remote sync for order ${order.orderId} did not return a specific error but was not successful.")
                Result.Error(Exception("Unknown error or unsuccessful remote sync for order ${order.orderId}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception during remote order sync for ${order.orderId}")
            Result.Error(e)
        }
    }

    private fun mapDomainToEntity(domain: Order, needsSync: Boolean): OrderEntity {
        return OrderEntity(
            orderId = domain.orderId,
            buyerId = domain.buyerId,
            subTotalAmount = domain.subTotalAmount,
            shippingCost = domain.shippingCost,
            discountAmount = domain.discountAmount,
            taxAmount = domain.taxAmount,
            totalOrderAmount = domain.totalOrderAmount,
            currency = domain.currency,
            orderTimestamp = domain.orderTimestamp,
            lastUpdatedTimestamp = domain.lastUpdatedTimestamp,
            status = domain.status,
            shippingAddress = mapDomainToShippingAddressEntity(domain.shippingAddress),
            billingAddress = domain.billingAddress?.let { mapDomainToShippingAddressEntity(it) },
            paymentDetailsJson = domain.paymentDetails?.let { gson.toJson(it) },
            deliveryInstructions = domain.deliveryInstructions,
            expectedDeliveryDate = domain.expectedDeliveryDate,
            shipmentProvider = domain.shipmentProvider,
            trackingNumber = domain.trackingNumber,
            needsSync = needsSync
        )
    }

    private fun mapDomainToOrderItemEntity(domain: OrderItem, parentOrderId: String): OrderItemEntity {
        return OrderItemEntity(
            orderItemId = domain.orderItemId,
            parentOrderId = parentOrderId,
            listingId = domain.listingId,
            sellerId = domain.sellerId,
            title = domain.title,
            descriptionSnapshot = domain.descriptionSnapshot,
            unitPriceAtPurchase = domain.unitPriceAtPurchase,
            quantityOrdered = domain.quantityOrdered,
            totalPrice = domain.totalPrice,
            primaryImageUrl = domain.primaryImageUrl
        )
    }

    private fun mapDomainToShippingAddressEntity(domain: ShippingAddress): ShippingAddressEntity {
        return ShippingAddressEntity(
            fullName = domain.fullName,
            addressLine1 = domain.addressLine1,
            addressLine2 = domain.addressLine2,
            city = domain.city,
            district = domain.district,
            state = domain.state,
            postalCode = domain.postalCode,
            country = domain.country,
            phoneNumber = domain.phoneNumber,
            landmark = domain.landmark
        )
    }

    private fun mapOrderWithItemsToDomain(orderWithItems: OrderWithItems): Order {
        val orderEntity = orderWithItems.order
        return Order(
            orderId = orderEntity.orderId,
            buyerId = orderEntity.buyerId,
            items = orderWithItems.items.map { mapOrderItemEntityToDomain(it) },
            subTotalAmount = orderEntity.subTotalAmount,
            shippingCost = orderEntity.shippingCost,
            discountAmount = orderEntity.discountAmount,
            taxAmount = orderEntity.taxAmount,
            totalOrderAmount = orderEntity.totalOrderAmount,
            currency = orderEntity.currency,
            orderTimestamp = orderEntity.orderTimestamp,
            lastUpdatedTimestamp = orderEntity.lastUpdatedTimestamp,
            status = orderEntity.status,
            shippingAddress = mapShippingAddressEntityToDomain(orderEntity.shippingAddress),
            billingAddress = orderEntity.billingAddress?.let { mapShippingAddressEntityToDomain(it) },
            paymentDetails = orderEntity.paymentDetailsJson?.let { gson.fromJson(it, PaymentDetails::class.java) },
            deliveryInstructions = orderEntity.deliveryInstructions,
            expectedDeliveryDate = orderEntity.expectedDeliveryDate,
            shipmentProvider = orderEntity.shipmentProvider,
            trackingNumber = orderEntity.trackingNumber
        )
    }

    private fun mapOrderItemEntityToDomain(entity: OrderItemEntity): OrderItem {
        return OrderItem(
            orderItemId = entity.orderItemId,
            listingId = entity.listingId,
            sellerId = entity.sellerId,
            title = entity.title,
            descriptionSnapshot = entity.descriptionSnapshot,
            unitPriceAtPurchase = entity.unitPriceAtPurchase,
            quantityOrdered = entity.quantityOrdered,
            totalPrice = entity.totalPrice,
            primaryImageUrl = entity.primaryImageUrl
        )
    }

    private fun mapShippingAddressEntityToDomain(entity: ShippingAddressEntity): ShippingAddress {
        return ShippingAddress(
            fullName = entity.fullName,
            addressLine1 = entity.addressLine1,
            addressLine2 = entity.addressLine2,
            city = entity.city,
            district = entity.district,
            state = entity.state,
            postalCode = entity.postalCode,
            country = entity.country,
            phoneNumber = entity.phoneNumber,
            landmark = entity.landmark
        )
    }
}