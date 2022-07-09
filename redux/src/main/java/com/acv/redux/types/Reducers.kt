package com.acv.redux.types

fun interface Reducer<S : State> {
    operator fun invoke(state: S, action: Action): S
}

fun interface ReducerType<S : State, A : Action> {
    operator fun invoke(state: S, action: A): S
}

inline fun <reified S : State, reified A : Action> Reducer(
    reducer: ReducerType<S, A>
): Reducer<State> =
    Reducer { state, action ->
        try {
            reducer(state as S, action as A)
        } catch (e : java.lang.Exception){
            state
        }
    }

inline fun <reified S : State, reified A : Action> ReducerT(
    reducerType: ReducerType<S, A>
): Reducer<S> =
    Reducer { state, action ->
        try {
            reducerType(state, action as A)
        } catch (e : java.lang.Exception){
            state
        }
    }

typealias ReducersMapObject = Map<String, Reducer<State>>

fun combineReducers(reducers: ReducersMapObject): Reducer<CombineState> {
    val finalReducerKeys: Set<String> = reducers.keys

    return Reducer { state, action ->
        var hasChanged = false
        val nextState: CombineState = object : CombineState {
            override val states: MutableMap<String, State> = hashMapOf()
        }
        finalReducerKeys.forEach { key ->
            val reducer: Reducer<State> = reducers[key]!!
            val previousStateForKey: State? = state.states[key]
            val nextStateForKey: State? = previousStateForKey?.let { reducer(it, action) }

            nextStateForKey?.let { nextState.states[key] = nextStateForKey }
            hasChanged = hasChanged || nextStateForKey != previousStateForKey
        }
        hasChanged = hasChanged || finalReducerKeys.size != state.states.keys.size
        (if (hasChanged) nextState else state)
    }
}