package com.goldlensai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.goldlensai.data.DemoData
import com.goldlensai.data.MarketStatus
import com.goldlensai.ui.theme.GoldLensTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var selectedLanguage by remember { mutableStateOf("English") }
            var voiceOutputEnabled by remember { mutableStateOf(false) }
            var selectedTimeframe by remember { mutableStateOf("H1") }
            var marketStatus by remember {
                mutableStateOf(
                    DemoData.marketStatus()
                )
            }

            fun refreshMarketStatus() {
                marketStatus = DemoData.marketStatus()
            }

            GoldLensTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF050B13))
                            .padding(0.dp),
                        verticalArrangement = Arrangement.Top
                    ) {
                        GoldLensApp(
                            selectedLanguage = selectedLanguage,
                            onLanguageChanged = { selectedLanguage = it },
                            voiceOutputEnabled = voiceOutputEnabled,
                            onVoiceChanged = { voiceOutputEnabled = it },
                            selectedTimeframe = selectedTimeframe,
                            onTimeframeChanged = { selectedTimeframe = it },
                            marketStatus = marketStatus,
                            onRefreshMarketStatus = ::refreshMarketStatus
                        )
                    }
                }
            }
        }
    }
}
