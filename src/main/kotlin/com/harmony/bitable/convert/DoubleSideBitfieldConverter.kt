package com.harmony.bitable.convert

import org.springframework.core.convert.ConverterNotFoundException
import org.springframework.core.convert.TypeDescriptor

abstract class DoubleSideBitfieldConverter<SOURCE, TARGET>(
    protected val sourceType: Class<SOURCE>,
    protected val targetType: Class<TARGET>,
) : BitfieldConverter {

    override fun canConvert(sourceType: Class<*>?, targetType: Class<*>): Boolean {
        return isLeftToRight(sourceType, targetType) || isRightToLeft(sourceType, targetType)
    }

    override fun <T> convert(source: Any?, targetType: Class<T>): T? {

        val sourceType = source?.javaClass

        if (isLeftToRight(sourceType, targetType)) {
            return convertToTarget(source as SOURCE?, targetType as Class<TARGET>) as T?
        }

        if (isRightToLeft(sourceType, targetType)) {
            return convertToSource(source as TARGET?, targetType as Class<SOURCE>) as T?
        }

        throw ConverterNotFoundException(TypeDescriptor.forObject(source), TypeDescriptor.valueOf(targetType))
    }

    abstract fun convertToSource(target: TARGET?, type: Class<SOURCE>): SOURCE?

    abstract fun convertToTarget(source: SOURCE?, type: Class<TARGET>): TARGET?

    private fun isLeftToRight(sourceType: Class<*>?, targetType: Class<*>): Boolean {
        return (sourceType == null || isLeftType(sourceType)) && isRightType(targetType)
    }

    private fun isRightToLeft(sourceType: Class<*>?, targetType: Class<*>): Boolean {
        return sourceType == null || isRightType(sourceType) && isLeftType(targetType)
    }

    private fun isLeftType(type: Class<*>): Boolean {
        return sourceType == type || sourceType.isAssignableFrom(type)
    }

    private fun isRightType(type: Class<*>): Boolean {
        return targetType == type || targetType.isAssignableFrom(type)
    }

}
