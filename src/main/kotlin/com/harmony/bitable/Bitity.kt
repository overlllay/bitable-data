package com.harmony.bitable

import org.springframework.data.mapping.model.Property

interface Bitity<T : Any> {

    fun getName(): String

    fun getType(): Class<T>

    fun getField(property: Property): BitityField?

}
