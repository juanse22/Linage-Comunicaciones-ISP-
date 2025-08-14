@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.Linageisp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Linageisp.ui.theme.*
import com.example.Linageisp.viewmodel.*
import kotlinx.coroutines.delay

/**
 * Pantalla del Asistente de IA con Firebase Genkit integrado
 * Interfaz de chat moderna para el asistente virtual de Linage ISP
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantScreen(
    onNavigateBack: () -> Unit = {},
    aiViewModel: AIAssistantViewModel = viewModel()
) {
    val uiState by aiViewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // Auto-scroll al último mensaje
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            delay(100)
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    // Estructura principal con gradiente de fondo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A1A),
                        Color(0xFF0D0D0D)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header del chat
            ChatHeader(onNavigateBack = onNavigateBack)
            
            // Área de mensajes
            Box(
                modifier = Modifier.weight(1f)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(uiState.messages) { index, message ->
                        ChatMessageItem(
                            message = message,
                            isFirst = index == 0,
                            showTyping = uiState.isTyping && index == uiState.messages.lastIndex
                        )
                    }
                    
                    // Indicador de escribiendo
                    if (uiState.isTyping) {
                        item {
                            TypingIndicator()
                        }
                    }
                    
                    // Espaciado final
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
                
                // Botones de acción rápida (solo si el chat está vacío o es mensaje de bienvenida)
                if (uiState.messages.size <= 1) {
                    QuickActionsPanel(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        onQuickAction = { action ->
                            aiViewModel.sendQuickAction(action)
                        }
                    )
                }
            }
            
            // Input de mensaje
            ChatInputSection(
                onSendMessage = { message ->
                    aiViewModel.sendMessage(message)
                    keyboardController?.hide()
                },
                isLoading = uiState.isLoading
            )
        }
        
        // Mostrar error si existe
        uiState.errorMessage?.let { error ->
            ErrorSnackbar(
                message = error,
                onDismiss = { aiViewModel.clearError() }
            )
        }
    }
}

/**
 * Header del chat con título y acciones
 */
@Composable
private fun ChatHeader(onNavigateBack: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2D2D2D)
        ),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón de retroceso
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        LinageOrange.copy(alpha = 0.2f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = LinageOrange
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Avatar del asistente
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(LinageOrange, LinageOrangeDark)
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🤖",
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Información del asistente
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Asistente Linage",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Alimentado por Gemini 1.5 Flash",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = LinageOrange
                    )
                )
            }
            
            // Indicador en línea
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(SuccessGreen, CircleShape)
            )
        }
    }
}

/**
 * Item individual de mensaje del chat
 */
@Composable
private fun ChatMessageItem(
    message: ChatMessage,
    isFirst: Boolean = false,
    showTyping: Boolean = false
) {
    val isUser = message.isFromUser
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // Avatar del asistente
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(LinageOrange, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🤖",
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        // Burbuja del mensaje
        Card(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .animateContentSize(),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) LinageOrange else Color(0xFF2D2D2D)
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isUser) Color.White else Color.White,
                        lineHeight = 20.sp
                    )
                )
                
                // Timestamp
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTimestamp(message.timestamp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isUser) Color.White.copy(alpha = 0.7f) else LinageOrange.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = if (isUser) TextAlign.End else TextAlign.Start
                )
            }
        }
        
        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            // Avatar del usuario
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF4CAF50), Color(0xFF2E7D32))
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👤",
                    fontSize = 16.sp
                )
            }
        }
    }
}

/**
 * Indicador de que el asistente está escribiendo
 */
@Composable
private fun TypingIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar del asistente
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(LinageOrange, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🤖",
                fontSize = 16.sp
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Burbuja con animación
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2D2D2D)
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val infiniteTransition = rememberInfiniteTransition(label = "typing")
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 0.5f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot_$index"
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .background(LinageOrange, CircleShape)
                    )
                    
                    if (index < 2) {
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }
        }
    }
}

/**
 * Panel de acciones rápidas
 */
@Composable
private fun QuickActionsPanel(
    modifier: Modifier = Modifier,
    onQuickAction: (QuickAction) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2D2D2D).copy(alpha = 0.95f)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "⚡ Acciones rápidas",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(QuickAction.values()) { index, action ->
                    QuickActionButton(
                        action = action,
                        onClick = { onQuickAction(action) }
                    )
                }
            }
        }
    }
}

/**
 * Botón de acción rápida
 */
@Composable
private fun QuickActionButton(
    action: QuickAction,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = LinageOrange.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = "${action.emoji} ${action.label}",
            style = MaterialTheme.typography.labelMedium.copy(
                color = Color.White
            )
        )
    }
}

/**
 * Sección de input para escribir mensajes
 */
@Composable
private fun ChatInputSection(
    onSendMessage: (String) -> Unit,
    isLoading: Boolean
) {
    var messageText by remember { mutableStateOf("") }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2D2D2D)
        ),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Campo de texto
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 56.dp, max = 120.dp),
                placeholder = {
                    Text(
                        text = "Escribe tu mensaje...",
                        color = Color.White.copy(alpha = 0.6f)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LinageOrange,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = LinageOrange
                ),
                shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (messageText.isNotBlank() && !isLoading) {
                            onSendMessage(messageText)
                            messageText = ""
                        }
                    }
                ),
                maxLines = 4
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Botón de enviar
            FloatingActionButton(
                onClick = {
                    if (messageText.isNotBlank() && !isLoading) {
                        onSendMessage(messageText)
                        messageText = ""
                    }
                },
                modifier = Modifier.size(56.dp),
                containerColor = if (messageText.isNotBlank() && !isLoading) LinageOrange else Color.Gray,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 6.dp
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
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

/**
 * Snackbar de error
 */
@Composable
private fun ErrorSnackbar(
    message: String,
    onDismiss: () -> Unit
) {
    LaunchedEffect(message) {
        delay(5000)
        onDismiss()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = ErrorRed
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDismiss
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Formatear timestamp a texto legible
 */
private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60000 -> "Ahora"
        diff < 3600000 -> "${diff / 60000}m"
        diff < 86400000 -> "${diff / 3600000}h"
        else -> "${diff / 86400000}d"
    }
}