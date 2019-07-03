package org.ice1000.tt.psi.yacctt

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.stubs.*
import com.intellij.psi.tree.IStubFileElementType
import icons.SemanticIcons
import org.ice1000.tt.YaccTTLanguage
import org.ice1000.tt.psi.childrenWithLeaves
import org.ice1000.tt.psi.yacctt.impl.YaccTTDataImpl
import org.ice1000.tt.psi.yacctt.impl.YaccTTDefImpl
import org.ice1000.tt.psi.yacctt.impl.YaccTTLabelImpl
import org.ice1000.tt.psi.yacctt.impl.YaccTTModuleImpl

class YaccTTFileStub(file: YaccTTFileImpl?) : PsiFileStubImpl<YaccTTFileImpl>(file) {
	override fun getType() = Type
	companion object Type : IStubFileElementType<PsiFileStubImpl<YaccTTFileImpl>>(YaccTTLanguage.INSTANCE) {
		override fun getStubVersion() = 1
		override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?) = YaccTTFileStub(null)
		override fun serialize(stub: PsiFileStubImpl<YaccTTFileImpl>, dataStream: StubOutputStream) = Unit
		override fun getExternalId() = "yacctt.file"
		override fun getBuilder() = object : DefaultStubBuilder() {
			override fun createStubForFile(file: PsiFile) = YaccTTFileStub(file as? YaccTTFileImpl)
		}
	}
}

fun factory(name: String): YaccTTStubType<*, *> = when (name) {
	"MODULE" -> YaccTTModuleStubType
	"DATA" -> YaccTTDataStubType
	"LABEL" -> YaccTTLabelStubType
	"DEF" -> YaccTTDefStubType
	else -> error("Bad stub type: $name")
}

abstract class YaccTTStubType<Stub: StubElement<*>, Psi: PsiElement>(
	debugName: String
) : IStubElementType<Stub, Psi>(debugName, YaccTTLanguage.INSTANCE) {
	// `toString()` returns `debugName`
	override fun getExternalId() = "yacctt.${super.toString()}"
}

interface YaccCompletionElement {
	val lookupElement: LookupElement
}

class YaccTTDefStub(
	parent: StubElement<*>,
	val declName: String
) : StubBase<YaccTTDef>(parent, YaccTTDefStubType), StubElement<YaccTTDef>, YaccCompletionElement {
	override val lookupElement get() = LookupElementBuilder.create(declName).withIcon(SemanticIcons.PINK_LAMBDA)
}

class YaccTTLabelStub(
	parent: StubElement<*>,
	val labelName: String
) : StubBase<YaccTTLabel>(parent, YaccTTLabelStubType), StubElement<YaccTTLabel>, YaccCompletionElement {
	override val lookupElement get() = LookupElementBuilder.create(labelName).withIcon(SemanticIcons.BLUE_C)
}

class YaccTTDataStub(
	parent: StubElement<*>,
	val dataName: String
) : StubBase<YaccTTData>(parent, YaccTTDataStubType), StubElement<YaccTTData>, YaccCompletionElement {
	override val lookupElement get() = LookupElementBuilder.create(dataName).withIcon(SemanticIcons.BLUE_HOLE)
}

class YaccTTModuleStub(
	parent: StubElement<*>,
	val moduleName: String
) : StubBase<YaccTTModule>(parent, YaccTTModuleStubType), StubElement<YaccTTModule>

object YaccTTModuleStubKey : StringStubIndexExtension<YaccTTModule>() {
	private val KEY = StubIndexKey.createIndexKey<String, YaccTTModule>(YaccTTModuleStubType.externalId)
	override fun getKey() = KEY
}

object YaccTTDefStubKey : StringStubIndexExtension<YaccTTDef>() {
	private val KEY = StubIndexKey.createIndexKey<String, YaccTTDef>(YaccTTDefStubType.externalId)
	override fun getKey() = KEY
}

object YaccTTLabelStubKey : StringStubIndexExtension<YaccTTLabel>() {
	private val KEY = StubIndexKey.createIndexKey<String, YaccTTLabel>(YaccTTLabelStubType.externalId)
	override fun getKey() = KEY
}

