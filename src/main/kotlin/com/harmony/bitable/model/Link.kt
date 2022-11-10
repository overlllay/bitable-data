package com.harmony.bitable.model

import com.google.gson.annotations.SerializedName
import com.harmony.bitable.BitfieldValue

class Link : BitfieldValue {

    @SerializedName("link")
    var link: String? = null

    @SerializedName("text")
    var text: String? = null

}
