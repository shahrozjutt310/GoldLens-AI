package com.goldlensai

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.goldlensai.data.DemoData
import com.goldlensai.data.MarketStatus
import com.goldlensai.data.NewsItem
import com.goldlensai.ui.theme.GoldLensTheme
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

sealed class AppScreen(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    data object Home : AppScreen("home", "Home", Icons.Filled.Home)
    data object Chart : AppScreen("chart", "Live Chart", Icons.Filled.ShowChart)
    data object Assistant : AppScreen("assistant", "AI Assistant", Icons.Filled.Assistant)
    data object News : AppScreen("news", "Market News", Icons.Filled.Newspaper)
    data object Settings : AppScreen("settings", "Settings", Icons.Filled.Settings)
}

@Composable
fun GoldLensApp(
    modifier: Modifier = Modifier,
    selectedLanguage: String,
    onLanguageChanged: (String) -> Unit,
    voiceOutputEnabled: Boolean,
    onVoiceChanged: (Boolean) -> Unit,
    selectedTimeframe: String,
    onTimeframeChanged: (String) -> Unit,
    marketStatus: MarketStatus,
    onRefreshMarketStatus: () -> Unit
) {
    val navController = rememberNavController()
    val items = listOf(AppScreen.Home, AppScreen.Chart, AppScreen.Assistant, AppScreen.News, AppScreen.Settings)

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF08131E)) {
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                items.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFFD166),
                            selectedTextColor = Color(0xFFFFD166),
                            unselectedIconColor = Color(0xFFB8C2D0),
                            unselectedTextColor = Color(0xFFB8C2D0),
                            indicatorColor = Color(0xFF10273B)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppScreen.Home.route) {
                HomeScreen(
                    marketStatus = marketStatus,
                    onRefresh = onRefreshMarketStatus,
                    selectedLanguage = selectedLanguage,
                    selectedTimeframe = selectedTimeframe,
                    onTimeframeChanged = onTimeframeChanged
                )
            }
            composable(AppScreen.Chart.route) {
                ChartScreen(
                    selectedTimeframe = selectedTimeframe,
                    onTimeframeChanged = onTimeframeChanged,
                    marketStatus = marketStatus
                )
            }
            composable(AppScreen.Assistant.route) {
                AssistantScreen(
                    voiceOutputEnabled = voiceOutputEnabled,
                    onVoiceChanged = onVoiceChanged,
                    selectedLanguage = selectedLanguage,
                    onLanguageChanged = onLanguageChanged
                )
            }
            composable(AppScreen.News.route) {
                NewsScreen()
            }
            composable(AppScreen.Settings.route) {
                SettingsScreen(
                    selectedLanguage = selectedLanguage,
                    onLanguageChanged = onLanguageChanged,
                    voiceOutputEnabled = voiceOutputEnabled,
                    onVoiceChanged = onVoiceChanged,
                    selectedTimeframe = selectedTimeframe,
                    onTimeframeChanged = onTimeframeChanged
                )
            }
        }
    }
}

