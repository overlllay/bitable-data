package com.harmony.bitable

import org.springframework.data.mapping.model.Property

data class BitityField(
    val fieldId: String?,
    val fieldName: String,
    val fieldType: BitfieldType,
    val property: Property,
    val isRecordIdField: Boolean,
) {

    constructor(fieldId: String, source: BitityField) : this(
        fieldId = fieldId,
        fieldName = source.fieldName,
        fieldType = source.fieldType,
        property = source.property,
        isRecordIdField = source.isRecordIdField
    )

    override fun toString(): String {
        return "BitityField(name=${fieldName}, type=${fieldType})"
    }

}
