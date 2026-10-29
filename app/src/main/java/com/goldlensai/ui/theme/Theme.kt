package com.goldlensai.data

import java.util.Locale

data class MarketStatus(
    val connected: Boolean,
    val message: String,
    val lastUpdated: String
)

data class Candle(
    val time: String,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float
)

data class NewsItem(
    val headline: String,
    val summary: String,
    val source: String,
    val time: String
)

object DemoData {
    fun marketStatus(): MarketStatus = MarketStatus(
        connected = false,
        message = "MARKET DATA UNAVAILABLE",
        lastUpdated = "No provider configured"
    )

    fun candles(): List<Candle> = listOf(
        Candle("09:00", 2342.4f, 2351.8f, 2338.1f, 2349.6f),
        Candle("09:05", 2349.6f, 2356.0f, 2345.1f, 2352.7f),
        Candle("09:10", 2352.7f, 2358.1f, 2346.3f, 2347.5f),
        Candle("09:15", 2347.5f, 2350.3f, 2342.0f, 2344.1f),
        Candle("09:20", 2344.1f, 2350.9f, 2339.8f, 2348.4f),
        Candle("09:25", 2348.4f, 2355.7f, 2343.9f, 2353.6f),
        Candle("09:30", 2353.6f, 2360.2f, 2348.0f, 2356.0f),
        Candle("09:35", 2356.0f, 2359.8f, 2348.5f, 2350.1f),
        Candle("09:40", 2350.1f, 2358.7f, 2345.3f, 2347.9f),
        Candle("09:45", 2347.9f, 2352.2f, 2340.6f, 2343.5f)
    )

    fun news(): List<NewsItem> = listOf(
        NewsItem(
            headline = "Gold traders watch key support zone as macro data remains mixed.",
            summary = "The metal is being assessed against support around 2340 and a wider neutral bias in the short term.",
            source = "GoldHub Monitor",
            time = "3h ago"
        ),
        NewsItem(
            headline = "Central bank reporting and ETF demand continue to shape the narrative.",
            summary = "Demand remains selective while volatility stays moderate. No live market price feed is attached in this build.",
            source = "Market Journal",
            time = "5h ago"
        ),
        NewsItem(
            headline = "Analysts highlight range trading conditions for XAUUSD.",
            summary = "Recent price action remains inside a defined band, reinforcing a cautious but steady outlook.",
            source = "Global Metals Desk",
            time = "Yesterday"
        )
    )
}
