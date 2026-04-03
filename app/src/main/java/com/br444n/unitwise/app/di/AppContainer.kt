package com.br444n.unitwise.app.di

import android.content.Context
import com.br444n.unitwise.app.data.datasource.remote.SharedComparisonRemoteDataSource
import com.br444n.unitwise.app.data.local.AppDatabase
import com.br444n.unitwise.app.data.repository.ComparisonRepositoryImpl
import com.br444n.unitwise.app.data.repository.UserPreferencesRepositoryImpl
import com.br444n.unitwise.app.domain.repository.ComparisonRepository
import com.br444n.unitwise.app.domain.repository.UserPreferencesRepository

interface AppContainer {
    val comparisonRepository: ComparisonRepository
    val userPreferencesRepository: UserPreferencesRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val comparisonRepository: ComparisonRepository by lazy {
        ComparisonRepositoryImpl(
            dao = AppDatabase.getDatabase(context).comparisonDao(),
            remoteDataSource = SharedComparisonRemoteDataSource()
        )
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepositoryImpl(context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE))
    }
}
