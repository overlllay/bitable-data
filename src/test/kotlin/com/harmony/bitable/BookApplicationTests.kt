package com.harmony.bitable

import com.harmony.bitable.domain.Book
import com.harmony.lark.model.PageCursor
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BookApplicationTests {

    @Autowired
    lateinit var bookRepository: BookRepository

    @Test
    fun test() {
        bookRepository.scan().forEach {
            println("Book: id=${it.id} name=${it.name}")
        }
    }

    @Test
    fun query() {

        val cursor: PageCursor<Book> = bookRepository.filter {
            select(Book::name)
            where { Book::tags contains "科幻" }
            orderBy { Book::name.desc() }
        }

        for (book in cursor) {
            println("${book.id} with name ${book.name}")
        }

    }

}
