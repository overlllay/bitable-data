package com.harmony.bitable.oapi

import com.harmony.lark.model.PageCursor
import org.springframework.data.util.Streamable

fun <DATA> PageCursor<DATA>.stream() = Streamable.of(Iterable { this })

fun <DATA, R> PageCursor<DATA>.iterable(converter: (DATA) -> R) = Iterable { InternalIterator(this, converter) }


private class InternalIterator<DATA, R>(
    private val iterator: Iterator<DATA>,
    private val converter: (DATA) -> R,
) : Iterator<R> {

    override fun hasNext() = iterator.hasNext()

    override fun next() = converter(iterator.next())

}
