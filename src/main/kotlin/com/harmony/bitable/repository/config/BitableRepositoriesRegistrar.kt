package com.harmony.bitable.repository.config

import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport
import org.springframework.data.repository.config.RepositoryConfigurationExtension

class BitableRepositoriesRegistrar : RepositoryBeanDefinitionRegistrarSupport() {

    override fun getAnnotation(): Class<out Annotation?> {
        return EnableBitableRepositories::class.java
    }

    override fun getExtension(): RepositoryConfigurationExtension {
        return BitableRepositoryConfigurationExtension()
    }

}
