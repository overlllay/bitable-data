package com.harmony.bitable.convert

import com.harmony.bitable.mapping.BitableMappingContext
import com.harmony.bitable.mapping.BitablePersistentEntity
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import org.slf4j.LoggerFactory
import org.springframework.core.OrderComparator
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.TypeDescriptor
import org.springframework.data.mapping.Parameter
import org.springframework.data.mapping.PersistentPropertyAccessor
import org.springframework.data.mapping.model.EntityInstantiators
import org.springframework.data.mapping.model.ParameterValueProvider
import org.springframework.util.Assert

class MappingBitableConverter(
    private val mappingContext: BitableMappingContext,
    private val entityInstantiators: EntityInstantiators = EntityInstantiators(),
    defaultConversionService: ConversionService,
) : BitableConverter {

    companion object {

        private val log = LoggerFactory.getLogger(MappingBitableConverter::class.java)

        const val DEFAULT_CONVERSION_SERVICE = "bitableConversionService"

        private val NoOpParameterValueProvider = object : ParameterValueProvider<BitablePersistentProperty> {

            override fun <T : Any?> getParameterValue(parameter: Parameter<T, BitablePersistentProperty>) = null

        }

    }

    private val conversionService = InternalConversionService(defaultConversionService)

    override fun addConverter(converter: BitfieldConverter) {
        log.info("Add BitfieldConverter of {}", converter)
        this.conversionService.addConversionService(BitfieldConverter.wrapAsConversionService(converter))
    }

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
    ): Any? {
        val propertyValue = instanceAccessor.getProperty(property) ?: return null
        return conversionService.convert(propertyValue, property.getBitfieldType().type)
    }

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

    private class InternalConversionService(
        private val defaultConversionService: ConversionService,
    ) : ConversionService {

        private val customConversionServices = mutableListOf<ConversionService>()

        fun addConversionService(conversionService: ConversionService) {
            customConversionServices.add(conversionService)
            OrderComparator.sort(customConversionServices)
        }

        override fun canConvert(sourceType: Class<*>?, targetType: Class<*>): Boolean {
            Assert.notNull(targetType, "Target type to convert to cannot be null")
            return canConvert(
                if (sourceType != null) TypeDescriptor.valueOf(sourceType) else null,
                TypeDescriptor.valueOf(targetType)
            )
        }

        override fun <T : Any?> convert(source: Any?, targetType: Class<T>): T? {
            Assert.notNull(targetType, "Target type to convert to cannot be null")
            return convert(source, TypeDescriptor.forObject(source), TypeDescriptor.valueOf(targetType)) as T?
        }

        override fun canConvert(sourceType: TypeDescriptor?, targetType: TypeDescriptor): Boolean {
            return getConversionService(sourceType, targetType) != null
        }

        override fun convert(source: Any?, sourceType: TypeDescriptor?, targetType: TypeDescriptor): Any? {
            Assert.notNull(targetType, "Target type to convert to cannot be null")
            val converter = getConversionService(sourceType, targetType)
            if (converter != null) {
                return converter.convert(source, sourceType, targetType)
            }
            return defaultConversionService.convert(source, sourceType, targetType)
        }

        private fun getConversionService(sourceType: TypeDescriptor?, targetType: TypeDescriptor): ConversionService? {
            val converter = customConversionServices.firstOrNull {
                it.canConvert(sourceType, targetType)
            }
            if (converter != null) {
                return converter
            }
            if (defaultConversionService.canConvert(sourceType, targetType)) {
                return defaultConversionService
            }
            return null
        }

    }

}
