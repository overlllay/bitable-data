package com.harmony.bitable.filter

import com.harmony.bitable.filter.Criteria.Companion.AND
import com.harmony.bitable.filter.Criteria.Companion.OR
import com.harmony.bitable.filter.Criteria.Companion.buildFromSource
import com.harmony.bitable.filter.querydsl.FilterSerializer
import com.querydsl.core.types.Predicate
import com.querydsl.kotlin.and
import com.querydsl.kotlin.or
import kotlin.reflect.KMutableProperty1

class RecordFilterBuilder<T : Any>(private val rootType: Class<T>) {

    private val root: BitfieldPathBuilder<T> = BitfieldPathBuilder(rootType, rootType.simpleName)

    private val names: MutableList<NameInformation<*>> = mutableListOf()

    private lateinit var predicateBuilder: PredicateBuilder<T>

    fun select(vararg names: KMutableProperty1<T, *>) {
        names.map { NameInformation.from(it) }.forEach { this.names.add(it) }
    }

    fun where(init: PredicateBuilder<T>.() -> Unit) {
        this.predicateBuilder = PredicateBuilder(rootType, root)
        init(this.predicateBuilder)
    }

    fun build(nameProvider: NameProvider = FilterBuilder.buildNameProvider(rootType)): RecordFilter {
        val filter = this.predicateBuilder.build(nameProvider)
        return SimpleRecordFilter(
            filter = filter,
            pageToken = null,
            fieldNames = null,
            viewId = null,
            sort = null
        )

    }

}

class PredicateBuilder<T : Any> internal constructor(
    private val rootType: Class<T>,
    private val root: BitfieldPathBuilder<T>,
) {

    private val allCriteria = mutableListOf<Criteria>()

    constructor(rootType: Class<T>) : this(rootType, BitfieldPathBuilder(rootType, rootType.simpleName))

    infix fun <R> KMutableProperty1<T, R>.eq(value: Any): Criteria {
        val predicate = root.getAsSimple(this).eq(value as R)
        return addPredicate(predicate)
    }

    infix fun <R> KMutableProperty1<T, R>.ne(value: Any): Criteria {
        val predicate = root.getAsSimple(this).ne(value as R)
        return addPredicate(predicate)
    }

    infix fun <R> KMutableProperty1<T, R>.contains(value: Collection<Any>): Criteria {
        val predicate = root.getAsSimple(this).`in`(value as Collection<R>)
        return addPredicate(predicate)
    }

    infix fun <R> KMutableProperty1<T, R>.contains(value: Any): Criteria {
        if (value is Collection<*>) {
            return this.contains(value as Collection<Any>)
        }
        return this.contains(listOf(value))
    }

    fun <R> KMutableProperty1<T, R>.isNull(): Criteria {
        val predicate = root.getAsSimple(this).isNull
        return addPredicate(predicate)
    }

    fun <R> KMutableProperty1<T, R>.isNotNull(): Criteria {
        val predicate = root.getAsSimple(this).isNotNull
        return addPredicate(predicate)
    }

    infix fun <R : Comparable<*>> KMutableProperty1<T, R>.gt(value: R): Criteria {
        val predicate = root.getAsComparable(this).gt(value)
        return addPredicate(predicate)
    }

    infix fun <R : Comparable<*>> KMutableProperty1<T, R>.goe(value: R): Criteria {
        val predicate = root.getAsComparable(this).goe(value)
        return addPredicate(predicate)
    }

    infix fun <R : Comparable<*>> KMutableProperty1<T, R>.lt(value: R): Criteria {
        val predicate = root.getAsComparable(this).lt(value)
        return addPredicate(predicate)
    }

    infix fun <R : Comparable<*>> KMutableProperty1<T, R>.loe(value: R): Criteria {
        val predicate = root.getAsComparable(this).loe(value)
        return addPredicate(predicate)
    }

    private fun addPredicate(
        predicate: Predicate,
        operation: ((Predicate, Predicate) -> Predicate) = AND,
    ): Criteria {
        val criteria = Criteria(predicate, operation)
        allCriteria.add(criteria)
        return criteria
    }

    fun and(init: PredicateBuilder<T>.() -> Unit) {
        val sub = PredicateBuilder(rootType, root)
        init(sub)
        val predicate = sub.buildPredicate()
        if (predicate != null) {
            addPredicate(predicate, AND)
        }
    }

    fun or(init: PredicateBuilder<T>.() -> Unit) {
        val sub = PredicateBuilder(rootType, root)
        init(sub)
        val predicate = sub.buildPredicate()
        if (predicate != null) {
            addPredicate(predicate, OR)
        }
    }

    private fun buildPredicate(): Predicate? {
        val availableCriteria = allCriteria.filter { !it.skip() }
        if (availableCriteria.isEmpty()) {
            return null
        }
        val iterator = availableCriteria.iterator()
        val first = buildFromSource(iterator.next())

        var current = first
        while (iterator.hasNext()) {
            val next = buildFromSource(iterator.next())
            current.applyNext(next)
            current = next
        }

        return first.build()
    }

    fun build(nameProvider: NameProvider = FilterBuilder.buildNameProvider(rootType)): String? {
        val predicate = buildPredicate() ?: return null
        val serializer = FilterSerializer(nameProvider)
        predicate.accept(serializer, null)
        return serializer.toString()
    }

}

class Criteria(
    private val predicate: Predicate,
    private var operation: ((Predicate, Predicate) -> Predicate),
) {

    private var processed = false

    private var previous: Criteria? = null

    private var next: Criteria? = null

    companion object {

        internal fun buildFromSource(source: Criteria): Criteria {
            return Criteria(source.build(), source.operation)
        }

        internal val AND: (Predicate, Predicate) -> Predicate = object : (Predicate, Predicate) -> Predicate {

            override fun invoke(p1: Predicate, p2: Predicate) = p1.and(p2)

            override fun toString(): String {
                return "AND"
            }

        }

        internal val OR: (Predicate, Predicate) -> Predicate = object : (Predicate, Predicate) -> Predicate {

            override fun invoke(p1: Predicate, p2: Predicate) = p1.or(p2)

            override fun toString(): String {
                return "OR"
            }

        }

    }


    infix fun and(next: Criteria): Criteria = applyNext(next, AND)

    infix fun or(next: Criteria): Criteria = applyNext(next, OR)

    internal fun applyNext(
        next: Criteria,
        operation: ((Predicate, Predicate) -> Predicate) = next.operation,
    ): Criteria {
        this.next = next

        next.previous = this
        next.processed = true
        next.operation = operation

        return this
    }

    fun skip(): Boolean = processed

    fun build(): Predicate {
        var current = this
        var result = current.predicate

        if (current.next == null) {
            return result
        }

        while (current.next != null) {
            val next = current.next!!
            result = next.operation(result, next.predicate)
            current = next
        }

        return result
    }

    override fun toString(): String {
        return predicate.toString()
    }

}

inline fun <reified T : Any> recordFilter(init: RecordFilterBuilder<T>.() -> Unit): RecordFilterBuilder<T> {
    val builder = RecordFilterBuilder<T>(T::class.java)
    init(builder)
    return builder
}

inline fun <reified T : Any> filter(init: PredicateBuilder<T>.() -> Unit): PredicateBuilder<T> {
    val builder = PredicateBuilder<T>(T::class.java)
    init(builder)
    return builder
}
