package com.harmony.bitable

import com.harmony.bitable.annotations.BitId
import com.harmony.bitable.annotations.Bitable
import com.harmony.bitable.annotations.Bitfield
import com.harmony.bitable.utils.BitityUtils
import com.harmony.bitable.utils.BitityUtils.getPropertyAnnotations
import com.harmony.bitable.utils.BitityUtils.getTypeBitfieldType
import org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation
import org.springframework.data.mapping.model.Property
import org.springframework.util.ClassUtils

internal class DefaultBitityService : BitityService {

    private val bitityCache = mutableMapOf<Class<*>, Bitity<*>>()

    override fun <T : Any> getBitity(type: Class<T>): Bitity<T> {
        return bitityCache.computeIfAbsent(type) { buildBitity(it) } as Bitity<T>
    }

    private fun <T : Any> buildBitity(type: Class<T>): Bitity<T> {
        val name = getBitityName(type)
        val fields = BitityUtils.getProperties(type).map { parseBitityField(it) }
        return BitityImpl(name, type, fields)
    }

    private fun getBitityName(type: Class<*>): String {
        val classType = ClassUtils.getUserClass(type)
        val bitable: Bitable? = findMergedAnnotation(classType, Bitable::class.java)
        if (bitable != null && bitable.name.isNotBlank()) {
            return bitable.name
        }
        return classType.simpleName
    }

    private fun parseBitityField(property: Property): BitityField {
        val propertyAnnotations = getPropertyAnnotations(property)
        val bitfield: Bitfield? = propertyAnnotations.getAnnotation(Bitfield::class.java)
        return BitityField(
            fieldId = null,
            fieldName = getFieldName(bitfield, property.name),
            fieldType = getFieldType(bitfield, property),
            property = property,
            isRecordIdField = propertyAnnotations.hasAnnotation(BitId::class.java)
        )
    }

    private fun getFieldType(bitfield: Bitfield?, property: Property): BitfieldType {
        if (bitfield != null && bitfield.type != BitfieldType.NONE) {
            return bitfield.type
        }
        return getTypeBitfieldType(property.type)
            ?: throw IllegalStateException("${property.type.simpleName} not have default bitfield type")
    }

    private fun getFieldName(bitfield: Bitfield?, name: String): String {
        return if (bitfield != null && bitfield.name.isNotBlank())
            bitfield.name
        else name
    }

    private class BitityImpl<T : Any>(
        private val name: String,
        private val type: Class<T>,
        private val fields: List<BitityField>,
    ) : Bitity<T> {

        private val fieldCache: Map<Property, BitityField> = fields.associateBy { it.property }

        override fun getName() = name

        override fun getType() = type

        override fun getField(property: Property) = fieldCache[property]

        override fun getField(predicate: (BitityField) -> Boolean): BitityField? {
            return fieldCache.values.firstOrNull(predicate)
        }

        override fun iterator(): Iterator<BitityField> = fields.iterator()

        override fun toString(): String {
            return "Bitity(name=$name, type=${type.simpleName})"
        }

    }

}
