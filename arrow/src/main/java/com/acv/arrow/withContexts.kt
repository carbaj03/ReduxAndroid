@file:OptIn(ExperimentalContracts::class, ExperimentalContracts::class)

package com.acv.arrow

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

sealed interface TypeWrapper<out A> {
    object IMPL : TypeWrapper<Nothing>
}

data class Context<A>(val a : A)
data class Context2<A, B>(val a : A, val b : B)
data class Context3<A, B, C>(val a : A, val b : B, val c : C)

inline fun <R, T1, T2> withContext(
    param1: T1,
    param2: T2,
    block: context(T1, T2) (TypeWrapper<T2>) -> R,
): R {
    return block(param1, param2, TypeWrapper.IMPL)
}

inline fun <A, R> withContexts(a: A, block: context(A) (TypeWrapper<A>) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(a, TypeWrapper.IMPL)
}

inline fun <A, B, R> withContexts(a: A, b: B, block: context(A, B) (TypeWrapper<B>) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(a, b, TypeWrapper.IMPL)
}

inline fun <A, B, C, R> withContexts(a: A, b: B, c: C, block: context(A, B, C) (TypeWrapper<C>) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(a, b, c, TypeWrapper.IMPL)
}

inline fun <A, B, C, D, R> withContexts(a: A, b: B, c: C, d: D, block: context(A, B, C, D) (TypeWrapper<D>) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(a, b, c, d, TypeWrapper.IMPL)
}

inline fun <A, B, C, D, E, R> withContexts(a: A, b: B, c: C, d: D, e: E, block: context(A, B, C, D, E) (TypeWrapper<E>) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(a, b, c, d, e, TypeWrapper.IMPL)
}

inline fun <A, B, C, D, E, F, R> withContexts(a: A, b: B, c: C, d: D, e: E, f: F, block: context(A, B, C, D, E, F) (TypeWrapper<F>) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(a, b, c, d, e, f, TypeWrapper.IMPL)
}

inline fun <A, B, C, D, E, F, G, R> withContexts(a: A, b: B, c: C, d: D, e: E, f: F, g: G, block: context(A, B, C, D, E, F, G) (TypeWrapper<G>) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(a, b, c, d, e, f, g, TypeWrapper.IMPL)
}

inline fun <A, B, C, D, E, F, G, H, R> withContexts(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, block: context(A, B, C, D, E, F, G, H) (TypeWrapper<H>) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(a, b, c, d, e, f, g, h, TypeWrapper.IMPL)
}

inline fun <A, B, C, D, E, F, G, H, I, R> withContexts(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, block: context(A, B, C, D, E, F, G, H, I) (TypeWrapper<I>) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(a, b, c, d, e, f, g, h, i, TypeWrapper.IMPL)
}

inline fun <A, B, C, D, E, F, G, H, I, J, R> withContexts(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, block: context(A, B, C, D, E, F, G, H, I, J) (TypeWrapper<J>) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(a, b, c, d, e, f, g, h, i, j, TypeWrapper.IMPL)
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, R> withContexts(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, block: context(A, B, C, D, E, F, G, H, I, J, K) (TypeWrapper<K>) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(a, b, c, d, e, f, g, h, i, j, k, TypeWrapper.IMPL)
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, R> withContexts(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, block: context(A, B, C, D, E, F, G, H, I, J, K, L) (TypeWrapper<L>) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(a, b, c, d, e, f, g, h, i, j, k, l, TypeWrapper.IMPL)
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, R> withContexts(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, block: context(A, B, C, D, E, F, G, H, I, J, K, L, M) (TypeWrapper<M>) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(a, b, c, d, e, f, g, h, i, j, k, l, m, TypeWrapper.IMPL)
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, R> withContexts(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, block: context(A, B, C, D, E, F, G, H, I, J, K, L, M, N) (TypeWrapper<N>) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(a, b, c, d, e, f, g, h, i, j, k, l, m, n, TypeWrapper.IMPL)
}