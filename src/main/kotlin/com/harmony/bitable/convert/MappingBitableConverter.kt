package com.harmony.bitable.convert

import com.harmony.bitable.convert.BitableConverters.defaultConversionService
import com.harmony.bitable.mapping.BitableMappingContext
import com.harmony.bitable.mapping.BitablePersistentEntity
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import org.springframework.core.convert.ConversionService
import org.springframework.data.mapping.Parameter
import org.springframework.data.mapping.PersistentPropertyAccessor
import org.springframework.data.mapping.model.EntityInstantiators
import org.springframework.data.mapping.model.ParameterValueProvider

class MappingBitableConverter(
    private val mappingContext: BitableMappingContext,
    private val conversionService: ConversionService = defaultConversionService(),
    private val entityInstantiators: EntityInstantiators = EntityInstantiators(),
) : BitableConverter {

    override fun <R : Any> read(type: Class<R>, source: AppTableRecord): R {
        val persistentEntity = mappingContext.getRequiredPersistentEntity(type)

        val instanceAccessor = createInstanceForAccessor(persistentEntity)

        val recordIdProperty = persistentEntity.getRecordIdProperty()
        if (recordIdProperty != null) {
            instanceAccessor.setProperty(recordIdProperty, source.recordId)
        }

        for (property in persistentEntity) {

            val fieldValue = if (property.isRecordIdProperty()) {
                source.recordId
            } else {
                readPropertyValue(property, source.fields)
            }

            instanceAccessor.setProperty(property, fieldValue)
        }

        return instanceAccessor.bean as R
    }

    override fun write(source: Any, sink: AppTableRecord) {
        val persistentEntity = mappingContext.getRequiredPersistentEntity(source.javaClass)
        val instanceAccessor = persistentEntity.getPropertyAccessor(source)

        val recordIdProperty = persistentEntity.getRecordIdProperty()
        if (recordIdProperty != null) {
            sink.recordId = instanceAccessor.getProperty(recordIdProperty)?.toString()
        }

        sink.fields = mutableMapOf()

        for (property in persistentEntity) {

            val fieldValue = getPropertyValue(property, instanceAccessor)

            if (property.isRecordIdProperty()) {
                sink.recordId = fieldValue?.toString()
            } else {
                sink.fields[property.getBitfieldName()] = fieldValue
            }

        }

    }

    override fun getMappingContext() = mappingContext

    override fun getConversionService(): ConversionService = conversionService

    private fun getPropertyValue(
        property: BitablePersistentProperty,
        instanceAccessor: PersistentPropertyAccessor<Any>,
    ) = instanceAccessor.getProperty(property)

    private fun readPropertyValue(property: BitablePersistentProperty, values: Map<String, Any>): Any? {
        val value = values[property.getBitfieldName()] ?: return null
        if (property.type.isAssignableFrom(value.javaClass)) {
            return value
        }

        return conversionService.convert(value, property.type)
    }

    private fun <R> createInstanceForAccessor(persistentEntity: BitablePersistentEntity<R>): PersistentPropertyAccessor<R> {

        val instantiator = entityInstantiators.getInstantiatorFor(persistentEntity)

        val instance = instantiator.createInstance(persistentEntity, NoOpParameterValueProvider)

        return persistentEntity.getPropertyAccessor(instance)
    }

    companion object {

        private val NoOpParameterValueProvider = object : ParameterValueProvider<BitablePersistentProperty> {

            override fun <T : Any?> getParameterValue(parameter: Parameter<T, BitablePersistentProperty>) = null

        }

    }

}
