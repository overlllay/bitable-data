package com.harmony.bitable.mapping

interface RecordIdAccessor {

    fun getRecordId(): String?

    fun getRequiredRecordId(): String {
        val recordId = getRecordId()
        if (recordId != null) {
            return recordId
        }
        throw IllegalStateException("Could not obtain recordId!")
    }

}
