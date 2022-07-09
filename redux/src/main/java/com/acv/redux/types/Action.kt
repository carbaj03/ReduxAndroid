package com.acv.redux.types

interface Action

fun interface AsyncAction<S : State> : Action {
    operator fun invoke(
        state: S,
        dispatcher: Dispatcher,
    )
}

typealias Thunk = AsyncAction<CombineState>