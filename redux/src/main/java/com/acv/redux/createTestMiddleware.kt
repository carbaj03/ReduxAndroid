package com.acv.redux

import com.acv.redux.types.*
import kotlinx.coroutines.flow.StateFlow

fun <S : State> createTestMiddleware(actions: MutableList<Action>): Middleware<S> =
    Middleware { _, next, action ->
        if (action !is AsyncAction<*>)
            actions.add(action)
        next(action)
    }

interface MockStore<S : State> : Store<S> {
    val actions: List<Action>
}

fun mockStore(slice: Slice<*>): MockStore<CombineState> =
    object : MockStore<CombineState> {
        private var temp = mutableListOf<Action>()
        private val store = configureStore(slice, middleware = arrayOf(createTestMiddleware(temp)))

        override val dispatch: Dispatch<Action>
            get() = store.dispatch

        override val state: StateFlow<CombineState>
            get() = store.state

        override val actions: List<Action>
            get() = temp
    }