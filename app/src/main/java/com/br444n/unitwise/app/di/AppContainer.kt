package com.br444n.unitwise.app.di

import android.content.Context
import com.br444n.unitwise.app.core.firebase.data.FirebaseTelemetryLogger
import com.br444n.unitwise.app.core.firebase.domain.TelemetryLogger
import com.br444n.unitwise.app.core.firebase.domain.usecase.LogListParityTypeUseCase
import com.br444n.unitwise.app.core.firebase.domain.usecase.LogListSharedUseCase
import com.br444n.unitwise.app.core.firebase.domain.usecase.LogOcrAttemptUseCase
import com.br444n.unitwise.app.core.firebase.domain.usecase.LogParityToggledUseCase
import com.br444n.unitwise.app.data.datasource.remote.SharedComparisonRemoteDataSource
import com.br444n.unitwise.app.data.local.AppDatabase
import com.br444n.unitwise.app.data.local.dao.ShoppingListDao
import com.br444n.unitwise.app.data.local.dao.ShoppingListItemDao
import com.br444n.unitwise.app.data.repository.ComparisonRepositoryImpl
import com.br444n.unitwise.app.data.repository.UserPreferencesRepositoryImpl
import com.br444n.unitwise.app.domain.repository.ComparisonRepository
import com.br444n.unitwise.app.domain.repository.UserPreferencesRepository
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

interface AppContainer {
    val comparisonRepository: ComparisonRepository
    val userPreferencesRepository: UserPreferencesRepository
    val shoppingListDao: ShoppingListDao
    val shoppingListItemDao: ShoppingListItemDao

    val telemetryLogger: TelemetryLogger
    val logOcrAttemptUseCase: LogOcrAttemptUseCase
    val logParityToggledUseCase: LogParityToggledUseCase
    val logListParityTypeUseCase: LogListParityTypeUseCase
    val logListSharedUseCase: LogListSharedUseCase
}

class DefaultAppContainer(
    private val context: Context,
) : AppContainer {
    override val comparisonRepository: ComparisonRepository by lazy {
        ComparisonRepositoryImpl(
            dao = AppDatabase.getDatabase(context).comparisonDao(),
            remoteDataSource = SharedComparisonRemoteDataSource(),
        )
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepositoryImpl(
            context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE),
        )
    }

    override val shoppingListDao: ShoppingListDao by lazy {
        AppDatabase.getDatabase(context).shoppingListDao()
    }

    override val shoppingListItemDao: ShoppingListItemDao by lazy {
        AppDatabase.getDatabase(context).shoppingListItemDao()
    }

    override val telemetryLogger: TelemetryLogger by lazy {
        FirebaseTelemetryLogger(
            analytics = FirebaseAnalytics.getInstance(context),
            crashlytics = FirebaseCrashlytics.getInstance(),
        )
    }

    override val logOcrAttemptUseCase: LogOcrAttemptUseCase by lazy {
        LogOcrAttemptUseCase(telemetryLogger)
    }
    override val logParityToggledUseCase: LogParityToggledUseCase by lazy {
        LogParityToggledUseCase(telemetryLogger)
    }
    override val logListParityTypeUseCase: LogListParityTypeUseCase by lazy {
        LogListParityTypeUseCase(telemetryLogger)
    }
    override val logListSharedUseCase: LogListSharedUseCase by lazy {
        LogListSharedUseCase(telemetryLogger)
    }
}
