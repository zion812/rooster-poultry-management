package com.example.rooster.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Date

// Order status with real-time tracking
enum class OrderStatus(
    val displayName: String,
    val displayNameTelugu: String,
    val color: Color,
    val icon: ImageVector,
    val stepNumber: Int,
    val isActive: Boolean = true,
) {
    PENDING_PAYMENT(
        "Pending Payment",
        "చెల్లింపు పెండింగ్",
        Color(0xFFF59E0B),
        Icons.Default.Payment,
        1,
    ),
    PAYMENT_CONFIRMED(
        "Payment Confirmed",
        "చెల్లింపు నిర్ధారించబడింది",
        Color(0xFF059669),
        Icons.Default.CheckCircle,
        2,
    ),
    ORDER_CONFIRMED(
        "Order Confirmed",
        "ఆర్డర్ నిర్ధారించబడింది",
        Color(0xFF3B82F6),
        Icons.Default.Assignment,
        3,
    ),
    PREPARING(
        "Preparing",
        "సిద్ధం చేస్తున్నారు",
        Color(0xFF8B5CF6),
        Icons.Default.Build,
        4,
    ),
    READY_FOR_PICKUP(
        "Ready for Pickup",
        "తీసుకోవడానికి సిద్ధం",
        Color(0xFF10B981),
        Icons.Default.CheckBox,
        5,
    ),
    IN_TRANSIT(
        "In Transit",
        "రవాణాలో",
        Color(0xFF6366F1),
        Icons.Default.LocalShipping,
        6,
    ),
    OUT_FOR_DELIVERY(
        "Out for Delivery",
        "డెలివరీకి బయలుదేరింది",
        Color(0xFFEC4899),
        Icons.Default.DeliveryDining,
        7,
    ),
    DELIVERED(
        "Delivered",
        "డెలివరీ అయింది",
        Color(0xFF059669),
        Icons.Default.Done,
        8,
        false,
    ),
    CANCELLED(
        "Cancelled",
        "రద్దు చేయబడింది",
        Color(0xFFEF4444),
        Icons.Default.Cancel,
        0,
        false,
    ),
    REFUNDED(
        "Refunded",
        "రీఫండ్ చేయబడింది",
        Color(0xFF6B7280),
        Icons.Default.MoneyOff,
        0,
        false,
    ),
}

// Payment methods supported
enum class PaymentMethod(
    val displayName: String,
    val displayNameTelugu: String,
    val icon: ImageVector,
    val isEnabled: Boolean = true,
) {
    CASH_ON_DELIVERY(
        "Cash on Delivery",
        "డెలివరీ వేళ చెల్లింపు",
        Icons.Default.Money,
        true,
    ),
    UPI(
        "UPI Payment",
        "UPI చెల్లింపు",
        Icons.Default.QrCode,
        true,
    ),
    CARD(
        "Card Payment",
        "కార్డ్ చెల్లింపు",
        Icons.Default.CreditCard,
        true,
    ),
    NET_BANKING(
        "Net Banking",
        "నెట్ బ్యాంకింగ్",
        Icons.Default.AccountBalance,
        true,
    ),
    WALLET(
        "Mobile Wallet",
        "మొబైల్ వాలెట్",
        Icons.Default.Wallet,
        true,
    ),
}

// Delivery options
enum class DeliveryOption(
    val displayName: String,
    val displayNameTelugu: String,
    val icon: ImageVector,
    val estimatedDays: Int,
    val cost: Double = 0.0,
) {
    PICKUP(
        "Self Pickup",
        "స్వయంగా తీసుకోవడం",
        Icons.Default.DirectionsWalk,
        0,
        0.0,
    ),
    LOCAL_DELIVERY(
        "Local Delivery",
        "స్థానిక డెలివరీ",
        Icons.Default.LocalShipping,
        1,
        50.0,
    ),
    EXPRESS_DELIVERY(
        "Express Delivery",
        "ఎక్స్‌ప్రెస్ డెలివరీ",
        Icons.Default.FlightTakeoff,
        1,
        150.0,
    ),
    STANDARD_DELIVERY(
        "Standard Delivery",
        "సాధారణ డెలివరీ",
        Icons.Default.LocalShipping,
        3,
        100.0,
    ),
}

// Streamlined checkout process
data class CheckoutStep(
    val stepNumber: Int,
    val title: String,
    val titleTelugu: String,
    val isCompleted: Boolean = false,
    val isActive: Boolean = false,
)

object CheckoutFlowHelper {
    fun getCheckoutSteps(): List<CheckoutStep> =
        listOf(
            CheckoutStep(1, "Review Items", "వస్తువులు సమీక్షించండి"),
            CheckoutStep(2, "Delivery Info", "డెలివరీ సమాచారం"),
            CheckoutStep(3, "Payment", "చెల్లింపు"),
            CheckoutStep(4, "Confirmation", "నిర్ధారణ"),
        )

    fun updateStepStatus(
        steps: List<CheckoutStep>,
        activeStep: Int,
    ): List<CheckoutStep> {
        return steps.mapIndexed { index, step ->
            step.copy(
                isCompleted = index < activeStep - 1,
                isActive = index == activeStep - 1,
            )
        }
    }

    fun calculateOrderTotal(
        itemTotal: Double,
        deliveryOption: DeliveryOption,
        taxRate: Double = 0.05, // 5% tax
        discountAmount: Double = 0.0,
    ): OrderTotal {
        val subtotal = itemTotal
        val deliveryCharges = deliveryOption.cost
        val taxAmount = (subtotal + deliveryCharges) * taxRate
        val total = subtotal + deliveryCharges + taxAmount - discountAmount

        return OrderTotal(
            subtotal = subtotal,
            deliveryCharges = deliveryCharges,
            taxAmount = taxAmount,
            discountAmount = discountAmount,
            total = total,
        )
    }
}

