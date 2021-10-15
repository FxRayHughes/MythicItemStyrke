package tkworld.tools.mythicitemstyrke.serializer

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class SerializerType(val baseClass: KClass<*>)