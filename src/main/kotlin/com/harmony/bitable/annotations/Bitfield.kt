package com.harmony.bitable.annotations

import com.harmony.bitable.BitfieldType
import org.springframework.core.annotation.AliasFor
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*

/**
 * 多维表格列配置
 */
@Target(FIELD, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
@Retention(RUNTIME)
annotation class Bitfield(

    @get:AliasFor("name")
    val value: String = "",

    /**
     * 多维表格列名称
     */
    @get:AliasFor("value")
    val name: String = "",

    /**
     * 列类型
     *
     * @see BitfieldType
     */
    val type: BitfieldType = BitfieldType.NONE,
)
