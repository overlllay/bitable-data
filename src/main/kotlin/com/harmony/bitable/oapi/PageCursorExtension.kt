package com.harmony.bitable.oapi

import com.harmony.lark.model.PageCursor
import com.harmony.lark.utils.PageUtils
import org.springframework.data.util.Streamable

private fun <T, R> identity(): (T) -> R = { it -> it as R }

fun <DATA> PageCursor<DATA>.toStream() = Streamable.of(Iterable { this })

fun <DATA> PageCursor<DATA>.toList(): List<DATA> = Streamable.of(Iterable { this }).toList()

fun <DATA, R> PageCursor<DATA>.toIterable(converter: (DATA) -> R = identity()): Iterable<R> {
    return LazyIterator(this.toStream().map { converter(it) })
}

fun <DATA, R> PageCursor<DATA>.convert(converter: (DATA) -> R = identity()): PageCursor<R> {
    return PageUtils.convert(this, converter)
}

fun <DATA, R> PageCursor<DATA>.toDisposableIterable(converter: (DATA) -> R = identity()) = Iterable {
    DisposableIterator(this, converter)
}

private class DisposableIterator<DATA, R>(
    private val iterator: Iterator<DATA>,
    private val converter: (DATA) -> R,
) : Iterator<R> {

    override fun hasNext() = iterator.hasNext()

    override fun next() = converter(iterator.next())

}


private class LazyIterator<R>(private val streamable: Streamable<R>) : Iterable<R> {

    private var list: List<R>? = null

    override fun iterator(): Iterator<R> {
        return toList().iterator()
    }

    private fun toList(): List<R> {
        if (list != null) {
            return list!!
        }
        synchronized(this) {
            if (list == null) {
                list = streamable.toList()
            }
        }
        return list!!
    }

}
