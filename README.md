# 🎙️ HearYou

**HearYou** is an Android voice companion app that lets you have natural, spoken conversations with AI-powered personas. Speak freely — HearYou listens, thinks, and talks back in a warm, human voice.

---

## ✨ Features

- **Voice-first interaction** — tap the mic, speak naturally, hear a real voice reply
- **AI-powered conversations** — backed by Google Gemini for context-aware, conversational responses
- **Natural TTS** — high-quality voice synthesis via ElevenLabs with multiple voice personas to choose from
- **Multilingual support** — switch between English and Hindi mid-conversation
- **Live transcript** — see what you're saying in real time while you speak
- **Conversation history** — full chat view with animated message bubbles
- **Clean, minimal UI** — built entirely in Jetpack Compose with smooth animations

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| DI | Hilt |
| Networking | Ktor |
| AI / Chat | Google Gemini API |
| Text-to-Speech | ElevenLabs API |
| Speech-to-Text | Android SpeechRecognizer |
| Audio Playback | MediaPlayer |
| Architecture | MVVM + Repository pattern |

---

## 📸 Screenshots

> _Coming soon_

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- Android SDK 26+
- A [Google Gemini API key](https://aistudio.google.com/app/apikey)
- An [ElevenLabs API key](https://elevenlabs.io)

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/hearyou.git
   cd hearyou
   ```

2. Add your API keys to `Constants.kt` (or a `local.properties` / secrets file):
   ```kotlin
   const val GEMINI_API_KEY = "your_gemini_key"
   const val ELEVENLABS_API_KEY = "your_elevenlabs_key"
   ```

3. Build and run on a physical device or emulator with microphone support.

---

## 🏗️ Architecture

```
app/
├── data/
│   ├── api/          # Gemini & ElevenLabs API clients (Ktor)
│   ├── model/        # Data classes (ChatMessage, Voice, etc.)
│   └── repository/   # ConversationRepositoryImpl
├── domain/
│   └── repository/   # Repository interface
├── presentation/
│   ├── components/   # Reusable Compose components
│   ├── screens/      # ConversationScreen
│   └── viewmodel/    # ConversationViewModel + VoiceState
└── util/
    └── Constants.kt
```

---

## 🎭 Voice Personas

Each voice is backed by an ElevenLabs voice ID and comes with a name, personality description, and language. Voices are displayed in a horizontally scrollable selector strip — tap any chip to switch mid-conversation.

---

## 📋 Permissions

| Permission | Reason |
|---|---|
| `RECORD_AUDIO` | Microphone access for speech recognition |
| `INTERNET` | API calls to Gemini and ElevenLabs |

---

## 📄 License

```
MIT License — see LICENSE for details
```

---

## 🙏 Acknowledgements

- [Google Gemini](https://deepmind.google/technologies/gemini/) for the conversational AI backbone
- [ElevenLabs](https://elevenlabs.io) for lifelike voice synthesis
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for the UI toolkit