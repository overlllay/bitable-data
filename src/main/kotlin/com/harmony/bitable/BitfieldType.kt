package com.harmony.bitable

import com.lark.oapi.service.bitable.v1.model.Attachment
import com.lark.oapi.service.bitable.v1.model.Location
import com.lark.oapi.service.bitable.v1.model.Person
import com.lark.oapi.service.bitable.v1.model.Url

import java.time.LocalDateTime

/**
 * [飞书多维表格的字段类型](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/bitable-v1/app-table-field/guide)
 */
enum class BitfieldType(val value: Int, val type: Class<*>) {

    NONE(0, Void::class.java),

    TEXT(1, String::class.java),

    NUMBER(2, Number::class.java),

    SINGLE_SELECT(3, String::class.java),

    MULTI_SELECT(4, Array<String>::class.java),

    DATE_TIME(5, LocalDateTime::class.java),

    CHECKBOX(7, Boolean::class.java),

    PERSON(11, Person::class.java),

    URL(15, Url::class.java),

    ATTACHMENT(17, Attachment::class.java),

    ASSOCIATION(18, String::class.java),

    FORMULA(20, String::class.java),

    CREATED_AT(1001, Double::class.java),

    UPDATED_AT(1002, Double::class.java),

    CREATED_BY(1003, PERSON::class.java),

    UPDATED_BY(1004, PERSON::class.java),

    AUTO_SERIAL(1005, String::class.java),

    PHONE_NUMBER(13, String::class.java),

    LOCATION(22, Location::class.java);

}