@Composable
private fun HomeScreen(
    marketStatus: MarketStatus,
    onRefresh: () -> Unit,
    selectedLanguage: String,
    selectedTimeframe: String,
    onTimeframeChanged: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050B13))
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeaderRow(title = "GoldLens AI", subtitle = "Gold market intelligence")

        StatusCard(
            title = "XAUUSD",
            statusText = marketStatus.message,
            connected = marketStatus.connected,
            lastUpdated = marketStatus.lastUpdated,
            onRefresh = onRefresh
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricTile(label = "Trend", value = "Range-Bound", tone = Color(0xFFFFD166))
            MetricTile(label = "RSI", value = "48.4", tone = Color(0xFF70E3C7))
            MetricTile(label = "Volumes", value = "Low", tone = Color(0xFF9CC9FF))
        }

        SectionCard(title = "Analysis Snapshot") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AnalysisRow(label = "Support", value = "2340.20")
                AnalysisRow(label = "Resistance", value = "2388.60")
                AnalysisRow(label = "Volatility", value = "Moderate")
                AnalysisRow(label = "Moving Average", value = "50/200 EMA alignment")
            }
        }

        SectionCard(title = "Market Context") {
            Text(
                text = "This app intentionally does not connect to an external market-data provider by default. The dashboard reflects the live configuration status only. Demo or demo-like sample values are clearly marked and must never be mistaken for live market pricing.",
                color = Color(0xFFCBD6E1),
                lineHeight = 22.sp
            )
        }

        SectionCard(title = "Preferred Timeframe") {
            val timeframes = listOf("M1", "M5", "M15", "M30", "H1", "H4", "D1")
            FlowRow(items = timeframes, selected = selectedTimeframe, onSelect = onTimeframeChanged)
        }

        SectionCard(title = "Interface Language") {
            val languages = listOf("English", "اردو", "Roman Urdu")
            FlowRow(items = languages, selected = selectedLanguage, onSelect = onLanguageChanged)
        }
    }
}

@Composable
private fun ChartScreen(
    selectedTimeframe: String,
    onTimeframeChanged: (String) -> Unit,
    marketStatus: MarketStatus
) {
    val candles = DemoData.candles()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050B13))
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeaderRow(title = "Live Chart", subtitle = "XAUUSD • ${selectedTimeframe}")

        if (!marketStatus.connected) {
            ErrorState(title = "MARKET DATA UNAVAILABLE", message = "No live provider configured. This chart is a local demonstration and not a live feed.")
        }

        val timeframes = listOf("M1", "M5", "M15", "M30", "H1", "H4", "D1")
        FlowRow(items = timeframes, selected = selectedTimeframe, onSelect = onTimeframeChanged)

        SectionCard(title = "Candlestick View") {
            CandlestickChart(candles = candles)
        }

        SectionCard(title = "Technical Snapshot") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AnalysisRow(label = "Trend", value = "Neutral to upward")
                AnalysisRow(label = "Support", value = "2340.20")
                AnalysisRow(label = "Resistance", value = "2388.60")
                AnalysisRow(label = "RSI", value = "48.4 - neutral")
                AnalysisRow(label = "MA Signal", value = "50 EMA above 200 EMA")
                AnalysisRow(label = "Volatility", value = "Moderate")
            }
        }
    }
}

