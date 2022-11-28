package com.harmony.bitable.filter.querydsl

import com.harmony.bitable.filter.NameProvider
import com.harmony.bitable.model.Option
import com.querydsl.core.support.SerializerBase
import com.querydsl.core.types.Path
import com.querydsl.core.types.PathType
import com.querydsl.core.types.SubQueryExpression

class FilterSerializer(
    private val fieldNameProvider: NameProvider = NameProvider.identity(),
) : SerializerBase<FilterSerializer>(FilterTemplates.DEFAULT) {

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
            append(serializeValue(constant))
        }
    }

    protected fun serializeValue(value: Any): String {
        if (value is Number) {
            return value.toString()
        }
        if (value is Array<*>) {
            return value.joinToString(", ") { "\"" + serializeSingleValue(it) + "\"" }
        }
        if (value is Collection<*>) {
            return value.joinToString(", ") { "\"" + serializeSingleValue(it) + "\"" }
        }
        return "\"${serializeSingleValue(value)}\""
    }

    private fun serializeSingleValue(value: Any?): String {
        if (value == null) {
            return ""
        }
        if (value is Option) {
            return value.getText()
        }
        return value.toString().replace("\"", "\\\"")
    }

}
