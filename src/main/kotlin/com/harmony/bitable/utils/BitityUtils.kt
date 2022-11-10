package com.harmony.bitable.utils

import com.harmony.bitable.annotations.Bitfield
import com.harmony.bitable.model.Attachment
import com.harmony.bitable.model.Link
import com.harmony.bitable.model.User
import com.harmony.bitable.BitfieldType
import com.harmony.bitable.BitfieldType.*
import org.springframework.beans.BeanUtils
import org.springframework.core.annotation.AnnotatedElementUtils.getMergedAnnotation
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.data.mapping.model.Property
import org.springframework.data.util.ClassTypeInformation
import org.springframework.data.util.Optionals
import org.springframework.util.ClassUtils
import org.springframework.util.ReflectionUtils
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.util.*

/**
 * @author wuxin
 */
object BitityUtils {

    private val DEFAULT_TYPE_MAPPING = mapOf(
        Character::class.java to TEXT,
        String::class.java to TEXT,

        Number::class.java to NUMBER,

        Boolean::class.java to CHECKBOX,

        Date::class.java to DATE_TIME,
        LocalDate::class.java to DATE_TIME,
        LocalDateTime::class.java to DATE_TIME,
        YearMonth::class.java to DATE_TIME,

        User::class.java to USER,
        Link::class.java to LINK,
        Attachment::class.java to ATTACHMENT
    )

    private val DEFAULT_FIELD_FILTER: (Field) -> Boolean = {
        !Modifier.isStatic(it.modifiers)
                && !Modifier.isFinal(it.modifiers)
                && AnnotationUtils.findAnnotation(it, Bitfield::class.java) != null
    }

    private val DEFAULT_ANNOTATION_FILTER: (Annotation) -> Boolean = {
        it.annotationClass.java.name.startsWith("com.harmony.bitable.annotations")
    }

    fun getTypeBitfieldType(type: Class<*>): BitfieldType? {
        val bitfieldType = DEFAULT_TYPE_MAPPING[type]
        if (bitfieldType != null) {
            return bitfieldType
        }
        for (key in DEFAULT_TYPE_MAPPING.keys) {
            if (key.isAssignableFrom(type)) {
                return DEFAULT_TYPE_MAPPING[key]
            }
        }
        return null
    }

    fun getProperties(type: Class<*>, fieldFilter: (Field) -> Boolean = DEFAULT_FIELD_FILTER): List<Property> {
        val rawType = ClassUtils.getUserClass(type)
        val descriptors = BeanUtils.getPropertyDescriptors(rawType).associateBy { it.name }
        val typeInformation = ClassTypeInformation.from(rawType)

        val result = mutableListOf<Property>()

        ReflectionUtils.doWithFields(type, {

            ReflectionUtils.makeAccessible(it)
            val descriptor = descriptors[it.name]

            result.add(
                if (descriptor != null)
                    Property.of(typeInformation, it, descriptor)
                else
                    Property.of(typeInformation, it)
            )

        }, fieldFilter)

        return result
    }

    fun getPropertyAnnotations(
        property: Property,
        annotationFilter: (Annotation) -> Boolean = DEFAULT_ANNOTATION_FILTER,
    ): PropertyAnnotations {
        val result = mutableMapOf<Class<*>, Annotation>()

        Optionals.toStream(property.setter, property.getter, property.field).forEach { annotationElement ->
            val annotations = getAnnotations(annotationElement, annotationFilter)
            annotations.forEach { annotation ->
                val annotationType = annotation.annotationClass.java
                if (result.containsKey(annotationType)) {
                    throw IllegalStateException("Duplicate annotation of ${annotationType.simpleName} in ${property.name}")
                }
                result[annotationType] = annotation
            }
        }

        return PropertyAnnotations(result)
    }

    private fun getAnnotations(annotatedElement: AnnotatedElement, annotationFilter: (Annotation) -> Boolean) =
        annotatedElement.annotations
            .filter(annotationFilter)
            .map { getMergedAnnotation(annotatedElement, it.annotationClass.java)!! }

    class PropertyAnnotations(private val annotations: Map<Class<*>, Annotation>) {

        fun <T : Annotation> getAnnotation(annotationType: Class<T>): T? {
            val annotation = annotations[annotationType] ?: return null
            return annotation as T
        }

        fun hasAnnotation(annotationType: Class<*>): Boolean {
            return annotations[annotationType] != null
        }

    }

}
