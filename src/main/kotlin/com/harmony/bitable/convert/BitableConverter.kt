package com.harmony.bitable.convert

import com.harmony.bitable.mapping.BitableMappingContext
import com.harmony.bitable.mapping.BitablePersistentEntity
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import org.springframework.data.convert.EntityConverter

interface BitableConverter :
    EntityConverter<BitablePersistentEntity<*>, BitablePersistentProperty, Any, AppTableRecord> {

    override fun getMappingContext(): BitableMappingContext

    fun addConverter(converter: BitfieldConverter)

}
