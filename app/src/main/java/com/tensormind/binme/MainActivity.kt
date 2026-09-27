package com.tensormind.binme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.tensormind.binme.ui.home.BinMeHomeScreen
import com.tensormind.binme.ui.onboarding.OnboardingPager
import com.tensormind.binme.ui.theme.BinMeTheme
import com.tensormind.binme.util.DataStoreManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Hide system bars and status bar for immersive full-screen mode
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            BinMeTheme {
                val context = LocalContext.current
                val dataStoreManager = remember { DataStoreManager(context) }
                val scope = rememberCoroutineScope()

                val onboardingCompleted by dataStoreManager.isOnboardingCompleted.collectAsState(initial = null)
                var currentScreen by remember { mutableStateOf<AppScreen?>(null) }

                LaunchedEffect(onboardingCompleted) {
                    if (onboardingCompleted == false) {
                        currentScreen = AppScreen.ONBOARDING
                    } else if (onboardingCompleted == true) {
                        currentScreen = AppScreen.HOME
                    }
                }

                when (currentScreen) {
                    AppScreen.ONBOARDING -> {
                        OnboardingPager(
                            onFinished = {
                                scope.launch {
                                    dataStoreManager.setOnboardingCompleted()
                                }
                                currentScreen = AppScreen.HOME
                            }
                        )
                    }
                    AppScreen.HOME -> {
                        BinMeHomeScreen(
                            onNavigateToScan = { /* Handled internally in BinMeHomeScreen state */ },
                            onNavigateToChat = { /* Handled internally in BinMeHomeScreen state */ },
                            onNavigateToHistory = { /* Handled internally in BinMeHomeScreen state */ },
                            onNavigateToTips = { /* Handled internally in BinMeHomeScreen state */ },
                            onNavigateToSettings = { /* Handled internally in BinMeHomeScreen state */ },
                            onLogOut = {
                                scope.launch {
                                    dataStoreManager.clearAll()
                                    currentScreen = AppScreen.ONBOARDING
                                }
                            }
                        )
                    }
                    null -> {
                        // Initial state loading
                    }
                }
            }
        }
    }
}

enum class AppScreen {
    ONBOARDING,
    HOME
}
