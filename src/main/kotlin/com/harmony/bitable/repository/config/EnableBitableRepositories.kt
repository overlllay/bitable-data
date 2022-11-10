package com.harmony.bitable.repository.config

import com.harmony.bitable.repository.support.BitableRepositoryFactoryBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.data.repository.config.DefaultRepositoryBaseClass
import org.springframework.data.repository.query.QueryLookupStrategy
import java.lang.annotation.Inherited
import kotlin.reflect.KClass

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
@Import(BitableRepositoriesRegistrar::class)
annotation class EnableBitableRepositories(

    /**
     * Alias for the [.basePackages] attribute. Allows for more concise annotation declarations e.g.:
     * `@EnableRedisRepositories("org.my.pkg")` instead of
     * `@EnableRedisRepositories(basePackages="org.my.pkg")`.
     */
    vararg val value: String = [],
    /**
     * Base packages to scan for annotated components. [.value] is an alias for (and mutually exclusive with) this
     * attribute. Use [.basePackageClasses] for a type-safe alternative to String-based package names.
     */
    val basePackages: Array<String> = [],
    /**
     * Type-safe alternative to [.basePackages] for specifying the packages to scan for annotated components. The
     * package of each class specified will be scanned. Consider creating a special no-op marker class or interface in
     * each package that serves no purpose other than being referenced by this attribute.
     */
    val basePackageClasses: Array<KClass<*>> = [],
    /**
     * Specifies which types are not eligible for component scanning.
     */
    val excludeFilters: Array<ComponentScan.Filter> = [],
    /**
     * Specifies which types are eligible for component scanning. Further narrows the set of candidate components from
     * everything in [.basePackages] to everything in the base packages that matches the given filter or filters.
     */
    val includeFilters: Array<ComponentScan.Filter> = [],
    /**
     * Returns the postfix to be used when looking up custom repository implementations. Defaults to Impl. So
     * for a repository named `PersonRepository` the corresponding implementation class will be looked up scanning
     * for `PersonRepositoryImpl`.
     *
     * @return
     */
    val repositoryImplementationPostfix: String = "Impl",
    /**
     * Configures the location of where to find the Spring Data named queries properties file.
     *
     * @return
     */
    val namedQueriesLocation: String = "",
    /**
     * Returns the key of the [QueryLookupStrategy] to be used for lookup queries for query methods. Defaults to
     * [Key.CREATE_IF_NOT_FOUND].
     *
     * @return
     */
    val queryLookupStrategy: QueryLookupStrategy.Key = QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND,
    /**
     * Returns the [FactoryBean] class to be used for each repository instance. Defaults to
     * [BitableRepositoryFactoryBean].
     *
     * @return
     */
    val repositoryFactoryBeanClass: KClass<*> = BitableRepositoryFactoryBean::class,
    /**
     * Configure the repository base class to be used to create repository proxies for this particular configuration.
     *
     * @return
     */
    val repositoryBaseClass: KClass<*> = DefaultRepositoryBaseClass::class,
    /**
     * Configures the bean name of the [BitableOperations] to be used. Defaulted to bitableTemplate.
     *
     * @return
     */
    val bitableTemplateRef: String = "bitableTemplate",
)
