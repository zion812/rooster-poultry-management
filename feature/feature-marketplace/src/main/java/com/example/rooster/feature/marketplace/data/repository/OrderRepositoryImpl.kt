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
import com.example.rooster.feature.marketplace.domain.model.PaymentDetails // Assuming domain model
import com.example.rooster.feature.marketplace.domain.model.ShippingAddress
import com.example.rooster.feature.marketplace.domain.repository.OrderRepository
import com.google.gson.Gson // For PaymentDetails JSON conversion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val orderDao: OrderDao,
    private val remoteDataSource: MarketplaceRemoteDataSource,
    private val gson: Gson // For serializing PaymentDetails to JSON for OrderEntity
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
                    descriptionSnapshot = null, // TODO: Fetch full product desc if needed for snapshot
                    unitPriceAtPurchase = cartItem.unitPrice,
                    quantityOrdered = cartItem.quantity,
                    totalPrice = cartItem.unitPrice * cartItem.quantity,
                    primaryImageUrl = cartItem.primaryImageUrl
                )
            }
            val subTotal = orderItems.sumOf { it.totalPrice }
            // TODO: Implement actual shipping, tax, discount logic
            val totalOrderAmount = subTotal

            val orderDomain = Order(
                orderId = orderId,
                buyerId = buyerId,
                items = orderItems,
                subTotalAmount = subTotal,
                shippingCost = 0.0, // Placeholder
                discountAmount = 0.0, // Placeholder
                taxAmount = 0.0, // Placeholder
                totalOrderAmount = totalOrderAmount,
                currency = "INR",
                orderTimestamp = now,
                lastUpdatedTimestamp = now,
                status = OrderStatus.PENDING_PAYMENT, // Initial status
                shippingAddress = shippingAddress,
                billingAddress = billingAddress ?: shippingAddress, // Default to shipping if not provided
                paymentDetails = null, // Payment happens after order creation
                deliveryInstructions = deliveryInstructions,
                expectedDeliveryDate = null // TODO: Estimate this
            )

            val orderEntity = mapDomainToEntity(orderDomain, needsSync = true)
            val orderItemEntities = orderItems.map { mapDomainToOrderItemEntity(it, orderId) }

            orderDao.insertOrderWithItems(orderEntity, orderItemEntities)

            // Attempt to save to remote
            // TODO: Decide if full Order domain object or a simpler DTO is sent to remote
            val remoteResult = remoteDataSource.createOrder(orderDomain)
            if (remoteResult is Result.Success) {
                orderDao.updateOrder(orderEntity.copy(needsSync = false))
                // The remote might return an updated Order object (e.g. with server timestamps)
                // For now, returning the locally constructed one.
                Result.Success(orderDomain.copy(orderId = remoteResult.data)) // Use ID from remote
            } else {
                // Failed to save to remote, needsSync remains true
                Result.Success(orderDomain) // Still success locally, worker will handle sync
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
                // Try fetching from remote if not found locally (or if forceRefresh)
                // For now, simplified: if not local, it's null.
                // TODO: Implement remote fetch and cache update for getOrderDetails
                Result.Success(null)
            }
        }.flowOn(Dispatchers.IO)
        // TODO: Add .catch
    }

    override fun getOrdersForUser(userId: String): Flow<Result<List<Order>>> {
        return orderDao.getOrdersWithItemsForUser(userId).map { list ->
            Result.Success(list.map { mapOrderWithItemsToDomain(it) })
        }.flowOn(Dispatchers.IO)
        // TODO: Add .catch and potentially fetch from remote and update cache
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<Unit> = withContext(Dispatchers.IO) {
        // This is a simplified update. A real app would have more robust logic.
        // It should also update the remote data source.
        try {
            val statusEnum = OrderStatus.valueOf(newStatus) // Ensure valid status
            val now = System.currentTimeMillis()
            orderDao.updateOrderStatus(orderId, statusEnum.name, now)

            // Also mark for sync and attempt remote update
            val orderEntity = orderDao.getOrderWithItemsById(orderId).map { it?.order }. LATER: This is flow, need to get value
            // For now, just updating locally. Remote update + sync flag needs more careful handling here.
            // TODO: Fetch order, update status, set needsSync=true, save locally, attempt remote update.

            remoteDataSource.updateOrderStatus(orderId, newStatus) // Attempt remote update
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun cancelOrder(orderId: String, reason: String?): Result<Unit> = withContext(Dispatchers.IO) {
        // Similar to updateOrderStatus, needs robust handling for local and remote.
        try {
            val now = System.currentTimeMillis()
            orderDao.updateOrderStatus(orderId, OrderStatus.CANCELLED_BY_USER.name, now)
            // TODO: Fetch order, update status, set needsSync=true, save locally, attempt remote update.
            remoteDataSource.cancelOrder(orderId) // Attempt remote update
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


    // --- Mappers ---
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
