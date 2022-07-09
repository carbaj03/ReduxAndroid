package com.acv.redux.types

interface MiddlewareAPI<S : State> {
    val dispatch: Dispatcher
    fun getState(): S
}

fun interface Middleware<S : State> {
    operator fun invoke(api: MiddlewareAPI<S>, next: Dispatcher, action: Action): Action
}