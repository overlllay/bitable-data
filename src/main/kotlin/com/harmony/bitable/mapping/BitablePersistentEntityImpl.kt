package com.harmony.bitable.mapping

import com.harmony.bitable.Bitity
import com.harmony.bitable.BitityField
import com.harmony.bitable.Bitable
import com.lark.oapi.service.bitable.v1.model.AppTableField
import org.springframework.data.mapping.MappingException
import org.springframework.data.mapping.model.BasicPersistentEntity
import org.springframework.data.mapping.model.Property
import org.springframework.data.util.TypeInformation

internal class BitablePersistentEntityImpl<T : Any>(
    typeInformation: TypeInformation<T>,
    private val bitable: Bitable,
    private val bitity: Bitity<T>,
) : BasicPersistentEntity<T, BitablePersistentProperty>(typeInformation),
    BitablePersistentEntity<T> {

    private var recordIdProperty: BitablePersistentProperty? = null

    private val fieldNameMapping = mutableMapOf<String, String>()

    override fun getBitableAddress() = bitable.address

    override fun hasRecordIdProperty() = recordIdProperty != null

    override fun getRecordIdAccessor(bean: Any): RecordIdAccessor {
        return if (hasRecordIdProperty())
            RecordIdPropertyAccessor(bean, this)
        else
            ABSENT_RECORD_ID_ACCESSOR
    }

    override fun getRecordIdProperty() = recordIdProperty

    override fun getBitableField(property: Property): BitityField? {
        val bitityField: BitityField = bitity.getField(property) ?: return null

        if (bitityField.isRecordIdField) {
            return bitityField
        }

        val appTableField: AppTableField = bitable.getField(bitityField.fieldName)
        if (appTableField.type != bitityField.fieldType.value) {
            throw IllegalStateException(
                "${bitityField.fieldName} field type mismatch," +
                        " require ${bitityField.fieldType.value} but found ${appTableField.type}"
            )
        }

        return BitityField(appTableField.fieldId, bitityField)
    }

    override fun getPersistentProperty(name: String): BitablePersistentProperty? {
        val persistentProperty = super<BasicPersistentEntity>.getPersistentProperty(name)
        if (persistentProperty != null) {
            return persistentProperty
        }
        val mappedName = fieldNameMapping.get(name) ?: return null
        return super<BasicPersistentEntity>.getPersistentProperty(mappedName)
    }

    override fun addPersistentProperty(property: BitablePersistentProperty) {
        super.addPersistentProperty(property)

        fieldNameMapping[property.getBitfieldName()] = property.name

        if (property.isRecordIdProperty()) {
            val recordIdProperty = this.recordIdProperty

            if (recordIdProperty != null) {
                throw MappingException(
                    """Attempt to add recordId property ${property.field} 
                    |but already have property ${recordIdProperty.field} registered as recordId. 
                    |Check your mapping configuration!""".trimMargin()
                )
            }

            this.recordIdProperty = property
        }
    }

    private class RecordIdPropertyAccessor(
        target: Any,
        entity: BitablePersistentEntity<*>,
    ) : RecordIdAccessor {

        private val idRecordProperty = entity.getRecordIdProperty()!!

        private val accessor = entity.getPropertyAccessor(target)

        override fun getRecordId(): String? {
            val value = accessor.getProperty(idRecordProperty)
            if (value == null || value is String) {
                return value as String?
            }
            throw IllegalStateException("RecordId type error")
        }

    }

    companion object {

        private val ABSENT_RECORD_ID_ACCESSOR = object : RecordIdAccessor {
            override fun getRecordId(): String? {
                return null
            }
        }

    }

}
