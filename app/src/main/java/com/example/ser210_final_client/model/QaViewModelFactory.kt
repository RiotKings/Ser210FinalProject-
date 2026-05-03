package com.example.ser210_final_client.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ser210_final_client.data.database.AppDatabase

class QaViewModelFactory(private val db: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QaViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
