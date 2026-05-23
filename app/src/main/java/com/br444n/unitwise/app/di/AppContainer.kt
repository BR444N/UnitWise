package com.br444n.unitwise.app.di

import android.content.Context
import com.br444n.unitwise.app.data.datasource.remote.SharedComparisonRemoteDataSource
import com.br444n.unitwise.app.data.local.AppDatabase
import com.br444n.unitwise.app.data.repository.ComparisonRepositoryImpl
import com.br444n.unitwise.app.data.repository.UserPreferencesRepositoryImpl
import com.br444n.unitwise.app.domain.repository.ComparisonRepository
import com.br444n.unitwise.app.domain.repository.UserPreferencesRepository
import com.br444n.unitwise.app.data.local.dao.ShoppingListDao
import com.br444n.unitwise.app.data.local.dao.ShoppingListItemDao

interface AppContainer {
    val comparisonRepository: ComparisonRepository
    val userPreferencesRepository: UserPreferencesRepository
    val shoppingListDao: ShoppingListDao
    val shoppingListItemDao: ShoppingListItemDao
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

    override val shoppingListDao: ShoppingListDao by lazy {
        AppDatabase.getDatabase(context).shoppingListDao()
    }

    override val shoppingListItemDao: ShoppingListItemDao by lazy {
        AppDatabase.getDatabase(context).shoppingListItemDao()
    }
}
