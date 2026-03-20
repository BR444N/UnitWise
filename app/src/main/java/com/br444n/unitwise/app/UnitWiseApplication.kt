package com.br444n.unitwise.app

import android.app.Application
import com.br444n.unitwise.app.di.AppContainer
import com.br444n.unitwise.app.di.DefaultAppContainer

class UnitWiseApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
