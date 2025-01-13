package com.misterioes.currenciesapplication.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<Intent : ViewIntent, State : ViewState, Effect : ViewSideEffect> :
    ViewModel() {
    private val _state = MutableStateFlow<State>(initialState())
    val state: StateFlow<State> = _state

    private val _effect = Channel<Effect>()
    val effect: ReceiveChannel<Effect> = _effect

    private val _intent: MutableSharedFlow<Intent> = MutableSharedFlow()

    protected abstract fun initialState(): State
    protected abstract fun handleIntent(intent: Intent)

    init {
        subscribeToIntents()
    }

    private fun subscribeToIntents() {
        viewModelScope.launch {
            _intent.collect {
                handleIntent(it)
            }
        }
    }

    protected fun setState(reducer: State.() -> State) {
        _state.value = _state.value.reducer()
    }

    open fun sendEffect(builder: () -> Effect) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }

    open fun setIntent(intent: Intent) {
        viewModelScope.launch {
            _intent.emit(intent)
        }
    }
}