@file:OptIn(ExperimentalAnimationApi::class)

package com.example.Linageisp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.getValue
import com.example.Linageisp.R
import com.example.Linageisp.ui.components.*
import com.example.Linageisp.ui.theme.*

enum class BillingTab {
    CURRENT, HISTORY, METHODS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(BillingTab.CURRENT) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showMethodDialog by remember { mutableStateOf(false) }
    
    val currentBill = remember { getCurrentBill() }
    val paymentHistory = remember { getPaymentHistory() }
    val paymentMethods = remember { mutableStateListOf(*getPaymentMethods().toTypedArray()) }
    var selectedMethod by remember { mutableStateOf<PaymentMethod?>(paymentMethods.firstOrNull { it.isDefault }) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        LinageBackground,
                        Color.White
                    )
                )
            )
    ) {
        // Top bar
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.billing_title),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = LinageOrange
                    )
                )
            },
            actions = {
                IconButton(
                    onClick = { /* Notifications */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificaciones",
                        tint = LinageOrange
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // Tab row
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = Color.Transparent,
            contentColor = LinageOrange,
            indicator = { tabPositions ->
                if (selectedTab.ordinal < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                        color = LinageOrange
                    )
                }
            }
        ) {
            BillingTab.values().forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = {
                        Text(
                            text = when (tab) {
                                BillingTab.CURRENT -> stringResource(R.string.billing_current)
                                BillingTab.HISTORY -> stringResource(R.string.billing_history)
                                BillingTab.METHODS -> stringResource(R.string.billing_methods)
                            },
                            fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Tab content
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                slideInHorizontally { width -> width } + fadeIn() togetherWith
                        slideOutHorizontally { width -> -width } + fadeOut()
            },
            label = "billingTabContent"
        ) { tab ->
            when (tab) {
                BillingTab.CURRENT -> CurrentBillContent(
                    billInfo = currentBill,
                    onPayNow = { showPaymentDialog = true },
                    onDownloadBill = { /* Download bill */ }
                )
                BillingTab.HISTORY -> PaymentHistoryContent(
                    history = paymentHistory
                )
                BillingTab.METHODS -> PaymentMethodsContent(
                    methods = paymentMethods,
                    selectedMethod = selectedMethod,
                    onMethodSelected = { selectedMethod = it },
                    onAddMethod = { showMethodDialog = true },
                    onDeleteMethod = { method ->
                        paymentMethods.removeAll { it.id == method.id }
                    }
                )
            }
        }
    }

    // Payment dialog
    if (showPaymentDialog) {
        PaymentDialog(
            billInfo = currentBill,
            paymentMethods = paymentMethods,
            selectedMethod = selectedMethod,
            onMethodSelected = { selectedMethod = it },
            onConfirmPayment = { 
                // Process payment
                showPaymentDialog = false 
            },
            onDismiss = { showPaymentDialog = false }
        )
    }

    // Add payment method dialog
    if (showMethodDialog) {
        AddPaymentMethodDialog(
            onMethodAdded = { method ->
                paymentMethods.add(method)
                showMethodDialog = false
            },
            onDismiss = { showMethodDialog = false }
        )
    }
}

@Composable
private fun CurrentBillContent(
    billInfo: BillInfo,
    onPayNow: () -> Unit,
    onDownloadBill: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            BillingSummaryCard(
                billInfo = billInfo,
                onPayNow = onPayNow,
                onDownloadBill = onDownloadBill
            )
        }
        
        item {
            UsageChart(
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        item {
            BillBreakdownCard(
                billInfo = billInfo
            )
        }
    }
}

@Composable
private fun PaymentHistoryContent(
    history: List<PaymentHistory>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Historial de Pagos",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { /* Filter */ }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filtrar",
                            tint = LinageOrange
                        )
                    }
                    
                    IconButton(
                        onClick = { /* Export */ }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = "Exportar",
                            tint = LinageOrange
                        )
                    }
                }
            }
        }
        
        items(history) { payment ->
            PaymentHistoryCard(
                payment = payment,
                onCardClick = { /* Show payment details */ }
            )
        }
    }
}

