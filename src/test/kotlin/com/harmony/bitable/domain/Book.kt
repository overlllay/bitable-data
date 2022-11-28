package com.harmony.bitable.domain

import com.harmony.bitable.BitfieldType
import com.harmony.bitable.annotations.BitId
import com.harmony.bitable.annotations.Bitable
import com.harmony.bitable.annotations.Bitfield
import com.lark.oapi.service.bitable.v1.model.Person
import org.springframework.data.annotation.Id
import java.time.LocalDateTime

@Bitable("书籍信息")
class Book {

    @Id
    @BitId
    var id: String? = null

    @Bitfield("书名")
    var name: String? = null

    @Bitfield("作者")
    var author: String? = null

    @Bitfield("单价")
    var price: Double = 0.0

    @Bitfield("标签")
    var tags: List<String> = listOf()

    @Bitfield("出版时间")
    var publishedAt: LocalDateTime? = null

    @Bitfield("内容简介")
    var introduction: String? = null

    @Bitfield(name = "创建时间", type = BitfieldType.CREATED_AT)
    var createdAt: LocalDateTime? = null

    @Bitfield(name = "创建人", type = BitfieldType.CREATED_BY)
    var createdBy: Person? = null

    @Bitfield(name = "修改时间", type = BitfieldType.UPDATED_AT)
    var updatedAt: LocalDateTime? = null

    @Bitfield(name = "修改人", type = BitfieldType.UPDATED_BY)
    var updatedBy: Person? = null

    override fun toString(): String {
        return String.format("%s(id=%s)", javaClass.simpleName, id)
    }

}
