package com.harmony.bitable.core

import com.harmony.bitable.Bitable

interface BitableSource {

    /**
     * 依据表名获取飞书表格信息
     *
     * @param name table name
     */
    fun getTable(name: String): Bitable

}
