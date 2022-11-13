package com.harmony.bitable

import java.net.URL

/**
 * 飞书多维表格在线地址
 */
data class BitableAddress(val appToken: String, val tableId: String) {

    fun toURL() = URL("https://feishu.cn/base/${appToken}?table=${tableId}")

}
