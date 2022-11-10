package com.harmony.bitable.autoconfigure

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "bitable")
class BitableProperties {

    lateinit var appToken: String

}
