package com.misterioes.currenciesapplication.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch

fun <T1, T2> combineAndAwait(
    flow1: Flow<T1>,
    flow2: Flow<T2>
): Flow<Pair<T1, T2>> =
    flow {
        var first: T1? = null
        var second: T2? = null
        val latch = CountDownLatch(2)

        CoroutineScope(Job() + Dispatchers.IO).launch {
            flow1.collect() {
                first = it
                latch.countDown()
            }
        }

        CoroutineScope(Job() + Dispatchers.IO).launch {
            flow2.collect() {
                second = it
                latch.countDown()
            }
        }

        latch.await()
        if (first != null && second != null)
            emit(Pair(first!!, second!!))
    }