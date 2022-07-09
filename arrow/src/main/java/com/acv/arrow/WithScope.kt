package com.acv.arrow

import arrow.core.*
import arrow.core.continuations.EffectScope
import arrow.core.continuations.either
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

interface WithScope : CoroutineScope {
    val io: CoroutineContext
    val default: CoroutineContext
    val jobs: MutableMap<String, Job>

    companion object : WithScope {
        override val io: CoroutineContext = IO
        override val default: CoroutineContext = Default
        override val jobs: MutableMap<String, Job> = mutableMapOf()
        override val coroutineContext: CoroutineContext = IO
    }
}

context (WithScope) inline fun <A, B> launchIO(
    crossinline action: suspend () -> Either<A, B>,
    crossinline error: (A) -> Unit = {},
    crossinline success: (B) -> Unit = {},
    crossinline before: () -> Unit = {},
    crossinline after: () -> Unit = {},
): Job =
    launchIO {
        before()
        action().fold(
            ifLeft = { error(it); after() },
            ifRight = { success(it); after() },
        )
    }

context (WithScope) fun launchIO(
    key: String? = null,
    block: suspend CoroutineScope.() -> Unit
): Job =
    launch(io, block = block).also { job ->
        key?.let {
            jobs[key]?.cancel().also { println("$key : $it") }
            jobs[key] = job.also { println("$key : $it") }
        }
    }

context (WithScope) suspend inline fun <T> io(noinline block: CoroutineScope.() -> T): T =
    withContext(io, block)

context (WithScope) fun <T> asyncIO(block: CoroutineScope.() -> T): Deferred<T> =
    async(io, block = block)

context(WithScope)  suspend inline fun <T> main(noinline block: CoroutineScope.() -> T): T =
    withContext(coroutineContext, block)

context (WithScope) inline fun <A, B> flowIO(
    crossinline f: suspend () -> Flow<Either<A, B>>,
    crossinline error: suspend (A) -> Unit = {},
    crossinline success: suspend (B) -> Unit = {}
): Job =
    launch(io) {
        f().collect {
            it.fold(error, success)
        }
    }

suspend inline fun <A, B, C> Either<A, B>.fold(
    crossinline ifLeft: suspend (A) -> C,
    crossinline ifRight: suspend (B) -> C
): C =
    when (this) {
        is Either.Right -> ifRight(value)
        is Either.Left -> ifLeft(value)
    }

context (WithScope) fun <A, E> eitherIo(
    onSuccess: suspend (A) -> Unit = {},
    onError: suspend (E) -> Unit = {},
    f: suspend EffectScope<E>.() -> A,
): Job =
    launch(io) {
        either(f).fold(onError, onSuccess)
    }

context (WithScope) inline fun <A> eitherMain(
    crossinline onSuccess: (A) -> Unit = {},
    crossinline onError: (DomainError) -> Unit = {},
    noinline f: EffectScope<DomainError>.() -> A,
): Job =
    launch(coroutineContext) {
        either(f).fold(onError, onSuccess)
    }

context (WithScope, EffectScope<DomainError>) fun <A> bindAsync(
    f: suspend () -> Either<DomainError, A>
): Deferred<A> =
    async(io) { f().bind() }

context (WithScope, EffectScope<DomainError>)  inline fun <A> then(
    f: () -> Either<DomainError, A>
): Either<DomainError, A> =
    f()

