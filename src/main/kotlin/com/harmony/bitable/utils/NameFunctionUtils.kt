package com.harmony.bitable.utils

import com.harmony.bitable.filter.NameFunction
import com.harmony.bitable.filter.NameInformation
import org.springframework.data.mapping.model.Property
import org.springframework.util.ReflectionUtils.invokeMethod
import org.springframework.util.ReflectionUtils.makeAccessible
import java.lang.invoke.SerializedLambda
import java.lang.reflect.Modifier
import kotlin.jvm.internal.CallableReference
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1

object NameFunctionUtils {

    private val typePropertiesMap: MutableMap<Class<*>, List<Property>> = mutableMapOf()

    @JvmStatic
    fun <T, R> getNameInformation(nameFunction: NameFunction<T, R>): NameInformation<R> {
        val method = nameFunction::class.java.getDeclaredMethod("writeReplace")
        val info = with(method) {
            makeAccessible(this)
            invokeMethod(this, nameFunction) as SerializedLambda
        }
        val owner: Class<T> = forOwnerType(info.implClass)
        return getNameInformation(owner, info.implMethodName)
    }

    fun <T, R> getNameInformation(nameFunction: KMutableProperty1<T, R>): NameInformation<R> {
        val owner: Class<T> = ((nameFunction as CallableReference).owner as KClass<*>).java as Class<T>
        return getNameInformation(owner, nameFunction.name)
    }

    private fun <T, R> getNameInformation(owner: Class<T>, name: String): NameInformation<R> {
        val property = getTypeProperty(owner, name)
            ?: throw IllegalStateException("${owner.simpleName} not found property of $name")

        if (!property.field.isPresent) {
            throw IllegalStateException("${owner.simpleName} not have field of $name")
        }

        return NameInformation(owner, property)
    }

    private fun getTypeProperty(type: Class<*>, name: String): Property? {
        return getTypeProperties(type).firstOrNull {
            it.getter.isPresent && it.getter.get().name == name || it.field.isPresent && it.field.get().name == name
        }
    }

    private fun <T> forOwnerType(implClass: String): Class<T> {
        return Class.forName(implClass.replace("[/$]".toRegex(), ".")) as Class<T>
    }

    private fun getTypeProperties(type: Class<*>): List<Property> {
        return typePropertiesMap.computeIfAbsent(type) {
            BitityUtils.getProperties(type) {
                !Modifier.isStatic(it.modifiers) && !Modifier.isFinal(it.modifiers)
            }
        }
    }

}