@Composable
private fun PaymentMethodsContent(
    methods: List<PaymentMethod>,
    selectedMethod: PaymentMethod?,
    onMethodSelected: (PaymentMethod) -> Unit,
    onAddMethod: () -> Unit,
    onDeleteMethod: (PaymentMethod) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Métodos de Pago",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                FloatingActionButton(
                    onClick = onAddMethod,
                    containerColor = LinageOrange,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar método",
                        tint = Color.White
                    )
                }
            }
        }
        
        items(methods) { method ->
            PaymentMethodCard(
                method = method,
                isSelected = selectedMethod?.id == method.id,
                onCardClick = { onMethodSelected(method) },
                onDeleteClick = if (!method.isDefault) {
                    { onDeleteMethod(method) }
                } else null
            )
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = LinageBackground
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Security,
                            contentDescription = null,
                            tint = LinageOrange,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Seguridad de Pagos",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Todos tus pagos están protegidos con encriptación SSL de 256 bits y cumplimos con los estándares PCI DSS para la seguridad de datos de tarjetas.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = LinageGray,
                            lineHeight = 20.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun UsageChart(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Consumo del Mes",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Simplified usage visualization
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                UsageItem("Datos", "450 GB", "de 500 GB", 0.9f)
                UsageItem("TV", "180 hrs", "ilimitado", 1f)
                UsageItem("Llamadas", "320 min", "ilimitado", 1f)
            }
        }
    }
}

@Composable
private fun UsageItem(
    title: String,
    used: String,
    total: String,
    progress: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                color = LinageGray
            )
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = used,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
        
        Text(
            text = total,
            style = MaterialTheme.typography.labelSmall.copy(
                color = LinageGray
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.width(60.dp),
            color = if (progress > 0.8f) ErrorRed else LinageOrange,
            trackColor = LinageGrayLight.copy(alpha = 0.3f)
        )
    }
}

@Composable
private fun BillBreakdownCard(
    billInfo: BillInfo
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Detalle de Factura",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val breakdown = listOf(
                "Plan ${billInfo.plan}" to 45000.0,
                "Equipos" to 5000.0,
                "IVA (19%)" to 9500.0
            )
            
            breakdown.forEach { (concept, amount) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = concept,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = formatCurrency(amount),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Divider(color = LinageGrayLight.copy(alpha = 0.5f))
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = formatCurrency(billInfo.currentAmount),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = LinageOrange
                    )
                )
            }
        }
    }
}

// Mock data and dialogs would be implemented here...
// Continuing with simplified implementations due to length constraints

@Composable
private fun PaymentDialog(
    billInfo: BillInfo,
    paymentMethods: List<PaymentMethod>,
    selectedMethod: PaymentMethod?,
    onMethodSelected: (PaymentMethod) -> Unit,
    onConfirmPayment: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Confirmar Pago",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column {
                Text(
                    text = "Vas a pagar: ${formatCurrency(billInfo.currentAmount)}",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Método de pago:",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
                
                selectedMethod?.let { method ->
                    Text(
                        text = method.displayName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = LinageOrange
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmPayment,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LinageOrange
                )
            ) {
                Text("Confirmar Pago")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun AddPaymentMethodDialog(
    onMethodAdded: (PaymentMethod) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Agregar Método de Pago")
        },
        text = {
            Text("Esta funcionalidad se implementaría con un formulario completo para agregar nuevos métodos de pago.")
        },
        confirmButton = {
            Button(
                onClick = {
                    // Mock new method
                    val newMethod = PaymentMethod(
                        id = "new_${System.currentTimeMillis()}",
                        type = PaymentType.CREDIT_CARD,
                        displayName = "Visa **** 1234",
                        isDefault = false
                    )
                    onMethodAdded(newMethod)
                }
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun getCurrentBill(): BillInfo {
    return BillInfo(
        accountNumber = "1234567890",
        currentAmount = 59500.0,
        dueDate = "15 de Febrero, 2024",
        status = BillStatus.PENDING,
        plan = "Fibra Hogar 100 MB",
        period = "Enero 2024"
    )
}

private fun getPaymentHistory(): List<PaymentHistory> {
    val methods = getPaymentMethods()
    return listOf(
        PaymentHistory(
            id = "pay001",
            amount = 59500.0,
            date = "15 de Enero, 2024",
            method = methods[0],
            status = PaymentStatus.SUCCESS
        ),
        PaymentHistory(
            id = "pay002",
            amount = 59500.0,
            date = "15 de Diciembre, 2023",
            method = methods[1],
            status = PaymentStatus.SUCCESS
        ),
        PaymentHistory(
            id = "pay003",
            amount = 59500.0,
            date = "15 de Noviembre, 2023",
            method = methods[0],
            status = PaymentStatus.SUCCESS
        )
    )
}

private fun getPaymentMethods(): List<PaymentMethod> {
    return listOf(
        PaymentMethod(
            id = "card001",
            type = PaymentType.CREDIT_CARD,
            displayName = "Visa terminada en 1234",
            isDefault = true
        ),
        PaymentMethod(
            id = "pse001",
            type = PaymentType.PSE,
            displayName = "Banco de Bogotá",
            isDefault = false
        ),
        PaymentMethod(
            id = "cash001",
            type = PaymentType.CASH,
            displayName = "Pago en Efectivo",
            isDefault = false
        )
    )
}

private fun formatCurrency(amount: Double): String {
    return String.format("$%,.0f COP", amount)
}