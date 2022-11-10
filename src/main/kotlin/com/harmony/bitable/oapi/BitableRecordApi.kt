package com.harmony.bitable.oapi

import com.harmony.bitable.BitableAddress
import com.harmony.lark.LarkApiException
import com.harmony.lark.LarkClient
import com.harmony.lark.ensureData
import com.harmony.lark.model.PageCursor
import com.harmony.lark.service.bitable.listCursor
import com.lark.oapi.Client
import com.lark.oapi.core.request.RequestOptions
import com.lark.oapi.service.bitable.v1.model.*

class BitableRecordApi(larkClient: LarkClient, private val pageSize: Int = 20) {

    private val appTableRecord = larkClient.unwrap(Client::bitable).appTableRecord()

    @JvmOverloads
    fun create(
        address: BitableAddress,
        record: AppTableRecord,
        userIdType: String? = null,
        options: RequestOptions = RequestOptions(),
    ): AppTableRecord {

        val request = CreateAppTableRecordReq.newBuilder()
            .appToken(address.appToken)
            .tableId(address.tableId)
            .userIdType(userIdType)
            .appTableRecord(record)
            .build()

        return appTableRecord.create(request, options).ensureData().record
    }

    @JvmOverloads
    fun batchCreate(
        address: BitableAddress,
        records: List<AppTableRecord>,
        userIdType: String? = null,
        options: RequestOptions = RequestOptions(),
    ): List<AppTableRecord> {

        val recordsBody = BatchCreateAppTableRecordReqBody().apply {
            this.records = records.toTypedArray()
        }

        val request = BatchCreateAppTableRecordReq.newBuilder()
            .appToken(address.appToken)
            .tableId(address.tableId)
            .userIdType(userIdType)
            .batchCreateAppTableRecordReqBody(recordsBody)
            .build()

        return appTableRecord.batchCreate(request, options).ensureData().records.toList()
    }

    @JvmOverloads
    fun delete(
        address: BitableAddress,
        recordId: String,
        options: RequestOptions = RequestOptions(),
    ): Boolean {

        val request = DeleteAppTableRecordReq.newBuilder()
            .recordId(recordId)
            .appToken(address.appToken)
            .tableId(address.tableId)
            .build()

        return appTableRecord.delete(request, options).ensureData().deleted
    }

    @JvmOverloads
    fun batchDelete(
        address: BitableAddress,
        recordIds: List<String>,
        options: RequestOptions = RequestOptions(),
    ): Map<String, Boolean> {

        val recordIdsBody = BatchDeleteAppTableRecordReqBody().apply {
            this.records = recordIds.toTypedArray()
        }

        val request = BatchDeleteAppTableRecordReq.newBuilder()
            .appToken(address.appToken)
            .tableId(address.tableId)
            .batchDeleteAppTableRecordReqBody(recordIdsBody)
            .build()

        return appTableRecord.batchDelete(request, options)
            .ensureData()
            .records
            .associate { it.recordId to it.deleted }
    }

    @JvmOverloads
    fun update(
        address: BitableAddress,
        record: AppTableRecord,
        userIdType: String? = null,
        options: RequestOptions = RequestOptions(),
    ): AppTableRecord {

        val request = UpdateAppTableRecordReq.newBuilder()
            .recordId(record.recordId)
            .appToken(address.appToken)
            .tableId(address.tableId)
            .userIdType(userIdType)
            .appTableRecord(record)
            .build()

        return appTableRecord.update(request, options).ensureData().record
    }

    @JvmOverloads
    fun batchUpdate(
        address: BitableAddress,
        records: List<AppTableRecord>,
        userIdType: String? = null,
        options: RequestOptions = RequestOptions(),
    ): List<AppTableRecord> {

        val recordsBody = BatchUpdateAppTableRecordReqBody().apply {
            this.records = records.toTypedArray()
        }

        val request = BatchUpdateAppTableRecordReq.newBuilder()
            .appToken(address.appToken)
            .tableId(address.tableId)
            .userIdType(userIdType)
            .batchUpdateAppTableRecordReqBody(recordsBody)
            .build()

        return appTableRecord.batchUpdate(request, options).ensureData().records.toList()
    }

    @JvmOverloads
    fun get(
        address: BitableAddress,
        recordId: String,
        userIdType: String? = null,
        options: RequestOptions = RequestOptions(),
    ): AppTableRecord? {

        val request = GetAppTableRecordReq.newBuilder()
            .recordId(recordId)
            .tableId(address.tableId)
            .appToken(address.appToken)
            .userIdType(userIdType)
            .build()

        return get(request, options)
    }

    @JvmOverloads
    fun get(request: GetAppTableRecordReq, options: RequestOptions = RequestOptions()): AppTableRecord? {
        try {
            return appTableRecord.get(request, options).ensureData().record
        } catch (e: LarkApiException) {
            if (e.response.code == 1254043) {
                return null
            }
            throw e
        }
    }

    @JvmOverloads
    fun getFirst(
        address: BitableAddress,
        filter: String,
        userIdType: String? = null,
        options: RequestOptions = RequestOptions(),
    ): AppTableRecord? {
        val request = ListAppTableRecordReq.newBuilder()
            .appToken(address.appToken)
            .tableId(address.tableId)
            .userIdType(userIdType)
            .filter(filter)
            .build()
        return getFirst(request, options)
    }

    @JvmOverloads
    fun getFirst(request: ListAppTableRecordReq, options: RequestOptions = RequestOptions()): AppTableRecord? {
        val result = getTop2(request, options)
        return if (result.isEmpty()) null else result[0]
    }

    fun getOne(
        address: BitableAddress,
        filter: String,
        userIdType: String? = null,
        options: RequestOptions = RequestOptions(),
    ): AppTableRecord? {

        val request = ListAppTableRecordReq.newBuilder()
            .appToken(address.appToken)
            .tableId(address.tableId)
            .userIdType(userIdType)
            .filter(filter)
            .build()

        val matched = getTop2(request, options)

        if (matched.isEmpty()) {
            return null
        }

        if (matched.size > 1) {
            throw DuplicateRecordException()
        }

        return matched[0]
    }


    private fun getTop2(
        request: ListAppTableRecordReq,
        options: RequestOptions = RequestOptions(),
    ): List<AppTableRecord> {
        val result = appTableRecord.list(request.apply { pageSize = 2 }, options).ensureData()
        return if (result.total == 0) emptyList() else result.items.toList()
    }

    @JvmOverloads
    fun list(request: ListAppTableRecordReq, options: RequestOptions = RequestOptions()): PageCursor<AppTableRecord> {
        if (request.pageSize == null) {
            request.pageSize = pageSize
        }
        return appTableRecord.listCursor(request, options)
    }

    @JvmOverloads
    fun list(address: BitableAddress, options: RequestOptions = RequestOptions()): PageCursor<AppTableRecord> {
        val request = ListAppTableRecordReq.newBuilder()
            .appToken(address.appToken)
            .tableId(address.tableId)
            .pageSize(pageSize)
            .build()
        return list(request, options)
    }

    @JvmOverloads
    fun count(address: BitableAddress, options: RequestOptions = RequestOptions()): Int {
        val request = ListAppTableRecordReq.newBuilder()
            .appToken(address.appToken)
            .tableId(address.tableId)
            .pageSize(1)
            .build()
        return appTableRecord.list(request, options).ensureData().total
    }

}
