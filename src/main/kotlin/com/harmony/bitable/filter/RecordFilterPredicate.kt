package com.harmony.bitable.filter

/**
 * @see com.lark.oapi.service.bitable.v1.model.ListAppTableRecordReq
 */
interface RecordFilterPredicate {

    companion object {

        fun ofFilter(filter: String): RecordFilterPredicate {
            return object : RecordFilterPredicate {
                override fun getFilter(): String = filter
            }
        }

    }

    fun getPageToken(): String? = null

    fun getFieldNames(): String? = null

    fun getViewId(): String? = null

    fun getFilter(): String

    fun getSort(): String? = null

}
