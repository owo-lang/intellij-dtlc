package org.ice1000.tt.gradle.json

import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.StructureKind
import kotlinx.serialization.UnionKind

/**
 * Base interface for a visitor of a [SerialDescriptor] tree,
 * which can return the result of processing the tree.
 *
 * Tree is formed from descriptors using [SerialDescriptor.getElementDescriptor] method.
 */
interface DescriptorVisitor<R> {
	fun visitDescriptor(descriptor: SerialDescriptor): R
}

/**
 * Skeleton implementation of [DescriptorVisitor]
 * which can be used to differentiate descriptors on a per-kind basis.
 */
abstract class BaseDescriptorVisitor<R> : DescriptorVisitor<R> {
	final override fun visitDescriptor(descriptor: SerialDescriptor): R = when (descriptor.kind) {
		is PrimitiveKind.STRING -> visitString(descriptor)
		is PrimitiveKind -> visitPrimitive(descriptor)
		is StructureKind.CLASS -> visitClass(descriptor)
		is StructureKind -> visitCollection(descriptor)
		is UnionKind.ENUM_KIND -> visitEnum(descriptor)
		is UnionKind -> visitUnion(descriptor)
	}

	abstract fun visitString(descriptor: SerialDescriptor): R
	abstract fun visitPrimitive(descriptor: SerialDescriptor): R
	abstract fun visitClass(descriptor: SerialDescriptor): R
	abstract fun visitCollection(descriptor: SerialDescriptor): R
	abstract fun visitEnum(descriptor: SerialDescriptor): R
	abstract fun visitUnion(descriptor: SerialDescriptor): R
}

/**
 * Visits [this] with a given [visitor].
 *
 * @see DescriptorVisitor
 * @see SerialDescriptor.getElementDescriptor
 */
fun <R> SerialDescriptor.accept(visitor: DescriptorVisitor<R>): R = visitor.visitDescriptor(this)

/**
 * Visits [this], making a [DescriptorVisitor] out of [visitor].
 *
 * @see DescriptorVisitor
 * @see SerialDescriptor.getElementDescriptor
 */
fun <R> SerialDescriptor.accept(visitor: (SerialDescriptor) -> R): R {
	val visitorImpl = object : DescriptorVisitor<R> {
		override fun visitDescriptor(descriptor: SerialDescriptor): R = visitor(descriptor)
	}
	return visitorImpl.visitDescriptor(this)
}
