package com.harmony.bitable.annotations

import kotlin.annotation.AnnotationTarget.*

/**
 * 多维表格的索引列(飞书多维表格首列即为索引列, 仅允许更新不允许移动和删除)
 */
@Target(FIELD, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Bitdex
