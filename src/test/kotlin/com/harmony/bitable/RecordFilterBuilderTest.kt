package com.harmony.bitable

import com.harmony.bitable.domain.Book
import com.harmony.bitable.filter.filter

fun main() {

    val filter0 = filter<Book> {
        Book::name eq "三体" or (Book::author eq "刘慈欣")
        or {
            Book::introduction contains listOf("科技")
        }
    }

    println(filter0.build())

    val filter1 = filter<Book> {
        Book::name eq "三体" or (Book::author eq "刘慈欣")
        Book::price gt 100.0 and (Book::price lt 200.0)
    }

    println(filter1.build())


}
