package edts.android.composedemo.ui.screen.state

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

val stepCounterFlow: Flow<Int> = flow {
    var stepCount = 0
    while (true) {
        delay(1000)
        emit(++stepCount)
    }
}
