package com.example.Linageisp.presentation.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Linageisp.ai.models.*
import com.example.Linageisp.BuildConfig
import kotlinx.coroutines.delay
import java.time.format.DateTimeFormatter
import kotlin.random.Random

/**
 * Pantalla principal del chat con LINA
 * UI completa con burbujas de mensajes, typing indicator, quick actions y más
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val messages by viewModel.messages.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    val quickActions by viewModel.quickActions.collectAsState()
    val listState = rememberLazyListState()
    
    // Auto-scroll al último mensaje
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            delay(100) // Pequeño delay para animaciones
            listState.animateScrollToItem(messages.size - 1)
        }
    }
    
    Scaffold(
        topBar = {
            ChatTopBar(
                onNavigateBack = onNavigateBack,
                connectionState = ConnectionState.CONNECTED
            )
        },
        bottomBar = {
            ChatBottomBar(
                currentMessage = viewModel.currentMessage,
                onMessageChange = viewModel::updateCurrentMessage,
                onSendMessage = viewModel::sendMessage,
                isLoading = isTyping,
                suggestions = emptyList(),
                onSuggestionClick = viewModel::sendMessage
            )
        },
        floatingActionButton = {
            if (BuildConfig.DEBUG) {
                Column {
                    // Botón 1: Test IA
                    FloatingActionButton(
                        onClick = { viewModel.sendMessage("¿Eres una IA real?") },
                        modifier = Modifier.padding(bottom = 70.dp),
                        containerColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Icon(Icons.Default.Psychology, "Test IA")
                    }
                    
                    // Botón 2: Test Matemáticas
                    FloatingActionButton(
                        onClick = { 
                            val num1 = Random.nextInt(1000, 9999)
                            val num2 = Random.nextInt(100, 999)
                            viewModel.sendMessage("¿Cuánto es $num1 × $num2?")
                        },
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ) {
                        Icon(Icons.Default.Calculate, "Test Math")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Lista de mensajes
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = messages,
                    key = { it.id }
                ) { message ->
                    MessageBubble(
                        message = message,
                        onQuickActionClick = { action ->
                            viewModel.handleQuickAction(action)
                        },
                        modifier = Modifier.animateItemPlacement()
                    )
                }
                
                // Mostrar typing indicator si LINA está escribiendo
                if (isTyping) {
                    item {
                        TypingIndicator(
                            modifier = Modifier.animateItemPlacement()
                        )
                    }
                }
            }
            
            // Quick Actions
            if (quickActions.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    items(quickActions) { action ->
                        QuickActionChip(
                            action = action,
                            onClick = { viewModel.handleQuickAction(action) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Barra superior del chat con estado de conexión
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(
    onNavigateBack: () -> Unit,
    connectionState: ConnectionState
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar de LINA
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🤖",
                        fontSize = 20.sp
                    )
                }
                
                Column {
                    Text(
                        text = "LINA",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when (connectionState) {
                            ConnectionState.CONNECTED -> "En línea"
                            ConnectionState.CONNECTING -> "Conectando..."
                            ConnectionState.OFFLINE -> "Modo offline"
                            ConnectionState.ERROR -> "Error de conexión"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = when (connectionState) {
                            ConnectionState.CONNECTED -> Color(0xFF4CAF50)
                            ConnectionState.CONNECTING -> MaterialTheme.colorScheme.primary
                            ConnectionState.OFFLINE -> Color(0xFFFF9800)
                            ConnectionState.ERROR -> MaterialTheme.colorScheme.error
                        }
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver"
                )
            }
        }
    )
}

/**
 * Burbuja de mensaje con soporte para markdown y quick actions
 */
@Composable
private fun MessageBubble(
    message: ChatMessage,
    onQuickActionClick: (QuickAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalAlignment = if (message.isFromUser) {
            Alignment.End
        } else {
            Alignment.Start
        }
    ) {
        // Burbuja principal del mensaje
        Card(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isFromUser) 16.dp else 4.dp,
                        bottomEnd = if (message.isFromUser) 4.dp else 16.dp
                    )
                ),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isFromUser) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Contenido del mensaje con soporte básico para markdown
                MessageContent(
                    content = message.content,
                    isFromUser = message.isFromUser,
                    isTyping = message.isTyping
                )
                
                // Timestamp
                Text(
                    text = message.timestamp.format(DateTimeFormatter.ofPattern("HH:mm")),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (message.isFromUser) {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    },
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        
        // Quick Actions (solo para mensajes de LINA)
        if (!message.isFromUser && message.quickActions.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(start = 8.dp, top = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(message.quickActions) { action ->
                    QuickActionChip(
                        action = action,
                        onClick = { onQuickActionClick(action) }
                    )
                }
            }
        }
    }
}

