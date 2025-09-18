package com.rksrtx76.hearyou.presentation

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rksrtx76.hearyou.ChatUiEvents
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(){
    val chatViewModel = hiltViewModel<ChatViewModel>()
    val chatState = chatViewModel.chatState.collectAsState().value
    val voiceViewModel = hiltViewModel<VoiceToTextViewModel>()
    val textViewModel = hiltViewModel<TextToSpeechViewModel>()
    val voiceState = voiceViewModel.state.collectAsState().value
    var canRecord by remember {
        mutableStateOf(false)
    }



    // permission request
    val recordAudioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted->
            canRecord = isGranted
        }
    )

    LaunchedEffect(recordAudioLauncher) {
        recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }



    LaunchedEffect(chatState.response) {
        if (chatState.response.isNotEmpty()) {
            Log.d("MainScreen", "AI Response: ${chatState.response}")
            textViewModel.speak(chatState.response)  // default en-US
        }
    }

    // Log spoken text when it changes
    LaunchedEffect(voiceState.spokenText) {
        if (voiceState.spokenText.isNotEmpty()) {
            Log.d("MainScreen", "User Spoken Text: ${voiceState.spokenText}")
        }
    }
    LaunchedEffect(Unit) {
        voiceViewModel.onResultCallback = { spokenText ->
            Log.d("MainScreen", "Auto sending to Gemini: $spokenText")
            chatViewModel.onEvent(ChatUiEvents.SendPrompt(spokenText))
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // glowing border when speaking

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (chatState.isLoading) {
                ModelChatLoading()
            }
        }

        EdgeGlowOverlay(isSpeaking = voiceState.isSpeaking)

        FloatingActionButton(
            onClick = {
                if (canRecord) {
                    if (!voiceState.isSpeaking) {
                        voiceViewModel.startListening("en-US")
                    } else {
                        voiceViewModel.stopListening()
                        chatViewModel.onEvent(ChatUiEvents.SendPrompt(voiceState.spokenText))
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(42.dp)
        ) {
            AnimatedContent(targetState = voiceState.isSpeaking) { isSpeaking ->
                if (isSpeaking) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = null
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
fun ModelChat(response : String){
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(response.isNotEmpty()) {
            Text(text = response, color = Color.White)
        }
    }
}






@Composable
fun ModelChatLoading() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    BouncingDot(delayMillis = index * 100)
                }
            }
        }
    }
}

@Composable
fun BouncingDot(delayMillis: Int) {
    val offsetY = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        while (true) {
            offsetY.animateTo(
                targetValue = -8f,
                animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
            )
            offsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 300, easing = FastOutLinearInEasing)
            )
        }
    }

    Box(
        modifier = Modifier
            .size(14.dp)
            .offset(y = offsetY.value.dp)
            .background(Color.White, shape = CircleShape)
    )
}



@Composable
fun EdgeGlowOverlay(isSpeaking: Boolean) {
    // Animate brightness / rotation while speaking
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    if (isSpeaking) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            // Sweep gradient rotates to give a moving glow
            rotate(rotation) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        listOf(
                            Color(0xFF00BCD4).copy(alpha = glowAlpha),
                            Color(0xFF8BC34A).copy(alpha = glowAlpha),
                            Color(0xFF00BCD4).copy(alpha = glowAlpha) // loop back
                        )
                    ),
                    radius = (size.minDimension / 2.3).toFloat(),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 24f,
                        cap = StrokeCap.Round
                    )
                )
            }
        }
    }
}