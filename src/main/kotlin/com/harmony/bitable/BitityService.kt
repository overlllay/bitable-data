package com.harmony.bitable

interface BitityService {

    companion object {

        fun defaultBitityService(): BitityService = DefaultBitityService()

    }

    fun <T : Any> getBitity(type: Class<T>): Bitity<T>

}
