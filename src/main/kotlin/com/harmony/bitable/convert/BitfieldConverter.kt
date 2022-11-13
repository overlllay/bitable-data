package com.harmony.bitable.convert

import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.TypeDescriptor
import org.springframework.lang.Nullable

interface BitfieldConverter {

    fun canConvert(@Nullable sourceType: Class<*>?, targetType: Class<*>): Boolean

    fun <T> convert(@Nullable source: Any?, targetType: Class<T>): T?

    companion object {

        @JvmStatic
        fun wrapAsConversionService(converter: BitfieldConverter): ConversionService {
            return object : ConversionService {

                override fun canConvert(sourceType: Class<*>?, targetType: Class<*>): Boolean {
                    return converter.canConvert(sourceType, targetType)
                }

                override fun canConvert(sourceType: TypeDescriptor?, targetType: TypeDescriptor): Boolean {
                    return converter.canConvert(sourceType?.objectType, targetType.objectType)
                }

                override fun <T : Any?> convert(source: Any?, targetType: Class<T>): T? {
                    return converter.convert(source, targetType)
                }

                override fun convert(source: Any?, sourceType: TypeDescriptor?, targetType: TypeDescriptor): Any? {
                    return converter.convert(source, targetType.objectType)
                }

            }
        }
    }

}
