package com.harmony.bitable.repository

import com.harmony.bitable.filter.NameProvider
import com.harmony.bitable.filter.PredicateBuilder
import com.harmony.bitable.filter.RecordFilterBuilder
import com.harmony.bitable.filter.RecordFilterPredicate
import com.harmony.lark.model.PageCursor
import org.springframework.data.repository.CrudRepository

interface BitableRepository<T : Any, ID> : CrudRepository<T, ID> {

    fun scan(): PageCursor<T>

    fun scan(predicate: RecordFilterPredicate): PageCursor<T>

    fun scan(predicate: PredicateBuilder<T>.() -> Unit): PageCursor<T>

    fun filter(predicate: RecordFilterBuilder<T>.() -> Unit): PageCursor<T>

}
