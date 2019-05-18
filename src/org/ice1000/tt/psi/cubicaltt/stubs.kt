package org.ice1000.tt.psi.cubicaltt

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.stubs.*
import com.intellij.psi.tree.IStubFileElementType
import org.ice1000.tt.CubicalTTLanguage
import org.ice1000.tt.psi.childrenWithLeaves
import org.ice1000.tt.psi.cubicaltt.impl.CubicalTTDataImpl
import org.ice1000.tt.psi.cubicaltt.impl.CubicalTTDefImpl
import org.ice1000.tt.psi.cubicaltt.impl.CubicalTTLabelImpl
import org.ice1000.tt.psi.cubicaltt.impl.CubicalTTModuleImpl

class CubicalTTFileStub(file: CubicalTTFileImpl?) : PsiFileStubImpl<CubicalTTFileImpl>(file) {
	override fun getType() = Type
	companion object Type : IStubFileElementType<PsiFileStubImpl<CubicalTTFileImpl>>(CubicalTTLanguage.INSTANCE) {
		override fun getStubVersion() = 1
		override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?) = CubicalTTFileStub(null)
		override fun serialize(stub: PsiFileStubImpl<CubicalTTFileImpl>, dataStream: StubOutputStream) = Unit
		override fun getExternalId() = "cubicaltt.file"
		override fun getBuilder() = object : DefaultStubBuilder() {
			override fun createStubForFile(file: PsiFile) = CubicalTTFileStub(file as? CubicalTTFileImpl)
		}
	}
}

fun factory(name: String): CubicalTTStubType<*, *> = when (name) {
	"MODULE" -> CubicalTTModuleStubType
	"DATA" -> CubicalTTDataStubType
	"LABEL" -> CubicalTTLabelStubType
	"DEF" -> CubicalTTDefStubType
	else -> error("Bad stub type: $name")
}

abstract class CubicalTTStubType<Stub: StubElement<*>, Psi: PsiElement>(
	debugName: String
) : IStubElementType<Stub, Psi>(debugName, CubicalTTLanguage.INSTANCE) {
	// `toString()` returns `debugName`
	override fun getExternalId() = "cubicaltt.${super.toString()}"
}

class CubicalTTDefStub(
	parent: StubElement<*>,
	val declName: String
) : StubBase<CubicalTTDef>(parent, CubicalTTDefStubType), StubElement<CubicalTTDef>

class CubicalTTLabelStub(
	parent: StubElement<*>,
	val labelName: String
) : StubBase<CubicalTTLabel>(parent, CubicalTTLabelStubType), StubElement<CubicalTTLabel>

class CubicalTTDataStub(
	parent: StubElement<*>,
	val dataName: String
) : StubBase<CubicalTTData>(parent, CubicalTTDataStubType), StubElement<CubicalTTData>

class CubicalTTModuleStub(
	parent: StubElement<*>,
	val moduleName: String
) : StubBase<CubicalTTModule>(parent, CubicalTTModuleStubType), StubElement<CubicalTTModule>

object CubicalTTModuleStubKey : StringStubIndexExtension<CubicalTTModule>() {
	private val KEY = StubIndexKey.createIndexKey<String, CubicalTTModule>(CubicalTTModuleStubType.externalId)
	override fun getKey() = KEY
}

object CubicalTTDefStubKey : StringStubIndexExtension<CubicalTTDef>() {
	private val KEY = StubIndexKey.createIndexKey<String, CubicalTTDef>(CubicalTTDefStubType.externalId)
	override fun getKey() = KEY
}

object CubicalTTLabelStubKey : StringStubIndexExtension<CubicalTTLabel>() {
	private val KEY = StubIndexKey.createIndexKey<String, CubicalTTLabel>(CubicalTTLabelStubType.externalId)
	override fun getKey() = KEY
}

object CubicalTTDataStubKey : StringStubIndexExtension<CubicalTTData>() {
	private val KEY = StubIndexKey.createIndexKey<String, CubicalTTData>(CubicalTTDataStubType.externalId)
	override fun getKey() = KEY
}

