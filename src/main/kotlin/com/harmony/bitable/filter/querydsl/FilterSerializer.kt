package com.harmony.bitable.filter.querydsl

import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.harmony.bitable.filter.NameProvider
import com.harmony.bitable.model.Option
import com.querydsl.core.support.SerializerBase
import com.querydsl.core.types.Path
import com.querydsl.core.types.PathType
import com.querydsl.core.types.SubQueryExpression

class FilterSerializer(
    private val fieldNameProvider: NameProvider = NameProvider.identity(),
    private val serializer: (Any) -> String? = { GSON.toJson(it) },
) : SerializerBase<FilterSerializer>(FilterTemplates.DEFAULT) {

    companion object {
        private val GSON =
            GsonBuilder().registerTypeHierarchyAdapter(Option::class.java, OptionTypeAdapter())
                .disableHtmlEscaping()
                .create()
    }

    override fun visit(expr: SubQueryExpression<*>, context: Void?): Void? = null

    override fun visit(path: Path<*>, context: Void?): Void? {
        if (path.metadata.pathType != PathType.PROPERTY && path.metadata.parent != null) {
            return super.visit(path, context)
        }

        val template = getTemplate(path.metadata.pathType)
        val fieldName = fieldNameProvider.getFieldName(path.metadata.name)
        val args = listOf(path.metadata.parent, fieldName)

        handleTemplate(template, args)
        return null
    }

    override fun visitConstant(constant: Any?) {
        if (constant != null) {
            append(serializer(constant))
        }
    }

    private class OptionTypeAdapter : TypeAdapter<Option>() {
        override fun write(writer: JsonWriter?, value: Option?) {
            writer?.value(value?.getText())
        }

        override fun read(reader: JsonReader?): Option {
            throw UnsupportedOperationException("Unsupported read Option")
        }

    }

}
