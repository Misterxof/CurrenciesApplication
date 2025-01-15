package com.misterioes.currenciesapplication.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misterioes.currenciesapplication.ui.base.ViewSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _effect = Channel<ViewSideEffect>()
    val effect: ReceiveChannel<ViewSideEffect> = _effect

    fun setEffect(effect: MainContract.Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}