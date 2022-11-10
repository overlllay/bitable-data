package com.harmony.bitable

interface BitityService {

    fun <T : Any> getBitity(type: Class<T>): Bitity<T>

}