object CubicalTTModuleStubType : CubicalTTStubType<CubicalTTModuleStub, CubicalTTModule>("module") {
	override fun createPsi(stub: CubicalTTModuleStub): CubicalTTModule = CubicalTTModuleImpl(stub, this)
	override fun createStub(psi: CubicalTTModule, parentStub: StubElement<*>) = CubicalTTModuleStub(parentStub, psi.nameDecl?.text.orEmpty())
	override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>) = CubicalTTModuleStub(parentStub, dataStream.readName()?.string.orEmpty())
	override fun serialize(stub: CubicalTTModuleStub, dataStream: StubOutputStream) = dataStream.writeName(stub.moduleName)
	override fun indexStub(stub: CubicalTTModuleStub, sink: IndexSink) {
		sink.occurrence(CubicalTTModuleStubKey.key, stub.moduleName)
	}
}

object CubicalTTDefStubType : CubicalTTStubType<CubicalTTDefStub, CubicalTTDef>("decl") {
	override fun createPsi(stub: CubicalTTDefStub) = CubicalTTDefImpl(stub, this)
	override fun createStub(psi: CubicalTTDef, parentStub: StubElement<*>) = CubicalTTDefStub(parentStub, psi.childrenWithLeaves.firstOrNull { it is CubicalTTNameDecl }?.text.orEmpty())
	override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>) = CubicalTTDefStub(parentStub, dataStream.readName()?.string.orEmpty())
	override fun serialize(stub: CubicalTTDefStub, dataStream: StubOutputStream) = dataStream.writeName(stub.declName)
	override fun shouldCreateStub(node: ASTNode?): Boolean {
		val parent = node?.psi?.parent
		if (parent is CubicalTTLetExp) return false
		if (parent is CubicalTTExpWhere) return false
		return super.shouldCreateStub(node)
	}

	override fun indexStub(stub: CubicalTTDefStub, sink: IndexSink) {
		sink.occurrence(CubicalTTDefStubKey.key, stub.declName)
	}
}

object CubicalTTLabelStubType : CubicalTTStubType<CubicalTTLabelStub, CubicalTTLabel>("label") {
	override fun createPsi(stub: CubicalTTLabelStub) = CubicalTTLabelImpl(stub, this)
	override fun createStub(psi: CubicalTTLabel, parentStub: StubElement<*>) = CubicalTTLabelStub(parentStub, psi.childrenWithLeaves.firstOrNull { it is CubicalTTNameDecl }?.text.orEmpty())
	override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>) = CubicalTTLabelStub(parentStub, dataStream.readName()?.string.orEmpty())
	override fun serialize(stub: CubicalTTLabelStub, dataStream: StubOutputStream) = dataStream.writeName(stub.labelName)
	override fun indexStub(stub: CubicalTTLabelStub, sink: IndexSink) {
		sink.occurrence(CubicalTTLabelStubKey.key, stub.labelName)
	}
}

object CubicalTTDataStubType : CubicalTTStubType<CubicalTTDataStub, CubicalTTData>("data") {
	override fun createPsi(stub: CubicalTTDataStub) = CubicalTTDataImpl(stub, this)
	override fun createStub(psi: CubicalTTData, parentStub: StubElement<*>) = CubicalTTDataStub(parentStub, psi.nameDecl?.text.orEmpty())
	override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>) = CubicalTTDataStub(parentStub, dataStream.readName()?.string.orEmpty())
	override fun serialize(stub: CubicalTTDataStub, dataStream: StubOutputStream) = dataStream.writeName(stub.dataName)
	override fun shouldCreateStub(node: ASTNode?): Boolean {
		val parent = node?.psi?.parent
		if (parent is CubicalTTLetExp) return false
		if (parent is CubicalTTExpWhere) return false
		return super.shouldCreateStub(node)
	}

	override fun indexStub(stub: CubicalTTDataStub, sink: IndexSink) {
		sink.occurrence(CubicalTTDataStubKey.key, stub.dataName)
	}
}
