package com.br444n.unitwise.app.feature.share

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object ShareDispatchers {
    val main: CoroutineDispatcher = Dispatchers.Main
    val io: CoroutineDispatcher = Dispatchers.IO
}
