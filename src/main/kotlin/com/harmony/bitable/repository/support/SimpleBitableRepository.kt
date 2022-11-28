package com.harmony.bitable.repository.support

import com.harmony.bitable.core.BitableEntityInformation
import com.harmony.bitable.core.BitableOperations
import com.harmony.bitable.filter.*
import com.harmony.bitable.repository.BitableRepository
import com.harmony.lark.model.PageCursor
import com.harmony.lark.model.Pageable
import org.springframework.data.util.Streamable
import java.util.*

class SimpleBitableRepository<T : Any, ID : Any>(
    private val entityInformation: BitableEntityInformation<T, ID>,
    private val bitableOperations: BitableOperations,
) : BitableRepository<T, ID> {

    private val entityName: String = entityInformation.javaType.simpleName

    private val nameProvider: NameProvider = FilterBuilder.buildNameProvider(entityInformation.javaType)

    override fun <S : T> save(entity: S) = bitableOperations.insert(entity)

    override fun <S : T> saveAll(entities: Iterable<S>): Iterable<S> {
        return bitableOperations.insertAll(entities)
    }

    override fun updateAll(entities: Iterable<T>): Iterable<T> {
        return bitableOperations.updateAll(entities)
    }

    override fun update(entity: T): T {
        return bitableOperations.update(entity)
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
        bitableOperations.deleteAll(entities)
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

    override fun scan(pageable: Pageable, closure: PredicateBuilder<T>.() -> Unit): PageCursor<T> {
        val filter = buildRecordFilter(pageable, closure)
        return scan(filter)
    }

    override fun getOne(id: ID): T {
        val entity = findById(id)
        if (entity.isPresent) {
            return entity.get()
        }
        throw IllegalStateException("$entityName of id $id not found")
    }

    override fun scan(recordFilter: RecordFilter): PageCursor<T> {
        return bitableOperations.scan(entityInformation.javaType, recordFilter)
    }

    override fun count(closure: PredicateBuilder<T>.() -> Unit): Int {
        val filter = buildRecordFilter(Pageable(1), closure)
        val cursor = scan(filter)
        return cursor.nextSlice().getTotal()
    }

    override fun firstOrNull(closure: PredicateBuilder<T>.() -> Unit): T? {
        val filter = buildRecordFilter(Pageable(1), closure)
        val cursor = scan(filter)
        return if (cursor.hasNext()) cursor.next() else null
    }

    override fun first(closure: PredicateBuilder<T>.() -> Unit): T {
        val filter = buildRecordFilter(Pageable(1), closure)
        val cursor = scan(filter)
        if (cursor.hasNext()) {
            return cursor.next()
        }
        throw IllegalStateException("$entityName data not found by filter ${filter.getFilter()}")
    }

    override fun filter(closure: RecordFilterBuilder<T>.() -> Unit): PageCursor<T> {
        val builder = RecordFilterBuilder(entityInformation.javaType)
        closure(builder)
        return scan(builder.build(nameProvider))
    }

    private fun buildRecordFilter(pageable: Pageable, closure: PredicateBuilder<T>.() -> Unit): RecordFilter {
        val builder = PredicateBuilder(entityInformation.javaType)
        closure(builder)
        val filter = builder.build(nameProvider)
        return SimpleRecordFilter(filter = filter, pageable = pageable)
    }

}
