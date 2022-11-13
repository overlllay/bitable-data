package com.harmony.bitable.repository.support

import com.harmony.bitable.core.BitableEntityInformation
import com.harmony.bitable.core.BitableOperations
import com.harmony.bitable.filter.*
import com.harmony.bitable.repository.BitableRepository
import com.harmony.lark.model.PageCursor
import org.springframework.data.util.Streamable
import java.util.*

class SimpleBitableRepository<T : Any, ID : Any>(
    private val entityInformation: BitableEntityInformation<T, ID>,
    private val bitableOperations: BitableOperations,
) : BitableRepository<T, ID> {

    private val nameProvider: NameProvider = FilterBuilder.buildNameProvider(entityInformation.javaType)

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

    override fun scan(): PageCursor<T> = bitableOperations.scan(entityInformation.javaType)

    override fun scan(predicate: RecordFilterPredicate): PageCursor<T> {
        return bitableOperations.scan(entityInformation.javaType, predicate)
    }

    override fun filter(predicate: RecordFilterBuilder<T>.() -> Unit): PageCursor<T> {
        val builder = RecordFilterBuilder(entityInformation.javaType)
        predicate(builder)
        return scan(builder.build(nameProvider))
    }

    override fun scan(predicate: PredicateBuilder<T>.() -> Unit): PageCursor<T> {
        val builder = PredicateBuilder(entityInformation.javaType)
        predicate(builder)
        return scan(RecordFilterPredicate.ofFilter(builder.build(nameProvider)))
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
