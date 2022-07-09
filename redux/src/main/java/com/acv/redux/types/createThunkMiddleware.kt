package com.acv.redux.types

@Suppress("UNCHECKED_CAST")
fun <S : State> createThunkMiddleware(): Middleware<S> =
    Middleware { store, next, action ->
        if (action is AsyncAction<*>) {
            (action as AsyncAction<S>)(
                state = store.getState(),
                dispatcher = store.dispatch
            )
        }
        next(action)
    }
