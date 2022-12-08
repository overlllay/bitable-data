package com.harmony.bitable

import com.harmony.bitable.domain.Book
import com.harmony.bitable.domain.BookCategory
import com.harmony.bitable.filter.recordFilter
import com.lark.oapi.core.utils.Jsons

fun main() {

    val filter = recordFilter {
        select(Book::name)
        where {
            Book::tags contains BookCategory.XIAN_XIA
        }
        orderBy { Book::name.desc() }
    }

    val recordFilter = filter.build()
    println(Jsons.DEFAULT.toJson(recordFilter))

}
