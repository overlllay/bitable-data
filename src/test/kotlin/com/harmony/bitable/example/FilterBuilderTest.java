package com.harmony.bitable.example;

import com.harmony.bitable.example.domain.Book;
import com.harmony.bitable.filter.FilterBuilder;

public class FilterBuilderTest {

    public static void main(String[] args) {
        FilterBuilder<Book> builder = new FilterBuilder<>(Book.class);

        String filter = builder.eq(Book::getName, "david")
                .ne(Book::getName, "mary")
                .goe(Book::getPrice, 100)
                .isNull(Book::getId)
                .isNotNull(Book::getName)
                .contains(Book::getName, "三体", "沙丘")
                .contains(Book::getName, "科幻")
                .notContains(Book::getName, "可爱", "言情")
                .build();

        System.out.println(filter);
    }

}


