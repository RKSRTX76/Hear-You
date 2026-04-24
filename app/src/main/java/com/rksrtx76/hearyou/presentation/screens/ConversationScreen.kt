package com.rksrtx76.hearyou.presentation.screens

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.rksrtx76.hearyou.data.model.ConversationLanguage
import com.rksrtx76.hearyou.presentation.components.ChatBubble
import com.rksrtx76.hearyou.presentation.components.LanguageSelector
import com.rksrtx76.hearyou.presentation.components.MicButton
import com.rksrtx76.hearyou.presentation.components.TranscriptBubble
import com.rksrtx76.hearyou.presentation.components.VoiceSelectorStrip
import com.rksrtx76.hearyou.presentation.viewmodel.ConversationViewModel
import com.rksrtx76.hearyou.presentation.viewmodel.VoiceState
import com.rksrtx76.hearyou.ui.theme.Amber
import com.rksrtx76.hearyou.ui.theme.MicActive
import com.rksrtx76.hearyou.ui.theme.MicSpeaking
import com.rksrtx76.hearyou.ui.theme.Midnight
import com.rksrtx76.hearyou.ui.theme.TextMuted
import com.rksrtx76.hearyou.ui.theme.TextPrimary

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ConversationScreen(
    viewModel: ConversationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    // Microphone permission
    val micPermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    // Auto scroll to bottom when new messages
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Midnight)
    ) {
        // Subtle ambient gradient orb
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(x = (-80).dp, y = (-60).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Amber.copy(alpha = 0.04f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            TopBar(
                voiceName        = uiState.selectedVoice.name,
                selectedLanguage = uiState.selectedLanguage,
                onLanguageSelect = viewModel::selectLanguage,
                onClearChat      = viewModel::clearConversation,
                messageCount     = uiState.messages.size
            )

            // Messages
            Box(modifier = Modifier.weight(1f)) {
                if (uiState.messages.isEmpty()) {
                    EmptyState(
                        voiceName = uiState.selectedVoice.name,
                        description = uiState.selectedVoice.description,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(
                            start  = 16.dp,
                            end    = 16.dp,
                            top    = 12.dp,
                            bottom = 24.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.messages, key = { it.id }) { message ->
                            ChatBubble(message = message)
                        }
                    }
                }

                // Fade overlay at bottom of message list
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Midnight)
                            )
                        )
                )
            }

            AnimatedVisibility(
                visible = uiState.voiceState is VoiceState.Listening ||
                        uiState.voiceState is VoiceState.Processing ||
                        uiState.voiceState is VoiceState.Speaking,
                enter = fadeIn() + expandVertically(),
                exit  = fadeOut() + shrinkVertically()
            ) {
                StatusIndicator(
                    voiceState = uiState.voiceState,
                    transcript = uiState.currentTranscript,
                    voiceName  = uiState.selectedVoice.name,
                    modifier   = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )
            }

            // Error
            AnimatedVisibility(visible = uiState.errorMessage != null) {
                Text(
                    text     = uiState.errorMessage ?: "",
                    color    = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 4.dp)
                )
            }

            // Voice selector
            VoiceSelectorStrip(
                voices        = uiState.availableVoices,
                selectedVoice = uiState.selectedVoice,
                onVoiceSelect = viewModel::selectVoice,
                modifier      = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )

            // Mic button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                MicButton(
                    voiceState = uiState.voiceState,
                    onMicClick = {
                        when (uiState.voiceState) {
                            is VoiceState.Listening -> viewModel.stopListening()
                            is VoiceState.Idle      -> {
                                if (micPermission.status.isGranted) {
                                    viewModel.startListening()
                                } else {
                                    micPermission.launchPermissionRequest()
                                }
                            }
                            else -> Unit
                        }
                    }
                )
            }
        }
    }
}


@Composable
private fun TopBar(
    voiceName: String,
    selectedLanguage: ConversationLanguage,
    onLanguageSelect: (ConversationLanguage) -> Unit,
    onClearChat: () -> Unit,
    messageCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App name + current voice
        Column {
            Text(
                text       = "HearYou",
                fontSize   = 20.sp,
                fontWeight = FontWeight.W700,
                color      = TextPrimary,
                letterSpacing = (-0.5).sp
            )
            Text(
                text     = "with $voiceName",
                fontSize = 12.sp,
                color    = Amber
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clear chats if exists
            AnimatedVisibility(visible = messageCount > 0) {
                IconButton(onClick = onClearChat) {
                    Icon(
                        Icons.Filled.DeleteOutline,
                        contentDescription = "Clear conversation",
                        tint   = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Language selector
            LanguageSelector(
                selected = selectedLanguage,
                onSelect = onLanguageSelect
            )
        }
    }
}

@Composable
private fun EmptyState(
    voiceName: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text       = "Hi, I'm $voiceName",
            fontSize   = 28.sp,
            fontWeight = FontWeight.W300,
            color      = TextPrimary,
            letterSpacing = (-0.5).sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text     = description,
            fontSize = 14.sp,
            color    = Amber
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text      = "Tap the mic and start talking",
            fontSize  = 13.sp,
            color     = TextMuted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StatusIndicator(
    voiceState: VoiceState,
    transcript: String,
    voiceName: String,
    modifier: Modifier = Modifier
) {
    val (label, color) = when (voiceState) {
        is VoiceState.Listening  -> Pair("Listening…", MicActive)
        is VoiceState.Processing -> Pair("$voiceName is thinking…", Amber)
        is VoiceState.Speaking   -> Pair("$voiceName is speaking", MicSpeaking)
        else                     -> Pair("", TextMuted)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text     = label,
            fontSize = 12.sp,
            color    = color,
            fontWeight = FontWeight.W500
        )

        if (transcript.isNotBlank()) {
            TranscriptBubble(transcript = transcript)
        }
    }
}
