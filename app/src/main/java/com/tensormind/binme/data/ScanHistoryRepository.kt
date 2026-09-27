package com.tensormind.binme.data

import com.tensormind.binme.data.remote.ScanResultData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HistoryItem(
    val id: String,
    val itemName: String,
    val category: String,
    val confidence: String,
    val timestamp: String,
    val description: String
)

object ScanHistoryRepository {
    private val _historyList = MutableStateFlow<List<HistoryItem>>(
        listOf(
            HistoryItem(
                id = "1",
                itemName = "Plastic Water Bottle (PET #1)",
                category = "Recycle",
                confidence = "94% confidence",
                timestamp = "Today, 10:15 AM",
                description = "Recyclable in Lake Oswego mixed recycling bin after rinsing."
            ),
            HistoryItem(
                id = "2",
                itemName = "Greasy Pizza Box",
                category = "Compost",
                confidence = "91% confidence",
                timestamp = "Yesterday, 7:40 PM",
                description = "Compostable in Lake Oswego curbside green bin."
            )
        )
    )

    val historyList: StateFlow<List<HistoryItem>> = _historyList.asStateFlow()

    fun addScanResult(result: ScanResultData) {
        val currentTime = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date())
        val newItem = HistoryItem(
            id = System.currentTimeMillis().toString(),
            itemName = result.itemName,
            category = result.category,
            confidence = result.confidence,
            timestamp = currentTime,
            description = result.description
        )
        val current = _historyList.value.toMutableList()
        current.add(0, newItem)
        _historyList.value = current
    }

    fun clearHistory() {
        _historyList.value = emptyList()
    }
}
