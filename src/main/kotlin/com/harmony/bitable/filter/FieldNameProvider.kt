package com.harmony.bitable.filter

import com.harmony.bitable.Bitity
import com.harmony.bitable.BitityField

class FieldNameProvider(bitity: Bitity<*>) : NameProvider {

    private val fieldMap = bitity.associateBy { it.property.name }

    override fun getFieldName(name: String): String {
        val field: BitityField = fieldMap[name] ?: throw IllegalStateException("$name field not found")
        return field.fieldName
    }

}
