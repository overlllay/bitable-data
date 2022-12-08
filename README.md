# bitable-data

飞书多维表格 + spring-data = bitable-data

### enable bitable repository

```kotlin
@EnableBitableRepositories
@SpringBootApplication
class BookApplication

fun main(vararg args: String) {
    runApplication<BookApplication>(*args)
}

@Repository
interface BookRepository : BitableRepository<Book, String>
```

### scan bitable record in dsl way

```kotlin
val cursor: PageCursor<Book> = bookRepository.filter {
    select(Book::name)
    where { Book::tags contains "科幻" } 
    orderBy { Book::name.desc() }
}

for (book in cursor) {
    println("${book.id} with name ${book.name}")
}
```
