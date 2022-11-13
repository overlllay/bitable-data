package com.harmony.bitable.filter

interface NameProvider {

    companion object {

        fun identity(): NameProvider = object : NameProvider {
            override fun getFieldName(name: String): String = name
        }

    }

    /**
     * 将名称映射为实际查询的字段名称
     */
    fun getFieldName(name: String): String

}
