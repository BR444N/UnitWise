package com.br444n.unitwise.app.di

import android.content.Context
import com.br444n.unitwise.app.data.local.AppDatabase
import com.br444n.unitwise.app.data.repository.ComparisonRepositoryImpl
import com.br444n.unitwise.app.domain.repository.ComparisonRepository

interface AppContainer {
    val comparisonRepository: ComparisonRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val comparisonRepository: ComparisonRepository by lazy {
        ComparisonRepositoryImpl(AppDatabase.getDatabase(context).comparisonDao())
    }
}
