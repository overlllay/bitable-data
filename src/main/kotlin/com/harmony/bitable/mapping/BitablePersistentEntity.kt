package com.harmony.bitable.mapping

import com.harmony.bitable.BitityField
import com.harmony.bitable.BitableAddress
import org.springframework.data.mapping.model.MutablePersistentEntity
import org.springframework.data.mapping.model.Property

interface BitablePersistentEntity<T> : MutablePersistentEntity<T, BitablePersistentProperty> {

    fun getBitableAddress(): BitableAddress

    fun hasRecordIdProperty(): Boolean

    fun getRecordIdAccessor(bean: Any): RecordIdAccessor

    /**
     * 获取多维表格的行记录ID字段
     */
    fun getRecordIdProperty(): BitablePersistentProperty?

    fun getRequiredRecordIdProperty(): BitablePersistentProperty {
        val recordIdProperty = getRecordIdProperty()
        if (recordIdProperty != null) {
            return recordIdProperty
        }
        throw IllegalStateException(String.format("Required recordId property not found for %s!", type))
    }

    fun getBitableField(property: Property): BitityField?

    fun getRequiredBitableField(property: Property): BitityField {
        val field = getBitableField(property)
        if (field != null) {
            return field
        }
        throw IllegalStateException("Required field ${property.name} not found for ${name}!")
    }

}
