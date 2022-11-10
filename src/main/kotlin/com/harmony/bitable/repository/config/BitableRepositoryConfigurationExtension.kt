package com.harmony.bitable.repository.config

import com.harmony.bitable.annotations.Bitable
import com.harmony.bitable.repository.support.BitableRepositoryFactoryBean
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport

class BitableRepositoryConfigurationExtension : RepositoryConfigurationExtensionSupport() {

    override fun getModulePrefix(): String {
        return "Bitable"
    }

    override fun getRepositoryFactoryBeanClassName(): String {
        return BitableRepositoryFactoryBean::class.java.name
    }

    override fun getIdentifyingAnnotations(): Collection<Class<out Annotation?>> {
        return listOf<Class<out Annotation?>>(Bitable::class.java)
    }

}
