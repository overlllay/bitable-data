package com.harmony.bitable.filter

import com.harmony.bitable.BitityService
import com.harmony.bitable.filter.querydsl.FilterSerializer
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate

class FilterBuilder<T : Any>(private val rootType: Class<T>) {

    companion object {

        private val defaultBitityService = BitityService.defaultBitityService()

        fun buildNameProvider(rootType: Class<*>): NameProvider {
            val bitity = defaultBitityService.getBitity(rootType)
            return FieldNameProvider(bitity)
        }

    }

    private val root: BitfieldPathBuilder<T> = BitfieldPathBuilder(rootType, rootType.simpleName)
    private val predicate = BooleanBuilder()

    fun <R> eq(name: NameFunction<T, R>, value: Any): FilterBuilder<T> {
        val predicate = root.getAsSimple(name).eq(value as R)
        return addPredicate(predicate)
    }

    fun <R> ne(name: NameFunction<T, R>, value: Any): FilterBuilder<T> {
        val predicate = root.getAsSimple(name).ne(value as R)
        return addPredicate(predicate)
    }

    fun <R : Comparable<*>> gt(name: NameFunction<T, R>, value: Any): FilterBuilder<T> {
        val predicate = root.getAsComparable(name).gt(value as R)
        return addPredicate(predicate)
    }

    fun <R : Comparable<*>> goe(name: NameFunction<T, R>, value: Any): FilterBuilder<T> {
        val predicate = root.getAsComparable(name).goe(value as R)
        return addPredicate(predicate)
    }

    fun <R : Comparable<*>> lt(name: NameFunction<T, R>, value: Any): FilterBuilder<T> {
        val predicate = root.getAsComparable(name).lt(value as R)
        return addPredicate(predicate)
    }

    fun <R : Comparable<*>> loe(name: NameFunction<T, R>, value: Any): FilterBuilder<T> {
        val predicate = root.getAsComparable(name).loe(value as R)
        return addPredicate(predicate)
    }

    fun <R> contains(name: NameFunction<T, R>, values: List<Any>): FilterBuilder<T> {
        val predicate = root.getAsSimple(name).`in`(values as List<R>)
        return addPredicate(predicate)
    }

    fun <R> contains(name: NameFunction<T, R>, vararg value: Any): FilterBuilder<T> {
        return contains(name, listOf(*value))
    }

    fun <R> notContains(name: NameFunction<T, R>, values: List<Any>): FilterBuilder<T> {
        val predicate = root.getAsSimple(name).notIn(values as List<R>)
        return addPredicate(predicate)
    }

    fun <R> notContains(name: NameFunction<T, R>, vararg value: Any): FilterBuilder<T> {
        return notContains(name, listOf(*value))
    }

    fun <R> isNull(name: NameFunction<T, R>): FilterBuilder<T> {
        val predicate = root.getAsSimple(name).isNull
        return addPredicate(predicate)
    }

    fun <R> isNotNull(name: NameFunction<T, R>): FilterBuilder<T> {
        val predicate = root.getAsSimple(name).isNotNull
        return addPredicate(predicate)
    }

    @JvmOverloads
    fun build(nameProvider: NameProvider = buildNameProvider(rootType)): String {
        val filterSerializer = FilterSerializer(nameProvider)
        predicate.accept(filterSerializer, null)
        return filterSerializer.toString()
    }

    private fun addPredicate(predicate: Predicate): FilterBuilder<T> {
        this.predicate.and(predicate)
        return this
    }

}
