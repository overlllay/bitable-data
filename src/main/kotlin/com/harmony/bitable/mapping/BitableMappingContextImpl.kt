package com.harmony.bitable.mapping

import com.harmony.bitable.BitityService
import com.harmony.bitable.core.BitableSource
import org.springframework.data.mapping.context.AbstractMappingContext
import org.springframework.data.mapping.model.Property
import org.springframework.data.mapping.model.SimpleTypeHolder
import org.springframework.data.util.TypeInformation

internal class BitableMappingContextImpl(
    private val bitableSource: BitableSource,
    private val bitityService: BitityService,
) : AbstractMappingContext<BitablePersistentEntity<*>, BitablePersistentProperty>(),
    BitableMappingContext {

    init {
        this.setSimpleTypeHolder(BITABLE_SIMPLE_TYPE_HOLDER)
    }

    override fun <T : Any> createPersistentEntity(typeInformation: TypeInformation<T>): BitablePersistentEntity<*> {
        val bitity = bitityService.getBitity(typeInformation.type)
        val table = bitableSource.getTable(bitity.getName())
        return BitablePersistentEntityImpl(typeInformation, table, bitity)
    }

    override fun createPersistentProperty(
        property: Property,
        owner: BitablePersistentEntity<*>,
        simpleTypeHolder: SimpleTypeHolder,
    ) = BitablePersistentPropertyImpl(property, owner, simpleTypeHolder)

    companion object {
        private val BITABLE_SIMPLE_TYPE_HOLDER = BitableSimpleTypeHolder()
    }

    private class BitableSimpleTypeHolder : SimpleTypeHolder(emptySet(), true) {

        override fun isSimpleType(type: Class<*>): Boolean {
            if (type.name.startsWith("com.lark.oapi.service.bitable.v1.model")) {
                return true
            }
            return super.isSimpleType(type)
        }

    }

}
