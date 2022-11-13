package com.harmony.bitable

import org.springframework.data.mapping.model.Property

/**
 * 基于实体解析得出的于飞书表格映射关系
 */
interface Bitity<T : Any> : Iterable<BitityField> {

    fun getName(): String

    fun getType(): Class<T>

    fun getField(property: Property): BitityField?

    fun getField(predicate: (BitityField) -> Boolean): BitityField?

}
