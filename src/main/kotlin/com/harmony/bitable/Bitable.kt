package com.harmony.bitable

import com.lark.oapi.service.bitable.v1.model.AppTableField
import java.lang.IllegalArgumentException

data class Bitable(
    val name: String,
    val address: BitableAddress,
    val fields: List<AppTableField>,
) {

    private val fieldCache: Map<String, Int>

    init {
        this.fieldCache = mutableMapOf()
        fields.forEachIndexed { index, field ->
            fieldCache[field.fieldName] = index
        }
    }

    fun getField(index: Int): AppTableField {
        return fields[index]
    }

    fun getField(name: String): AppTableField {
        val index = fieldCache[name] ?: throw IllegalArgumentException("$name field not found in table ${this.name}")
        return getField(index)
    }

}

