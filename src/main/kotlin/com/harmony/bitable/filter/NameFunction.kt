package com.harmony.bitable.filter

import java.io.Serializable

interface NameFunction<T, R> : java.util.function.Function<T, R>, Serializable