/**
 * Contenido del mensaje con soporte básico para markdown
 */
@Composable
private fun MessageContent(
    content: String,
    isFromUser: Boolean,
    isTyping: Boolean
) {
    val textColor = if (isFromUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    if (isTyping) {
        // Efecto de typing con cursor parpadeante
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
            
            TypingCursor()
        }
    } else {
        // Procesamiento básico de markdown
        MarkdownText(
            text = content,
            color = textColor
        )
    }
}

/**
 * Procesamiento básico de markdown para negritas y listas
 */
@Composable
private fun MarkdownText(
    text: String,
    color: Color
) {
    val lines = text.split("\n")
    
    Column {
        lines.forEach { line ->
            when {
                line.startsWith("**") && line.endsWith("**") -> {
                    // Texto en negrita
                    Text(
                        text = line.removeSurrounding("**"),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
                line.startsWith("• ") || line.startsWith("- ") -> {
                    // Lista con bullet points
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodyMedium,
                        color = color,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                line.startsWith("# ") -> {
                    // Encabezado
                    Text(
                        text = line.removePrefix("# "),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = color,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                else -> {
                    // Texto normal
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodyMedium,
                        color = color
                    )
                }
            }
        }
    }
}

/**
 * Cursor parpadeante para efecto de typing
 */
@Composable
private fun TypingCursor() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_blink"
    )
    
    Text(
        text = "|",
        color = MaterialTheme.colorScheme.primary.copy(alpha = alpha),
        style = MaterialTheme.typography.bodyMedium
    )
}

/**
 * Chip para quick actions
 */
@Composable
private fun QuickActionChip(
    action: QuickAction,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                action.icon?.let { icon ->
                    Text(text = icon, fontSize = 12.sp)
                }
                Text(
                    text = action.text,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        modifier = Modifier.height(32.dp)
    )
}

/**
 * Indicador de typing animado
 */
@Composable
private fun TypingIndicator(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LINA está escribiendo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                
                // Puntos animados
                repeat(3) { index ->
                    TypingDot(delay = index * 200)
                }
            }
        }
    }
}

/**
 * Punto animado para typing indicator
 */
@Composable
private fun TypingDot(delay: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing_dot")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = delay),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_scale"
    )
    
    Box(
        modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = scale))
    )
}

/**
 * Barra inferior con input y sugerencias
 */
@Composable
private fun ChatBottomBar(
    currentMessage: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    isLoading: Boolean,
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    
    Column {
        // Sugerencias rápidas
        if (suggestions.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(suggestions) { suggestion ->
                    SuggestionChip(
                        text = suggestion,
                        onClick = { onSuggestionClick(suggestion) }
                    )
                }
            }
        }
        
        // Input de mensaje
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = currentMessage,
                    onValueChange = onMessageChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = "Escribe tu mensaje...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (currentMessage.isNotBlank() && !isLoading) {
                                onSendMessage(currentMessage.trim())
                                focusManager.clearFocus()
                            }
                        }
                    ),
                    enabled = !isLoading
                )
                
                // Botón de envío
                FloatingActionButton(
                    onClick = {
                        if (currentMessage.isNotBlank() && !isLoading) {
                            onSendMessage(currentMessage.trim())
                            focusManager.clearFocus()
                        }
                    },
                    modifier = Modifier.size(48.dp),
                    containerColor = if (currentMessage.isNotBlank() && !isLoading) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Enviar mensaje",
                            tint = if (currentMessage.isNotBlank()) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Chip para sugerencias
 */
@Composable
private fun SuggestionChip(
    text: String,
    onClick: () -> Unit
) {
    androidx.compose.material3.SuggestionChip(
        onClick = onClick,
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall
            )
        },
        modifier = Modifier.height(32.dp)
    )
}

/**
 * Mensaje de error
 */
@Composable
private fun ErrorMessage(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TextButton(onClick = onRetry) {
                Text("Reintentar")
            }
        }
    }
}