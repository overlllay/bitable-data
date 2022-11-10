package com.harmony.bitable.annotations

import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*

/**
 * 多维表格行数据的 recordId, 同时也能兼任数据 ID 的作用
 *
 * @see AppTableRecord.getRecordId
 */
@Target(FIELD, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
@Retention(RUNTIME)
annotation class BitId