data class OrderTotal(
    val subtotal: Double,
    val deliveryCharges: Double,
    val taxAmount: Double,
    val discountAmount: Double,
    val total: Double,
)

// Order tracking with real-time updates
data class OrderTrackingInfo(
    val orderId: String,
    val currentStatus: OrderStatus,
    val statusHistory: List<OrderStatusUpdate>,
    val estimatedDelivery: Date?,
    val trackingNumber: String? = null,
    val deliveryAddress: String,
    val contactNumber: String,
    val specialInstructions: String? = null,
)

data class OrderStatusUpdate(
    val status: OrderStatus,
    val timestamp: Date,
    val location: String? = null,
    val notes: String? = null,
    val updatedBy: String? = null,
)

// Order items with details
data class OrderItem(
    val id: String,
    val listingId: String,
    val name: String,
    val nameTelugu: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double,
    val imageUrl: String? = null,
    val sellerName: String,
    val specifications: Map<String, String> = emptyMap(),
)

// Complete order data model
data class Order(
    val orderId: String,
    val buyerId: String,
    val items: List<OrderItem>,
    val orderTotal: OrderTotal,
    val paymentMethod: PaymentMethod,
    val deliveryOption: DeliveryOption,
    val deliveryAddress: String,
    val contactNumber: String,
    val currentStatus: OrderStatus,
    val createdAt: Date,
    val updatedAt: Date,
    val estimatedDelivery: Date?,
    val specialInstructions: String? = null,
    val trackingInfo: OrderTrackingInfo? = null,
)

// Progress indicator for checkout
@Composable
fun CheckoutProgressIndicator(
    steps: List<CheckoutStep>,
    currentStep: Int,
    isTeluguMode: Boolean = false,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        steps.forEachIndexed { index, step ->
            CheckoutStepIndicator(
                step =
                    step.copy(
                        isCompleted = index < currentStep - 1,
                        isActive = index == currentStep - 1,
                    ),
                isTeluguMode = isTeluguMode,
            )

            // Add connector line between steps (except for the last step)
            if (index < steps.size - 1) {
                Box(
                    modifier =
                        Modifier
                            .height(2.dp)
                            .weight(1f)
                            .background(
                                if (index < currentStep - 1) Color(0xFF059669) else Color(0xFFE5E7EB),
                            ),
                )
            }
        }
    }
}

@Composable
private fun CheckoutStepIndicator(
    step: CheckoutStep,
    isTeluguMode: Boolean,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        when {
                            step.isCompleted -> Color(0xFF059669)
                            step.isActive -> Color(0xFF3B82F6)
                            else -> Color(0xFFE5E7EB)
                        },
                    ),
            contentAlignment = Alignment.Center,
        ) {
            if (step.isCompleted) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            } else {
                Text(
                    text = step.stepNumber.toString(),
                    color = if (step.isActive) Color.White else Color(0xFF6B7280),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isTeluguMode) step.titleTelugu else step.title,
            fontSize = 12.sp,
            color =
                when {
                    step.isCompleted || step.isActive -> Color(0xFF111827)
                    else -> Color(0xFF6B7280)
                },
            fontWeight = if (step.isActive) FontWeight.Medium else FontWeight.Normal,
        )
    }
}

// Order status timeline
@Composable
fun OrderStatusTimeline(
    trackingInfo: OrderTrackingInfo,
    isTeluguMode: Boolean = false,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        Text(
            text = if (isTeluguMode) "ఆర్డర్ ట్రాకింగ్" else "Order Tracking",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827),
        )

        Spacer(modifier = Modifier.height(16.dp))

        trackingInfo.statusHistory.sortedByDescending { it.timestamp }
            .forEachIndexed { index, update ->
                OrderStatusTimelineItem(
                    update = update,
                    isLatest = index == 0,
                    isTeluguMode = isTeluguMode,
                )

                if (index < trackingInfo.statusHistory.size - 1) {
                    Box(
                        modifier =
                            Modifier
                                .width(2.dp)
                                .height(24.dp)
                                .background(Color(0xFFE5E7EB))
                                .padding(start = 15.dp),
                    )
                }
            }
    }
}

@Composable
private fun OrderStatusTimelineItem(
    update: OrderStatusUpdate,
    isLatest: Boolean,
    isTeluguMode: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier =
                Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isLatest) update.status.color else Color(0xFFE5E7EB)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = update.status.icon,
                contentDescription = null,
                tint = if (isLatest) Color.White else Color(0xFF6B7280),
                modifier = Modifier.size(20.dp),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isTeluguMode) update.status.displayNameTelugu else update.status.displayName,
                fontSize = 16.sp,
                fontWeight = if (isLatest) FontWeight.Bold else FontWeight.Medium,
                color = if (isLatest) Color(0xFF111827) else Color(0xFF6B7280),
            )

            Text(
                text = formatDateTime(update.timestamp),
                fontSize = 12.sp,
                color = Color(0xFF6B7280),
            )

            update.location?.let { location ->
                Text(
                    text = location,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                )
            }

            update.notes?.let { notes ->
                Text(
                    text = notes,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                )
            }
        }
    }
}

// Utility function to format date time
private fun formatDateTime(date: Date): String {
    // This would use proper date formatting based on locale
    return date.toString() // Simplified for now
}
