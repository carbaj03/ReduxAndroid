package com.acv.redux

import com.acv.redux.types.*
import kotlinx.coroutines.flow.StateFlow

fun <S : State> applyMiddleware(
    vararg middlewares: Middleware<S>
): StoreEnhancer<S> =
    StoreEnhancer { createStore ->
        StoreEnhancerStoreCreator { reducer, initialState ->
            val store = createStore(reducer, initialState)
            var dispatch: Dispatch<Action> = Dispatch {
                throw  Throwable(
                    "Dispatching while constructing your middleware is not allowed. " +
                            "Other middleware would not be applied to this dispatch."
                )
            }
            val middlewareAPI: MiddlewareAPI<S> = object : MiddlewareAPI<S> {
                override val dispatch: Dispatcher = Dispatch { dispatch(it) }
                override fun getState(): S = store.state.value
            }

            dispatch = middlewares.foldRight(store.dispatch) { middleware, dispatcher ->
                Dispatch { action ->  middleware(middlewareAPI, dispatcher, action) }
            }
            object : Store<S> {
                override val dispatch: Dispatch<Action> = dispatch
                override val state: StateFlow<S> = store.state
            }
        }
    }