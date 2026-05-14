package com.example.channapatnatoys.ui.screens.verify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.channapatnatoys.data.model.Toy
import com.example.channapatnatoys.data.remote.OpenRouterService
import com.example.channapatnatoys.data.repository.ToyRepository
import com.example.channapatnatoys.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VerifyState(
    val toy: Toy? = null,
    val storyEnglish: String = "",
    val storyKannada: String = ""
)

@HiltViewModel
class VerifyViewModel @Inject constructor(
    private val toyRepository: ToyRepository,
    private val openRouterService: OpenRouterService
) : ViewModel() {

    // API key is stored in local.properties → injected via BuildConfig at build time
    private val apiKey = com.example.channapatnatoys.BuildConfig.OPENROUTER_API_KEY

    private val _uiState = MutableStateFlow<UiState<VerifyState>>(UiState.Idle)
    val uiState: StateFlow<UiState<VerifyState>> = _uiState.asStateFlow()

    fun verifyToy(toyId: String) {
        if (toyId.isBlank() || toyId.length < 3) return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // 1. Fetch toy from Firestore
                val toy = toyRepository.getToyById(toyId).firstOrNull()
                if (toy != null) {

                    // 2. Generate story via OpenRouter (Gemini free model)
                    var englishText = ""
                    var kannadaText = ""
                    
                    val prompt = "You are a storyteller for Channapatna wooden toys. " +
                            "Write 3 warm sentences about this toy in English. " +
                            "Then on a new line write ಕನ್ನಡ: followed by the Kannada translation. " +
                            "The toy is a ${toy.category} named ${toy.name} " +
                            "made by ${toy.artisanName} in ${toy.workshopAddress}."

                    val response = try {
                        openRouterService.generateContent(apiKey, prompt)
                    } catch (e: Exception) {
                        null
                    }

                    if (response != null && response.contains("ಕನ್ನಡ:")) {
                        val parts = response.split("ಕನ್ನಡ:")
                        englishText = parts[0].trim()
                        kannadaText = parts[1].trim()
                    } else if (response != null) {
                        englishText = response
                    } else {
                        // Fallback dummy story if API fails or returns null
                        englishText = "The ${toy.name} is a beautifully handcrafted toy made by ${toy.artisanName}. " +
                                "Using traditional lacquer techniques passed down through generations, each piece is " +
                                "carefully shaped from Ivory Wood sourced near Channapatna. " +
                                "This toy is a piece of Karnataka's rich cultural heritage."
                        kannadaText = "ಈ ${toy.name} ಚನ್ನಪಟ್ಟಣದ ಸಾಂಪ್ರದಾಯಿಕ ಲಾಕ್ ತಂತ್ರಜ್ಞಾನದಿಂದ ತಯಾರಿಸಲಾದ ಸುಂದರವಾದ ಆಟಿಕೆ. " +
                                "${toy.artisanName} ರಿಂದ ಕಾಳಜಿಯಿಂದ ತಯಾರಿಸಲ್ಪಟ್ಟ ಈ ಆಟಿಕೆ ಕರ್ನಾಟಕದ ಸಮೃದ್ಧ ಸಾಂಸ್ಕೃತಿಕ ಪರಂಪರೆಯ ಒಂದು ಭಾಗ."
                    }

                    _uiState.value = UiState.Success(
                        VerifyState(
                            toy = toy,
                            storyEnglish = englishText,
                            storyKannada = kannadaText
                        )
                    )
                } else {
                    _uiState.value = UiState.Error("Toy not found")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = UiState.Error("An error occurred: ${e.message}")
            }
        }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }
}
