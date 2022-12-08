package com.harmony.bitable.domain

import com.harmony.bitable.model.Option

enum class BookCategory(private val text: String) : Option {

    XIAN_XIA("仙侠"),

    SCIENCE_FICTION("科幻");

    @Override
    override fun getText(): String = text

}
