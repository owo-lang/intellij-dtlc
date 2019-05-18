package org.ice1000.tt.psi.cubicaltt

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.stubs.*
import com.intellij.psi.tree.IStubFileElementType
import org.ice1000.tt.CubicalTTLanguage
import org.ice1000.tt.psi.cubicaltt.impl.CubicalTTModuleImpl

class CubicalTTFileStub(file: CubicalTTFileImpl?) : PsiFileStubImpl<CubicalTTFileImpl>(file) {
	override fun getType() = Type
	companion object Type : IStubFileElementType<PsiFileStubImpl<CubicalTTFileImpl>>(CubicalTTLanguage.INSTANCE) {
		override fun getStubVersion() = 0
		override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?) = CubicalTTFileStub(null)
		override fun serialize(stub: PsiFileStubImpl<CubicalTTFileImpl>, dataStream: StubOutputStream) = Unit
		override fun getExternalId() = "cubicaltt.file"
		override fun getBuilder() = object : DefaultStubBuilder() {
			override fun createStubForFile(file: PsiFile) = CubicalTTFileStub(file as? CubicalTTFileImpl)
		}
	}
}

class CubicalTTDeclStub(
	parent: StubElement<*>,
	val declName: String
) : StubBase<CubicalTTDecl>(parent, TODO()), StubElement<CubicalTTDecl>

class CubicalTTModuleStub(
	parent: StubElement<*>,
	val moduleName: String
) : StubBase<CubicalTTModule>(parent, CubicalTTModuleStubType), StubElement<CubicalTTModule>

object CubicalTTModuleStubKey : StringStubIndexExtension<CubicalTTModule>() {
	private val KEY = StubIndexKey.createIndexKey<String, CubicalTTModule>(CubicalTTModuleStubType.externalId)
	override fun getKey() = KEY
	override fun getAllKeys(project: Project) = StubIndex.getInstance().getAllKeys(key, project)
}

object CubicalTTModuleStubType : IStubElementType<CubicalTTModuleStub, CubicalTTModule>("module", CubicalTTLanguage.INSTANCE) {
	override fun createPsi(stub: CubicalTTModuleStub): CubicalTTModule = CubicalTTModuleImpl(stub, this)
	override fun createStub(psi: CubicalTTModule, parentStub: StubElement<*>) = CubicalTTModuleStub(parentStub, psi.nameDecl?.text.orEmpty())
	override fun getExternalId() = "cubicaltt.module"
	override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>) = CubicalTTModuleStub(parentStub, dataStream.readNameString().orEmpty())
	override fun serialize(stub: CubicalTTModuleStub, dataStream: StubOutputStream) = dataStream.writeName(stub.moduleName)
	override fun indexStub(stub: CubicalTTModuleStub, sink: IndexSink) {
		sink.occurrence(CubicalTTModuleStubKey.key, stub.moduleName)
	}
}
