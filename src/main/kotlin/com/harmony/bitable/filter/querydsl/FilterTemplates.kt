package com.harmony.bitable.filter.querydsl

import com.querydsl.core.types.Ops
import com.querydsl.core.types.PathType
import com.querydsl.core.types.Templates

class FilterTemplates : Templates() {

    companion object {

        @JvmStatic
        val DEFAULT = FilterTemplates()

    }

    init {

        add(PathType.PROPERTY, "CurrentValue.[{1s}]")

        add(Ops.IN, "{0}.contains({1})")

        add(Ops.NOT_IN, "NOT({0}.contains({1}))")

        add(Ops.IS_NULL, "{0} = \"\"")

        add(Ops.IS_NOT_NULL, "NOT({0} = \"\")")

        add(Ops.OR, "OR({0}, {1})")

    }

}
