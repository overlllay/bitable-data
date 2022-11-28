package com.harmony.bitable

import com.harmony.bitable.domain.Book
import com.harmony.bitable.repository.BitableRepository
import com.harmony.bitable.repository.config.EnableBitableRepositories
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Repository

@EnableBitableRepositories
@SpringBootApplication
class BookApplication {

    private val log = LoggerFactory.getLogger(BookApplication::class.java)

    @Bean
    fun displayAll(bookRepository: BookRepository): CommandLineRunner {
        return CommandLineRunner {

            val cursor = bookRepository.scan {
                Book::name contains "三体"
            }

            for (book in cursor) {
                println("book: $book")
            }

        }
    }

}

fun main(vararg args: String) {
    runApplication<BookApplication>(*args)
}

@Repository
interface BookRepository : BitableRepository<Book, String>
