package com.acv.arrow

import arrow.core.*
import arrow.core.continuations.EffectScope
import com.acv.redux.Slice
import com.acv.redux.createAction
import com.acv.redux.createStore
import com.acv.redux.types.*
import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

val store: Store<AppState> = createStore(
    { s: AppState, a: AppAction ->
        when (a) {
            is AppAction.Counter -> s.copy(counter = a.count, isLoading = false)
            AppAction.Increment -> s.copy(counter = s.counter + 1, isLoading = false)
            AppAction.Error -> s.copy(error = "sdafdsf", isLoading = false)
            AppAction.Loading -> s.copy(isLoading = true)
        }
    },
    { s: AppState, a: UserAction ->
        when (a) {
            UserAction.Increment -> s.copy(counter = s.counter + 2)
        }
    },
    initialState = AppState()
)

suspend fun main() {
    withContexts(WithScope, Repository) {

        launchIO {
            store.dispatch(complexUseCase())
        }

        store.state.collect {
            println(it)
        }
    }
}


data class AppState(
    val counter: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
) : State

sealed interface AppAction : Action {
    object Error : AppAction
    object Loading : AppAction
    object Increment : AppAction
    data class Counter(val count: Int) : AppAction
}

sealed interface UserAction : Action {
    object Increment : UserAction
}

interface Repository {
   suspend fun increment(): Either<DomainError, Int>
    suspend fun decrement(): Either<DomainError, Int>

    companion object : Repository {
        private var count = 0

        override suspend fun increment(): Either<DomainError, Int> {
            delay(1.seconds)
            if(Random.nextBoolean()) return DomainError.Default.left()
            return Either.Right(count + 1)
        }

        override suspend fun decrement(): Either<DomainError, Int> {
            delay(1.seconds)
            if(Random.nextBoolean()) return DomainError.Default.left()
            return Either.Right(count - 1)
        }
    }
}

context(WithScope, Repository)
fun complexUseCase(): AsyncAction<AppState> =
    action<AppState, DomainError>(
        onError = { AppAction.Error.dispatch() }
    ) {
        decrementUseCase().dispatch()
        incrementUseCase().dispatch()
    }

context(WithScope, Repository)
fun decrementUseCase() = action<AppState, DomainError>(
    onError = { AppAction.Error.dispatch() }
) {
    AppAction.Loading.dispatch()
    decrement().bind().let(AppAction::Counter).dispatch()
}

context(WithScope, Repository)
fun incrementUseCase() = action<AppState, DomainError>(
    onError = { AppAction.Error.dispatch() }
) {
    AppAction.Loading.dispatch()
    increment().bind().let(AppAction::Counter).dispatch()
}

sealed interface DomainError {
    object Default : DomainError
}

fun <A> A?.toEither(): Either<DomainError, A> =
    this?.right() ?: DomainError.Default.left()

context(EffectScope<DomainError>) suspend inline fun <A> bind(
    crossinline f: suspend () -> Either<DomainError, A>
): A =
    f().bind()

context(EffectScope<DomainError>) suspend inline fun <A> bindNull(
    crossinline f: suspend () -> A?
): A =
    f().toEither().bind()

context(WithScope) inline fun <S : State, E> action(
    slice: Slice<S>,
    crossinline onError: context(Dispatch<Action>) (E) -> Unit = { _ -> },
    crossinline f: context(EffectScope<E>, Dispatch<Action>) (S) -> Unit
): Thunk =
    createAction(slice) { s, d ->
        eitherIo(onError = { onError(d, it) }, f = { f(this, d, s) })
    }

context(WithScope) fun <S : State, E> action(
    key: String? = null,
    onError: suspend context(Dispatch<Action>) (E) -> Unit = { _ -> },
    f: suspend context(EffectScope<E>, Dispatch<Action>) (S) -> Unit
): AsyncAction<S> =
    createAction { s, d ->
        eitherIo(
            onError = { onError(d, it) },
            f = { f(this, d, s) }
        ).also { job ->
            key?.let {
                jobs[key]?.cancel().also { println("$key : $it") }
                jobs[key] = job.also { println("$key : $it") }
            }
        }
    }

context(WithScope) inline fun <S : State> actionMain(
    crossinline f: context(EffectScope<DomainError>, Dispatch<Action>) (S) -> Unit
): AsyncAction<S> =
    createAction { s, d ->
        eitherMain { f(this, d, s) }
    }