object YaccTTDataStubKey : StringStubIndexExtension<YaccTTData>() {
	private val KEY = StubIndexKey.createIndexKey<String, YaccTTData>(YaccTTDataStubType.externalId)
	override fun getKey() = KEY
}

object YaccTTModuleStubType : YaccTTStubType<YaccTTModuleStub, YaccTTModule>("module") {
	override fun createPsi(stub: YaccTTModuleStub): YaccTTModule = YaccTTModuleImpl(stub, this)
	override fun createStub(psi: YaccTTModule, parentStub: StubElement<*>) = YaccTTModuleStub(parentStub, psi.nameDecl?.text.orEmpty())
	override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>) = YaccTTModuleStub(parentStub, dataStream.readName()?.string.orEmpty())
	override fun serialize(stub: YaccTTModuleStub, dataStream: StubOutputStream) = dataStream.writeName(stub.moduleName)
	override fun indexStub(stub: YaccTTModuleStub, sink: IndexSink) {
		sink.occurrence(YaccTTModuleStubKey.key, stub.moduleName)
	}
}

object YaccTTDefStubType : YaccTTStubType<YaccTTDefStub, YaccTTDef>("decl") {
	override fun createPsi(stub: YaccTTDefStub) = YaccTTDefImpl(stub, this)
	override fun createStub(psi: YaccTTDef, parentStub: StubElement<*>) = YaccTTDefStub(parentStub, psi.childrenWithLeaves.firstOrNull { it is YaccTTNameDecl }?.text.orEmpty())
	override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>) = YaccTTDefStub(parentStub, dataStream.readName()?.string.orEmpty())
	override fun serialize(stub: YaccTTDefStub, dataStream: StubOutputStream) = dataStream.writeName(stub.declName)
	override fun shouldCreateStub(node: ASTNode?): Boolean {
		val parent = node?.psi?.parent
		if (parent is YaccTTLetExp) return false
		if (parent is YaccTTExpWhere) return false
		return super.shouldCreateStub(node)
	}

	override fun indexStub(stub: YaccTTDefStub, sink: IndexSink) {
		sink.occurrence(YaccTTDefStubKey.key, stub.declName)
	}
}

object YaccTTLabelStubType : YaccTTStubType<YaccTTLabelStub, YaccTTLabel>("label") {
	override fun createPsi(stub: YaccTTLabelStub) = YaccTTLabelImpl(stub, this)
	override fun createStub(psi: YaccTTLabel, parentStub: StubElement<*>) = YaccTTLabelStub(parentStub, psi.childrenWithLeaves.firstOrNull { it is YaccTTNameDecl }?.text.orEmpty())
	override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>) = YaccTTLabelStub(parentStub, dataStream.readName()?.string.orEmpty())
	override fun serialize(stub: YaccTTLabelStub, dataStream: StubOutputStream) = dataStream.writeName(stub.labelName)
	override fun indexStub(stub: YaccTTLabelStub, sink: IndexSink) {
		sink.occurrence(YaccTTLabelStubKey.key, stub.labelName)
	}
}

object YaccTTDataStubType : YaccTTStubType<YaccTTDataStub, YaccTTData>("data") {
	override fun createPsi(stub: YaccTTDataStub) = YaccTTDataImpl(stub, this)
	override fun createStub(psi: YaccTTData, parentStub: StubElement<*>) = YaccTTDataStub(parentStub, psi.nameDecl?.text.orEmpty())
	override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>) = YaccTTDataStub(parentStub, dataStream.readName()?.string.orEmpty())
	override fun serialize(stub: YaccTTDataStub, dataStream: StubOutputStream) = dataStream.writeName(stub.dataName)
	override fun shouldCreateStub(node: ASTNode?): Boolean {
		val parent = node?.psi?.parent
		if (parent is YaccTTLetExp) return false
		if (parent is YaccTTExpWhere) return false
		return super.shouldCreateStub(node)
	}

	override fun indexStub(stub: YaccTTDataStub, sink: IndexSink) {
		sink.occurrence(YaccTTDataStubKey.key, stub.dataName)
	}
}
