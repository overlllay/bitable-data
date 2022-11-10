package com.harmony.bitable.model

import com.google.gson.annotations.SerializedName
import com.harmony.bitable.BitfieldValue

class Attachment : BitfieldValue {

    @SerializedName("file_token")
    var fileToken: String? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("size")
    var size: Long? = null

    @SerializedName("tmp_url")
    var tmpUrl: String? = null

    @SerializedName("type")
    var type: String? = null

    @SerializedName("url")
    var url: String? = null

}
