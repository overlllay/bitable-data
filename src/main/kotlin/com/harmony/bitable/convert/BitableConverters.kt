package com.harmony.bitable.convert

import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.support.DefaultConversionService
import org.springframework.core.convert.support.GenericConversionService
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

object BitableConverters {

    fun defaultConversionService(): GenericConversionService {
        val conversionService = DefaultConversionService()
        conversionService.addConverter(DoubleToLocalDateTime())
        conversionService.addConverter(DoubleToLocalDate())
        return conversionService
    }

    internal class DoubleToLocalDateTime : Converter<Double, LocalDateTime> {
        override fun convert(source: Double): LocalDateTime {
            return Date(source.toLong()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        }
    }

    internal class DoubleToLocalDate : Converter<Double, LocalDate> {
        override fun convert(source: Double): LocalDate {
            return Date(source.toLong()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        }
    }

}
