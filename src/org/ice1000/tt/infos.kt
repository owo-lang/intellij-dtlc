package org.ice1000.tt

import com.intellij.CommonBundle
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileTypeConsumer
import com.intellij.openapi.fileTypes.FileTypeFactory
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.psi.FileViewProvider
import icons.TTIcons
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.util.*

object MiniTTFileType : LanguageFileType(MiniTTLanguage.INSTANCE) {
	override fun getDefaultExtension() = MINI_TT_EXTENSION
	override fun getName() = TTBundle.message("minitt.name")
	override fun getIcon() = TTIcons.MINI_TT_FILE
	override fun getDescription() = TTBundle.message("minitt.name.description")
}

class MiniTTFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, MiniTTLanguage.INSTANCE) {
	override fun getFileType() = MiniTTFileType
}

object OwOFileType : LanguageFileType(OwOLanguage.INSTANCE) {
	override fun getDefaultExtension() = OWO_EXTENSION
	override fun getName() = TTBundle.message("owo.name")
	override fun getIcon() = TTIcons.OWO_FILE
	override fun getDescription() = TTBundle.message("owo.name.description")
}

class OwOFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, OwOLanguage.INSTANCE) {
	override fun getFileType() = OwOFileType
}

class TTFileTypeFactory : FileTypeFactory() {
	override fun createFileTypes(consumer: FileTypeConsumer) {
		consumer.consume(MiniTTFileType, MINI_TT_EXTENSION)
		consumer.consume(OwOFileType, OWO_EXTENSION)
	}
}

object TTBundle {
	@NonNls private const val BUNDLE = "org.ice1000.tt.owo-bundle"
	private val bundle: ResourceBundle by lazy { ResourceBundle.getBundle(BUNDLE) }

	@JvmStatic
	fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
		CommonBundle.message(bundle, key, *params)
}
