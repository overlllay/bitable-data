package com.harmony.bitable.repository.support

import com.harmony.bitable.core.BitableEntityInformation
import com.harmony.bitable.core.BitableOperations
import com.harmony.bitable.mapping.BitableMappingContext
import com.harmony.bitable.mapping.BitablePersistentEntity
import com.harmony.bitable.repository.BitableRepository
import org.springframework.data.repository.core.EntityInformation
import org.springframework.data.repository.core.RepositoryInformation
import org.springframework.data.repository.core.RepositoryMetadata
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport
import org.springframework.data.repository.core.support.RepositoryFactorySupport

class BitableRepositoryFactoryBean<T : BitableRepository<S, ID>, S, ID>(
    repositoryInterface: Class<T>,
    private val bitableOperations: BitableOperations,
    private val mappingContext: BitableMappingContext,
) : RepositoryFactoryBeanSupport<T, S, ID>(repositoryInterface) {

    override fun createRepositoryFactory(): RepositoryFactorySupport {
        return BitableRepositoryFactory(bitableOperations, mappingContext)
    }

    private class BitableRepositoryFactory(
        private val bitableOperations: BitableOperations,
        private val mappingContext: BitableMappingContext,
    ) : RepositoryFactorySupport() {

        override fun <T : Any, ID> getEntityInformation(domainClass: Class<T>): EntityInformation<T, ID> {
            val persistentEntity = mappingContext.getRequiredPersistentEntity(domainClass)
            return BitableEntityInformation(persistentEntity as BitablePersistentEntity<T>)
        }

        override fun getTargetRepository(metadata: RepositoryInformation): Any {
            val entityInformation: EntityInformation<out Any, Any> = getEntityInformation(metadata.domainType)
            return super.getTargetRepositoryViaReflection(metadata, entityInformation, bitableOperations)
        }

        override fun getRepositoryBaseClass(metadata: RepositoryMetadata): Class<*> {
            return SimpleBitableRepository::class.java
        }

    }


}
