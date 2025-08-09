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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.sp
import com.example.Linageisp.R
import com.example.Linageisp.ui.components.*
import com.example.Linageisp.ui.theme.*
import kotlinx.coroutines.delay

data class FAQItem(
    val question: String,
    val answer: String,
    val category: String = "General"
)

data class SupportTicket(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val priority: String
)

enum class SupportTab {
    CHAT, FAQ, TICKETS, CONTACT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechnicalSupportScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(SupportTab.CHAT) }
    
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
                    text = stringResource(R.string.support_title),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = LinageOrange
                    )
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // Tab row
        ScrollableTabRow(
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
            SupportTab.values().forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = {
                        Text(
                            text = when (tab) {
                                SupportTab.CHAT -> stringResource(R.string.support_chat)
                                SupportTab.FAQ -> stringResource(R.string.support_faq)
                                SupportTab.TICKETS -> "Tickets"
                                SupportTab.CONTACT -> "Contacto"
                            },
                            fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Tab content
        when (selectedTab) {
            SupportTab.CHAT -> ChatContent()
            SupportTab.FAQ -> FAQContent()
            SupportTab.TICKETS -> TicketsContent()
            SupportTab.CONTACT -> ContactContent()
        }
    }
}

@Composable
private fun ChatContent(
    modifier: Modifier = Modifier
) {
    var messages by remember { mutableStateOf(getInitialMessages()) }
    var inputText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Messages
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(messages) { message ->
                ChatBubble(message = message)
            }
            
            if (isTyping) {
                item {
                    TypingIndicator()
                }
            }
        }

        // Input area
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp,
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = "Escribe tu mensaje...",
                            color = LinageGray
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LinageOrange,
                        cursorColor = LinageOrange
                    ),
                    shape = RoundedCornerShape(24.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (inputText.isNotBlank()) {
                                messages = messages + ChatMessage(inputText.trim(), isUser = true)
                                inputText = ""
                                keyboardController?.hide()
                                
                                // Simulate bot response
                                isTyping = true
                                
                                val responses = listOf(
                                    "Entiendo tu consulta. Te ayudo con eso de inmediato.",
                                    "Gracias por contactarnos. Estamos revisando tu solicitud.",
                                    "He registrado tu consulta. Te proporcionaré una solución.",
                                    "Perfecto, voy a verificar esa información para ti."
                                )
                                
                                // Add bot response after delay
                                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                    delay(2000)
                                    isTyping = false
                                    messages = messages + ChatMessage(
                                        responses.random(),
                                        isUser = false
                                    )
                                }
                            }
                        }
                    )
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                FloatingActionButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            messages = messages + ChatMessage(inputText.trim(), isUser = true)
                            inputText = ""
                            keyboardController?.hide()
                            
                            // Simulate bot response
                            isTyping = true
                            
                            val responses = listOf(
                                "Entiendo tu consulta. Te ayudo con eso de inmediato.",
                                "Gracias por contactarnos. Estamos revisando tu solicitud.",
                                "He registrado tu consulta. Te proporcionaré una solución.",
                                "Perfecto, voy a verificar esa información para ti."
                            )
                            
                            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                delay(2000)
                                isTyping = false
                                messages = messages + ChatMessage(
                                    responses.random(),
                                    isUser = false
                                )
                            }
                        }
                    },
                    containerColor = LinageOrange,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun FAQContent(
    modifier: Modifier = Modifier
) {
    val faqItems = getFAQItems()
    var expandedItems by remember { mutableStateOf(setOf<Int>()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(faqItems.size) { index ->
            val item = faqItems[index]
            val isExpanded = expandedItems.contains(index)
            
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickableWithoutRipple {
                                expandedItems = if (isExpanded) {
                                    expandedItems - index
                                } else {
                                    expandedItems + index
                                }
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.question,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        
                        Icon(
                            imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = null,
                            tint = LinageOrange
                        )
                    }
                    
                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = item.answer,
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
    }
}

@Composable
private fun TicketsContent(
    modifier: Modifier = Modifier
) {
    val tickets = getMockTickets()

    LazyColumn(
        modifier = modifier
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
                    text = "Mis Tickets de Soporte",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                FloatingActionButton(
                    onClick = { /* Create new ticket */ },
                    containerColor = LinageOrange,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nuevo ticket",
                        tint = Color.White
                    )
                }
            }
        }
        
        items(tickets) { ticket ->
            SupportTicketCard(
                title = ticket.title,
                description = ticket.description,
                status = ticket.status,
                priority = ticket.priority,
                onCardClick = { /* Open ticket details */ }
            )
        }
    }
}

