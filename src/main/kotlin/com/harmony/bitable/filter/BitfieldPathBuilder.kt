package com.harmony.bitable.filter

import com.harmony.bitable.utils.NameFunctionUtils.getNameInformation
import com.querydsl.core.types.ConstantImpl
import com.querydsl.core.types.Ops
import com.querydsl.core.types.dsl.*
import kotlin.reflect.KMutableProperty1

internal class BitfieldPathBuilder<T : Any>(type: Class<T>, variable: String) : EntityPathBase<T>(type, variable) {

    fun <R> getAsSimple(name: NameInformation<R>): SimplePath<R> {
        val path = createSimple(name.property.name, name.property.type as Class<R>)
        return BitfieldSimplePath(path)
    }

    fun <R> getAsSimple(name: NameFunction<T, R>): SimplePath<R> {
        return getAsSimple(getNameInformation(name))
    }

    fun <R> getAsSimple(name: KMutableProperty1<T, R>): SimplePath<R> {
        return getAsSimple(getNameInformation(name))
    }

    fun <R : Comparable<*>> getAsComparable(name: NameInformation<R>): ComparablePath<R> {
        val path = createComparable(name.property.name, name.property.type as Class<R>)
        return BitfieldComparablePath(path)
    }

    fun <R : Comparable<*>> getAsComparable(name: KMutableProperty1<T, R>): ComparablePath<R> {
        return getAsComparable(getNameInformation(name))
    }

    fun <R : Comparable<*>> getAsComparable(name: NameFunction<T, R>): ComparablePath<R> {
        return getAsComparable(getNameInformation(name))
    }

    private class BitfieldSimplePath<T>(path: SimplePath<T>) : SimplePath<T>(path.type, path.metadata) {

        override fun `in`(vararg right: T): BooleanExpression {
            return `in`(listOf(*right))
        }

        override fun `in`(right: Collection<T>): BooleanExpression {
            return Expressions.booleanOperation(Ops.IN, mixin, ConstantImpl.create(right));
        }

        override fun notIn(right: Collection<T>): BooleanExpression {
            return Expressions.booleanOperation(Ops.NOT_IN, mixin, ConstantImpl.create(right));
        }

        override fun notIn(vararg right: T): BooleanExpression {
            return notIn(listOf(*right))
        }

    }

    private class BitfieldComparablePath<T : Comparable<*>>(path: ComparablePath<T>) :
        ComparablePath<T>(path.type, path.metadata) {

        override fun `in`(vararg right: T): BooleanExpression {
            return `in`(listOf(*right))
        }

        override fun `in`(right: Collection<T>): BooleanExpression {
            return Expressions.booleanOperation(Ops.IN, mixin, ConstantImpl.create(right));
        }

        override fun notIn(right: Collection<T>): BooleanExpression {
            return Expressions.booleanOperation(Ops.NOT_IN, mixin, ConstantImpl.create(right));
        }

        override fun notIn(vararg right: T): BooleanExpression {
            return notIn(listOf(*right))
        }

    }

}
