package com.harmony.bitable.oapi

import com.harmony.bitable.Bitable
import com.harmony.bitable.BitableAddress
import com.harmony.lark.LarkClient
import com.harmony.lark.service.bitable.listCursor
import com.lark.oapi.Client
import com.lark.oapi.service.bitable.v1.model.AppTable
import com.lark.oapi.service.bitable.v1.model.AppTableField
import com.lark.oapi.service.bitable.v1.model.ListAppTableFieldReq
import com.lark.oapi.service.bitable.v1.model.ListAppTableReq

class BitableApi(larkClient: LarkClient, private val pageSize: Int = 20) {

    private val appTable = larkClient.unwrap(Client::bitable).appTable()

    private val appTableField = larkClient.unwrap(Client::bitable).appTableField()

    fun getTable(appToken: String, tableName: String): Bitable {
        val appTable = getAppTable(appToken) { it.name == tableName }
            ?: throw IllegalStateException("$tableName table not found")
        val fields = getAppTableFields(appToken, appTable.tableId)
        val address = BitableAddress(appToken, appTable.tableId)
        return Bitable(name = appTable.name, address = address, fields = fields)
    }

    fun getTable(address: BitableAddress): Bitable {
        val appTable = getAppTable(address.appToken) { it.tableId == address.tableId }
            ?: throw IllegalStateException("${address.tableId} table not found")
        val fields = getAppTableFields(address.appToken, address.tableId)
        return Bitable(name = appTable.name, address = address, fields = fields)
    }

    private fun getAppTableFields(appToken: String, tableId: String): List<AppTableField> {
        val request = ListAppTableFieldReq.newBuilder()
            .appToken(appToken)
            .tableId(tableId)
            .pageSize(pageSize)
            .build()
        return appTableField.listCursor(request).toList()
    }

    private fun getAppTable(appToken: String, predicate: (AppTable) -> Boolean): AppTable? {
        val request = ListAppTableReq().apply {
            this.appToken = appToken
            this.pageSize = this@BitableApi.pageSize
        }
        return appTable.listCursor(request).toStream().firstOrNull(predicate)
    }

}
