package com.harmony.bitable.autoconfigure

import com.harmony.bitable.BitityService
import com.harmony.bitable.DefaultBitityService
import com.harmony.bitable.convert.*
import com.harmony.bitable.convert.MappingBitableConverter.Companion.DEFAULT_CONVERSION_SERVICE
import com.harmony.bitable.core.BitableSource
import com.harmony.bitable.core.BitableSourceImpl
import com.harmony.bitable.core.BitableTemplate
import com.harmony.bitable.mapping.BitableMappingContext
import com.harmony.bitable.mapping.BitableMappingContextImpl
import com.harmony.bitable.oapi.BitableApi
import com.harmony.bitable.oapi.BitableRecordApi
import com.harmony.lark.LarkClient
import com.harmony.lark.autoconfigure.LarkClientAutoConfiguration
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.core.convert.ConversionService
import org.springframework.data.mapping.model.EntityInstantiators

@ConditionalOnClass(LarkClient::class)
@EnableConfigurationProperties(BitableProperties::class)
@AutoConfigureAfter(LarkClientAutoConfiguration::class)
class BitableAutoConfiguration(private val bitableProperties: BitableProperties) {

    @Bean
    @ConditionalOnMissingBean(BitityService::class)
    fun bitityService(): BitityService = DefaultBitityService()

    @Bean
    @ConditionalOnBean(LarkClient::class)
    @ConditionalOnProperty(prefix = "bitable", name = ["app-token"])
    @ConditionalOnMissingBean(BitableSource::class)
    fun bitableSource(larkClient: LarkClient): BitableSource {
        return BitableSourceImpl(bitableProperties.appToken, BitableApi(larkClient))
    }

    @Bean
    @ConditionalOnBean(BitityService::class, BitableSource::class)
    @ConditionalOnMissingBean(BitableMappingContext::class)
    private fun bitableMappingContext(
        bitityService: BitityService,
        bitableSource: BitableSource,
    ): BitableMappingContext {
        return BitableMappingContextImpl(bitableSource, bitityService)
    }

    @Bean
    @ConditionalOnBean(BitableMappingContext::class)
    @ConditionalOnMissingBean(BitableConverter::class)
    fun bitableConverter(
        bitableMappingContext: BitableMappingContext,
        bitfieldConverter: ObjectProvider<BitfieldConverter>,
        @Qualifier(DEFAULT_CONVERSION_SERVICE) @Autowired(required = false) defaultConversionService: ConversionService?,
    ): BitableConverter {

        val bitableConverter = MappingBitableConverter(
            mappingContext = bitableMappingContext,
            entityInstantiators = EntityInstantiators(),
            defaultConversionService = defaultConversionService ?: BitableConverters.defaultConversionService()
        )

        bitfieldConverter.forEach { bitableConverter.addConverter(it) }

        return bitableConverter
    }

    @Bean
    @ConditionalOnBean(LarkClient::class, BitableMappingContext::class, BitableConverter::class)
    @ConditionalOnMissingBean(BitableTemplate::class)
    fun bitableTemplate(
        larkClient: LarkClient,
        bitableMappingContext: BitableMappingContext,
        bitableConverter: BitableConverter,
    ): BitableTemplate {
        return BitableTemplate(
            recordApi = BitableRecordApi(larkClient),
            mappingContext = bitableMappingContext,
            converter = bitableConverter
        )
    }

}
