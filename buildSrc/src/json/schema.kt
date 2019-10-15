package org.ice1000.tt.gradle.json

import kotlinx.serialization.*
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonLiteral
import kotlinx.serialization.json.JsonObject

internal val SerialDescriptor.jsonType
	get() = when (this.kind) {
		StructureKind.LIST -> "array"
		PrimitiveKind.BYTE, PrimitiveKind.SHORT, PrimitiveKind.INT, PrimitiveKind.LONG,
		PrimitiveKind.FLOAT, PrimitiveKind.DOUBLE -> "number"
		PrimitiveKind.STRING, PrimitiveKind.CHAR, UnionKind.ENUM_KIND -> "string"
		PrimitiveKind.BOOLEAN -> "boolean"
		else -> "object"
	}


/**
 * Creates an [JsonObject] which contains Json Schema of given [descriptor].
 *
 * Schema can contain following fields:
 * `description`, `type` for all descriptors;
 * `properties` and `required` for objects;
 * `enum` for enums;
 * `items` for arrays.
 *
 * User can modify this schema to add additional validation keywords
 * (as per [https://json-schema.org/latest/json-schema-validation.html])
 * if they want.
 */
fun schema(descriptor: SerialDescriptor): JsonObject {
	val properties: MutableMap<String, JsonObject> = mutableMapOf()
	val requiredProperties: MutableSet<String> = mutableSetOf()
	val isEnum = descriptor.kind == UnionKind.ENUM_KIND

	if (!isEnum) descriptor.elementDescriptors().forEachIndexed { index, child ->
		val elementName = descriptor.getElementName(index)
		properties[elementName] = child.accept(::schema)
		if (!descriptor.isElementOptional(index)) requiredProperties.add(elementName)
	}

	val jsonType = descriptor.jsonType
	val objectData: MutableMap<String, JsonElement> = mutableMapOf(
		"description" to JsonLiteral(descriptor.name),
		"type" to JsonLiteral(jsonType)
	)
	if (isEnum) {
		val allElementNames = (0 until descriptor.elementsCount).map(descriptor::getElementName)
		objectData += "enum" to JsonArray(allElementNames.map(::JsonLiteral))
	}
	when (jsonType) {
		"object" -> if (requiredProperties != setOf("0", "1")) {
			objectData["properties"] = JsonObject(properties)
			objectData["required"] = JsonArray(requiredProperties.map { JsonLiteral(it) })
		}
		"array" -> objectData["items"] = properties.values.let {
			check(it.size == 1) { "Array descriptor has returned inconsistent number of elements: expected 1, found ${it.size}" }
			it.first()
		}
		else -> { /* no-op */
		}
	}
	return JsonObject(objectData)
}
