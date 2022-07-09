package com.acv.redux

import com.acv.redux.types.*

fun configureStore(
    vararg slice: Slice<*>,
    middleware: Array<Middleware<CombineState>> = emptyArray(),
): Store<CombineState> =
    createStore(
        reducer = combineReducers(
            (slice.associate { it.reducer }.asSequence() + slice.associate { it.extraReducer }.asSequence())
                .distinct()
                .groupBy({ it.key }, { it.value })
                .mapValues { combineReducers(it.value) }
        ),
        preloadedState = object : CombineState {
            override val states: MutableMap<String, State> = slice.associate { it.initialState }.toMutableMap()
        },
        enhancer = applyMiddleware(createThunkMiddleware(), *middleware)
    )

inline fun <reified S : State, reified A : Action> createStore(
    vararg reducer: ReducerType<S, A> = arrayOf(),
    initialState: S,
    middleware: Array<Middleware<S>> = emptyArray(),
    extra: ReducerType<S, A> = ReducerType { s, _ -> s },
): Store<S> =
    createStore(
        reducer = combineReducers(
            reducer
                .map { ReducerT<S, A> { state, action -> it(state, action) } }
                .plus(ReducerT<S, A> { state, action -> extra(state, action) })
        ),
        preloadedState = initialState,
        enhancer = applyMiddleware(createThunkMiddleware(), *middleware)
    )

interface Slice<S : State> {
    val name: String
    val initialState: Pair<String, S>
    val reducer: Pair<String, Reducer<State>>
    val extraReducer: Pair<String, Reducer<State>>
}

inline fun <reified S : State, reified A : Action> createSlice(
    initialState: S,
    crossinline extra: (S, Action) -> S = { s, _ -> s },
    crossinline reducer: (S, A) -> S = { s, _ -> s }
): Slice<S> =
    object : Slice<S> {
        override val name: String =
            S::class.simpleName!!
        override val reducer: Pair<String, Reducer<State>> =
            name to Reducer<S, A> { state, action -> reducer(state, action) }
        override val initialState: Pair<String, S> =
            name to initialState
        override val extraReducer: Pair<String, Reducer<State>> =
            name to Reducer<S, Action> { state, action -> extra(state, action) }
    }

@Suppress("UNCHECKED_CAST")
fun <S : State> CombineState.select(slice: Slice<S>): S =
    states[slice.name] as S

fun <S : State> createAction(slice: Slice<S>, f: (S, Dispatch<Action>) -> Unit): Thunk =
    Thunk { states, dispatcher -> f(states.select(slice), dispatcher) }

fun <S : State> createAction(f: (S, Dispatch<Action>) -> Unit): AsyncAction<S> =
    AsyncAction { state, dispatcher -> f(state, dispatcher) }