@Composable
private fun ContactContent(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Contacta con Soporte",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
        
        item {
            ContactCard(
                title = stringResource(R.string.support_whatsapp),
                subtitle = "+57 300 123 4567",
                icon = Icons.Default.Message,
                color = Color(0xFF25D366),
                onClick = { /* Open WhatsApp */ }
            )
        }
        
        item {
            ContactCard(
                title = stringResource(R.string.support_phone),
                subtitle = "(601) 123-4567",
                icon = Icons.Default.Phone,
                color = LinageOrange,
                onClick = { /* Make phone call */ }
            )
        }
        
        item {
            ContactCard(
                title = "Email",
                subtitle = "soporte@linageisp.com",
                icon = Icons.Default.Email,
                color = Color(0xFF1976D2),
                onClick = { /* Send email */ }
            )
        }
        
        item {
            ContactCard(
                title = "Horario de Atención",
                subtitle = "Lunes a Viernes: 8:00 AM - 6:00 PM\nSábados: 9:00 AM - 2:00 PM",
                icon = Icons.Default.Schedule,
                color = LinageGray,
                onClick = { }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
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
            Surface(
                modifier = Modifier.size(48.dp),
                color = color.copy(alpha = 0.1f),
                shape = CircleShape
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = LinageGray,
                        lineHeight = 18.sp
                    )
                )
            }
            
            if (onClick != {}) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = LinageGray
                )
            }
        }
    }
}

@Composable
private fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier {
    return this.clip(RoundedCornerShape(12.dp))
        .background(Color.Transparent)
        .then(
            Modifier.clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
        )
}

private fun getInitialMessages(): List<ChatMessage> {
    return listOf(
        ChatMessage(
            text = "¡Hola! Soy el asistente virtual de Linage ISP. ¿En qué puedo ayudarte hoy?",
            isUser = false
        )
    )
}

private fun getFAQItems(): List<FAQItem> {
    return listOf(
        FAQItem(
            question = "¿Cómo puedo reportar una falla en mi servicio?",
            answer = "Puedes reportar fallas a través de esta aplicación, llamando al (601) 123-4567, o enviando un mensaje por WhatsApp al +57 300 123 4567. Nuestro equipo técnico te asistirá de inmediato.",
            category = "Soporte Técnico"
        ),
        FAQItem(
            question = "¿Cuál es la velocidad real de mi plan de internet?",
            answer = "La velocidad puede variar según varios factores. Usa nuestro test de velocidad integrado en la app para verificar tu velocidad actual. Si hay diferencias significativas, contacta soporte técnico.",
            category = "Velocidad"
        ),
        FAQItem(
            question = "¿Cómo puedo cambiar mi plan de internet?",
            answer = "Puedes cambiar tu plan desde la sección 'Planes' de esta aplicación, llamándonos, o visitando nuestras oficinas. Los cambios generalmente se aplican en el siguiente ciclo de facturación.",
            category = "Planes"
        ),
        FAQItem(
            question = "¿Qué hacer si no tengo internet?",
            answer = "Primero, verifica que todos los cables estén conectados correctamente y reinicia tu módem/router. Si persiste el problema, contacta soporte técnico inmediatamente.",
            category = "Soporte Técnico"
        ),
        FAQItem(
            question = "¿Cómo puedo pagar mi factura?",
            answer = "Puedes pagar en línea a través de nuestra app, en bancos corresponsales, PSE, o en nuestras oficinas. También ofrecemos débito automático para tu comodidad.",
            category = "Facturación"
        ),
        FAQItem(
            question = "¿Cuándo vence mi factura?",
            answer = "La fecha de vencimiento aparece en tu factura y en la sección 'Facturación' de esta app. Generalmente es el mismo día cada mes según tu fecha de instalación.",
            category = "Facturación"
        )
    )
}

private fun getMockTickets(): List<SupportTicket> {
    return listOf(
        SupportTicket(
            id = "TK001",
            title = "Velocidad lenta en horas pico",
            description = "Durante las noches la velocidad de internet baja considerablemente",
            status = "En progreso",
            priority = "Media"
        ),
        SupportTicket(
            id = "TK002",
            title = "Cortes intermitentes de servicio",
            description = "El servicio se corta cada 2-3 horas por unos minutos",
            status = "Abierto",
            priority = "Alta"
        ),
        SupportTicket(
            id = "TK003",
            title = "Consulta sobre cambio de plan",
            description = "Quiero cambiar a un plan superior, ¿cuáles son las opciones?",
            status = "Resuelto",
            priority = "Baja"
        )
    )
}