@Composable
private fun AssistantScreen(
    voiceOutputEnabled: Boolean,
    onVoiceChanged: (Boolean) -> Unit,
    selectedLanguage: String,
    onLanguageChanged: (String) -> Unit
) {
    val context = LocalContext.current
    var prompt by remember { mutableStateOf("XAUUSD کی سمت کیا ہے؟") }
    var reply by remember { mutableStateOf("GoldLens AI: ڈیٹا فراہم کنندہ کنفیگریشن موجود نہیں۔ اس لیے یہ ایک ڈیمو مشاورت ہے۔") }
    var listening by remember { mutableStateOf(false) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    LaunchedEffect(context) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("ur")
            }
        }
    }
    DisposableEffect(tts) {
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    fun speakMessage(message: String) {
        if (voiceOutputEnabled) {
            val configured = if (selectedLanguage == "اردو") Locale("ur") else Locale.US
            tts?.language = configured
            tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, "goldlens_ai")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050B13))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeaderRow(title = "AI Assistant", subtitle = "Urdu and Roman Urdu")

        if (selectedLanguage == "Roman Urdu") {
            Text(text = "Roman Urdu mode enabled", color = Color(0xFFB8C2D0))
        }

        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Ask about gold or the market") },
            minLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFFD166),
                focusedLabelColor = Color(0xFFFFD166),
                unfocusedBorderColor = Color(0xFF2A3645),
                unfocusedLabelColor = Color(0xFFB8C2D0),
                cursorColor = Color(0xFFFFD166)
            )
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    val message = when {
                        selectedLanguage == "Roman Urdu" -> "GoldLens AI: market data unavailable. Demo analysis shows trend is stable with support near 2340 and resistance around 2388."
                        selectedLanguage == "اردو" -> "GoldLens AI: مارکیٹ ڈیٹا دستیاب نہیں ہے۔ ڈیمو تجزیہ کے مطابق ٹرینڈ مستحکم ہے، سپورٹ 2340 کے قریب اور ریزسٹینس 2388 کے ارد گرد ہے۔"
                        else -> "GoldLens AI: market data is unavailable. Demo analysis suggests a stable trend with support near 2340 and resistance around 2388."
                    }
                    reply = message
                    speakMessage(message)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD166), contentColor = Color(0xFF0A1624))
            ) {
                Text("Ask AI")
            }

            OutlinedButton(
                onClick = {
                    listening = !listening
                    val message = if (listening) "Listening for your question…" else "Microphone ready"
                    reply = message
                    if (voiceOutputEnabled) speakMessage(message)
                },
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB8C2D0))
            ) {
                Icon(Icons.Filled.Mic, contentDescription = "Microphone")
                Spacer(Modifier.width(8.dp))
                Text(if (listening) "Listening" else "Mic")
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Voice output", color = Color(0xFFCBD6E1))
            Switch(checked = voiceOutputEnabled, onCheckedChange = onVoiceChanged)
        }

        val languageOptions = listOf("English", "اردو", "Roman Urdu")
        FlowRow(items = languageOptions, selected = selectedLanguage, onSelect = onLanguageChanged)

        SectionCard(title = "AI Response") {
            Text(reply, color = Color(0xFFCBD6E1), lineHeight = 22.sp)
        }
    }
}

@Composable
private fun NewsScreen() {
    val items = DemoData.news()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050B13))
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeaderRow(title = "Market News", subtitle = "Verified sources • demo feed")

        if (items.isEmpty()) {
            EmptyState(title = "No market news", message = "No items are available until a news source is configured.")
        } else {
            items.forEach { item ->
                NewsCard(item = item)
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    selectedLanguage: String,
    onLanguageChanged: (String) -> Unit,
    voiceOutputEnabled: Boolean,
    onVoiceChanged: (Boolean) -> Unit,
    selectedTimeframe: String,
    onTimeframeChanged: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050B13))
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeaderRow(title = "Settings", subtitle = "Personalize your setup")

        SectionCard(title = "Language") {
            FlowRow(items = listOf("English", "اردو", "Roman Urdu"), selected = selectedLanguage, onSelect = onLanguageChanged)
        }

        SectionCard(title = "Theme") {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Dark Navy theme", color = Color(0xFFCBD6E1))
                Icon(Icons.Filled.DarkMode, contentDescription = "Theme", tint = Color(0xFFFFD166))
            }
        }

        SectionCard(title = "Voice output") {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Enable spoken responses", color = Color(0xFFCBD6E1))
                Switch(checked = voiceOutputEnabled, onCheckedChange = onVoiceChanged)
            }
        }

        SectionCard(title = "Default chart timeframe") {
            FlowRow(items = listOf("M1", "M5", "M15", "M30", "H1", "H4", "D1"), selected = selectedTimeframe, onSelect = onTimeframeChanged)
        }

        SectionCard(title = "Service configuration") {
            Text(
                text = "No API secret or key is embedded in the app. Add your credentials in local.properties or secure environment variables before enabling any live provider.",
                color = Color(0xFFCBD6E1),
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun HeaderRow(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = title, fontSize = 28.sp, color = Color.White, fontWeight = FontWeight.Bold)
        Text(text = subtitle, fontSize = 14.sp, color = Color(0xFFB8C2D0))
    }
}

