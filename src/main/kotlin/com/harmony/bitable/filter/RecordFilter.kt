package com.harmony.bitable.filter

/**
 * @see com.lark.oapi.service.bitable.v1.model.ListAppTableRecordReq
 */
interface RecordFilter {

    fun getPageSize(): Int = 20

    fun getPageToken(): String? = null

    fun getFieldNames(): String? = null

    fun getViewId(): String? = null

    fun getFilter(): String?

    fun getSort(): String? = null

}
