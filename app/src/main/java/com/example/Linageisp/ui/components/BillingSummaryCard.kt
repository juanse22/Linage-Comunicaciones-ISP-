@file:OptIn(ExperimentalAnimationApi::class)

package com.example.Linageisp.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.Linageisp.ui.theme.*
import java.text.NumberFormat
import java.util.*

data class BillInfo(
    val accountNumber: String,
    val currentAmount: Double,
    val dueDate: String,
    val status: BillStatus,
    val plan: String,
    val period: String
)

enum class BillStatus {
    PAID, PENDING, OVERDUE
}

data class PaymentMethod(
    val id: String,
    val type: PaymentType,
    val displayName: String,
    val isDefault: Boolean = false
)

enum class PaymentType {
    CREDIT_CARD, DEBIT_CARD, PSE, CASH
}

data class PaymentHistory(
    val id: String,
    val amount: Double,
    val date: String,
    val method: PaymentMethod,
    val status: PaymentStatus
)

enum class PaymentStatus {
    SUCCESS, PENDING, FAILED
}

@Composable
fun BillingSummaryCard(
    billInfo: BillInfo,
    onPayNow: () -> Unit,
    onDownloadBill: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (billInfo.status) {
        BillStatus.PAID -> SuccessGreen
        BillStatus.PENDING -> LinageOrange  
        BillStatus.OVERDUE -> ErrorRed
    }

    val statusText = when (billInfo.status) {
        BillStatus.PAID -> "Pagada"
        BillStatus.PENDING -> "Pendiente"
        BillStatus.OVERDUE -> "Vencida"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header with status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Factura Actual",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Cuenta: ${billInfo.accountNumber}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = LinageGray
                        )
                    )
                }
                
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = statusColor,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Amount section with large display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Total a Pagar",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = LinageGray
                    )
                )
                
                Text(
                    text = formatCurrency(billInfo.currentAmount),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        fontSize = 36.sp
                    )
                )
                
                Text(
                    text = "Vence: ${billInfo.dueDate}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = LinageGray
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Plan info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = LinageBackground
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = billInfo.plan,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Período: ${billInfo.period}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = LinageGray
                            )
                        )
                    }
                    
                    Icon(
                        imageVector = Icons.Filled.Wifi,
                        contentDescription = null,
                        tint = LinageOrange,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (billInfo.status != BillStatus.PAID) {
                    Button(
                        onClick = onPayNow,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LinageOrange
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Payment,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Pagar Ahora",
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                
                OutlinedButton(
                    onClick = onDownloadBill,
                    modifier = Modifier
                        .weight(if (billInfo.status != BillStatus.PAID) 0.7f else 1f)
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = LinageOrange
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, 
                        Brush.horizontalGradient(
                            colors = listOf(LinageOrange, LinageOrangeLight)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Download,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Descargar",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentHistoryCard(
    payment: PaymentHistory,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (payment.status) {
        PaymentStatus.SUCCESS -> SuccessGreen
        PaymentStatus.PENDING -> LinageOrange
        PaymentStatus.FAILED -> ErrorRed
    }
    
    val statusText = when (payment.status) {
        PaymentStatus.SUCCESS -> "Exitoso"
        PaymentStatus.PENDING -> "Pendiente"
        PaymentStatus.FAILED -> "Fallido"
    }
    
    val methodIcon = when (payment.method.type) {
        PaymentType.CREDIT_CARD, PaymentType.DEBIT_CARD -> Icons.Filled.CreditCard
        PaymentType.PSE -> Icons.Filled.AccountBalance
        PaymentType.CASH -> Icons.Filled.AttachMoney
    }

    Card(
        onClick = onCardClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Payment method icon
            Surface(
                modifier = Modifier.size(48.dp),
                color = LinageOrange.copy(alpha = 0.1f),
                shape = CircleShape
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = methodIcon,
                        contentDescription = null,
                        tint = LinageOrange,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Payment info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatCurrency(payment.amount),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    
                    Surface(
                        color = statusColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = statusText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = statusColor,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = payment.method.displayName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = LinageGray
                    )
                )
                
                Text(
                    text = payment.date,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = LinageGrayLight
                    )
                )
            }
            
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = LinageGrayLight
            )
        }
    }
}

@Composable
fun PaymentMethodCard(
    method: PaymentMethod,
    isSelected: Boolean = false,
    onCardClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val methodIcon = when (method.type) {
        PaymentType.CREDIT_CARD, PaymentType.DEBIT_CARD -> Icons.Filled.CreditCard
        PaymentType.PSE -> Icons.Filled.AccountBalance
        PaymentType.CASH -> Icons.Filled.AttachMoney
    }

    Card(
        onClick = onCardClick,
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = LinageOrange,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) LinageOrange.copy(alpha = 0.05f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                color = LinageOrange.copy(alpha = 0.1f),
                shape = CircleShape
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = methodIcon,
                        contentDescription = null,
                        tint = LinageOrange,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = method.displayName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                    
                    if (method.isDefault) {
                        Surface(
                            color = SuccessGreen.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Predeterminado",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SuccessGreen,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
                
                Text(
                    text = when (method.type) {
                        PaymentType.CREDIT_CARD -> "Tarjeta de Crédito"
                        PaymentType.DEBIT_CARD -> "Tarjeta Débito"
                        PaymentType.PSE -> "PSE"
                        PaymentType.CASH -> "Efectivo"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = LinageGray
                    )
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Seleccionado",
                        tint = LinageOrange
                    )
                }
                
                if (onDeleteClick != null) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = ErrorRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(java.util.Locale.Builder().setLanguage("es").setRegion("CO").build())
    return format.format(amount)
}