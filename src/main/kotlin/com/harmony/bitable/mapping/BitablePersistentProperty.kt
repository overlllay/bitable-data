package com.harmony.bitable.mapping

import com.harmony.bitable.BitfieldType
import org.springframework.data.mapping.PersistentProperty

interface BitablePersistentProperty : PersistentProperty<BitablePersistentProperty> {

    /**
     * 多维表给列ID
     */
    fun getBitfieldId(): String?

    /**
     * 多维表格列名
     */
    fun getBitfieldName(): String

    /**
     * 多维表格列类型
     */
    fun getBitfieldType(): BitfieldType

    /**
     * 判断是否是多维表格的索引列(首列)
     */
    fun isRecordIdProperty(): Boolean

}
