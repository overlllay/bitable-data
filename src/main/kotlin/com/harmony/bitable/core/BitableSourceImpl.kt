package com.harmony.bitable.core

import com.harmony.bitable.oapi.BitableApi

class BitableSourceImpl(
    val appToken: String,
    private val bitableApi: BitableApi,
) : BitableSource {

    override fun getTable(name: String) = bitableApi.getTable(appToken, name)

}
