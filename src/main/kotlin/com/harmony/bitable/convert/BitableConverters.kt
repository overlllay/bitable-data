package com.harmony.bitable.convert

import com.google.gson.JsonElement
import com.harmony.bitable.model.Option
import com.lark.oapi.core.utils.Jsons
import com.lark.oapi.service.bitable.v1.model.Attachment
import com.lark.oapi.service.bitable.v1.model.Person
import com.lark.oapi.service.bitable.v1.model.Url
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.support.DefaultConversionService
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

object BitableConverters {

    fun defaultConversionService(): ConversionService {
        val conversionService = DefaultConversionService()
        conversionService.addConverter(DoubleToLocalDateTime())
        conversionService.addConverter(DoubleToLocalDate())
        conversionService.addConverter(MapToAttachment())
        conversionService.addConverter(MapToUser())
        conversionService.addConverter(MapToUrl())
        conversionService.addConverter(OptionToString())
        conversionService.addConverter(OptionArrayToStringArray())
        return conversionService
    }

    private fun <T> convert(source: Map<String, Any>, type: Class<T>): T? {
        val sourceValue: JsonElement = Jsons.DEFAULT.toJsonTree(source)
        return Jsons.DEFAULT.fromJson(sourceValue, type)
    }

    private class DoubleToLocalDateTime : Converter<Double, LocalDateTime> {
        override fun convert(source: Double): LocalDateTime {
            return Date(source.toLong()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        }
    }

    private class DoubleToLocalDate : Converter<Double, LocalDate> {
        override fun convert(source: Double): LocalDate {
            return Date(source.toLong()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        }
    }

    private class MapToUser : Converter<Map<String, Any>, Person> {
        override fun convert(source: Map<String, Any>): Person? {
            return convert(source, Person::class.java)
        }
    }

    private class MapToUrl : Converter<Map<String, Any>, Url> {
        override fun convert(source: Map<String, Any>): Url? {
            return convert(source, Url::class.java)
        }
    }

    private class MapToAttachment : Converter<Map<String, Any>, Attachment> {
        override fun convert(source: Map<String, Any>): Attachment? {
            return convert(source, Attachment::class.java)
        }
    }

    private class OptionToString : Converter<Option, String> {
        override fun convert(source: Option) = source.getText()

    }

    private class OptionArrayToStringArray : Converter<Array<Option>, Array<String>> {
        override fun convert(source: Array<Option>) = source.map { it.getText() }.toTypedArray()

    }

}
