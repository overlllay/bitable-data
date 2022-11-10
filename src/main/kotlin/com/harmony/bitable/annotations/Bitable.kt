package com.harmony.bitable.annotations

import org.springframework.core.annotation.AliasFor
import kotlin.annotation.AnnotationTarget.*

/**
 * 映射为飞书多维表格
 */
@Target(ANNOTATION_CLASS, CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Bitable(

    @get:AliasFor("name")
    val value: String = "",

    /**
     * 多维表格名称
     */
    @get:AliasFor("value")
    val name: String = "",
)
