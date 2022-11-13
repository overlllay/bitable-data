package com.harmony.bitable.repository.support

import com.harmony.bitable.core.BitableEntityInformation
import com.harmony.bitable.core.BitableOperations
import com.harmony.bitable.repository.BitableRepository
import org.springframework.data.util.Streamable
import java.util.*

class SimpleBitableRepository<T : Any, ID : Any>(
    private val entityInformation: BitableEntityInformation<T, ID>,
    private val bitableOperations: BitableOperations,
) : BitableRepository<T, ID> {

    override fun <S : T> save(entity: S) = bitableOperations.insert(entity)

    override fun <S : T> saveAll(entities: Iterable<S>): Iterable<S> {
        return Streamable.of(entities).map { save(entity = it) }
    }

    override fun findById(id: ID): Optional<T> {
        val entity = bitableOperations.findById(id, entityInformation.javaType) ?: return Optional.empty()
        return Optional.of(entity)
    }

    override fun existsById(id: ID): Boolean {
        return findById(id).isPresent
    }

    override fun findAll(): Iterable<T> = bitableOperations.findAll(entityInformation.javaType)

    override fun count(): Long = bitableOperations.count(entityInformation.javaType)

    override fun deleteAll() = bitableOperations.delete(entityInformation.javaType)

    override fun deleteAll(entities: Iterable<T>) {
        for (entity in entities) {
            bitableOperations.delete(entity)
        }
    }

    override fun deleteAllById(ids: Iterable<ID>) {
        for (id in ids) {
            bitableOperations.delete(id, entityInformation.javaType)
        }
    }

    override fun delete(entity: T) {
        bitableOperations.delete(entity)
    }

    override fun deleteById(id: ID) {
        bitableOperations.delete(id, entityInformation.javaType)
    }

    override fun findAllById(ids: Iterable<ID>): Iterable<T> {
        return Streamable.of(ids).map { findById(it).orElse(null) }.filterNotNull()
    }

}