@Composable
private fun StatusCard(
    title: String,
    statusText: String,
    connected: Boolean,
    lastUpdated: String,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1B2C)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = title, fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(text = statusText, color = if (connected) Color(0xFF70E3C7) else Color(0xFFFF6B6B), fontWeight = FontWeight.Medium)
                }
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(if (connected) Color(0xFF70E3C7) else Color(0xFFFF6B6B), shape = CircleShape)
                )
            }

            Divider(color = Color(0xFF223548))

            Text(text = "Last checked: $lastUpdated", color = Color(0xFFB8C2D0))
            Button(
                onClick = onRefresh,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD166), contentColor = Color(0xFF0A1624))
            ) {
                Text("Check connection")
            }
        }
    }
}

@Composable
private fun MetricTile(label: String, value: String, tone: Color) {
    Card(
        modifier = Modifier.weight(1f),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1B2C)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = label, color = Color(0xFFB8C2D0), fontSize = 12.sp)
            Text(text = value, color = tone, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1B2C)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            content()
        }
    }
}

@Composable
private fun AnalysisRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color(0xFFB8C2D0))
        Text(value, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun FlowRow(items: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            val isSelected = item == selected
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .clickable { onSelect(item) },
                color = if (isSelected) Color(0xFFFFD166) else Color(0xFF122535)
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        color = if (isSelected) Color(0xFF0A1624) else Color(0xFFCBD6E1),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun CandlestickChart(candles: List<com.goldlensai.data.Candle>) {
    val minPrice = candles.minOf { it.low }
    val maxPrice = candles.maxOf { it.high }
    val range = maxPrice - minPrice
    val yPadding = 24f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(Color(0xFF091722), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val chartWidth = size.width
            val chartHeight = size.height
            val candleWidth = (chartWidth - 32f) / candles.size

            candles.forEachIndexed { index, candle ->
                val x = 16f + index * candleWidth + candleWidth / 2f
                val highY = yPadding + ((maxPrice - candle.high) / range) * (chartHeight - yPadding * 2)
                val lowY = yPadding + ((maxPrice - candle.low) / range) * (chartHeight - yPadding * 2)
                val openY = yPadding + ((maxPrice - candle.open) / range) * (chartHeight - yPadding * 2)
                val closeY = yPadding + ((maxPrice - candle.close) / range) * (chartHeight - yPadding * 2)

                drawLine(
                    color = Color(0xFFB8C2D0),
                    start = androidx.compose.ui.geometry.Offset(x, highY),
                    end = androidx.compose.ui.geometry.Offset(x, lowY),
                    strokeWidth = 2f
                )

                val isBullish = candle.close >= candle.open
                val bodyTop = min(openY, closeY)
                val bodyHeight = max(6f, abs(closeY - openY))
                drawRoundRect(
                    color = if (isBullish) Color(0xFFFFD166) else Color(0xFF8EA2B8),
                    topLeft = androidx.compose.ui.geometry.Offset(x - 5f, bodyTop),
                    size = androidx.compose.ui.geometry.Size(10f, bodyHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                )
            }
        }

        androidx.compose.foundation.layout.Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 8.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text("Demo chart only", color = Color(0xFFB8C2D0), fontSize = 12.sp)
            Text("No live feed connected", color = Color(0xFFFF6B6B), fontSize = 12.sp)
        }
    }
}

@Composable
private fun NewsCard(item: NewsItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1B2C)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(item.headline, color = Color.White, fontWeight = FontWeight.SemiBold)
            Text(item.summary, color = Color(0xFFCBD6E1), lineHeight = 20.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item.source, color = Color(0xFFFFD166), fontWeight = FontWeight.Medium)
                Text(item.time, color = Color(0xFFB8C2D0))
            }
        }
    }
}

@Composable
private fun EmptyState(title: String, message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1B2C)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Filled.Info, contentDescription = null, tint = Color(0xFFB8C2D0), modifier = Modifier.size(36.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
            Text(message, color = Color(0xFFCBD6E1), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ErrorState(title: String, message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1A17)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, color = Color(0xFFFFB3B3), fontWeight = FontWeight.Bold)
            Text(message, color = Color(0xFFD7D2CF), lineHeight = 20.sp)
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color(0xFFFFD166))
    }
}
