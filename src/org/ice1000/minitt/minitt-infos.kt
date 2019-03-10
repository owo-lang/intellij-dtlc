package org.ice1000.minitt

import com.intellij.CommonBundle
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileTypeConsumer
import com.intellij.openapi.fileTypes.FileTypeFactory
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.psi.FileViewProvider
import icons.MiniTTIcons
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.util.*

object MiniTTFileType : LanguageFileType(MiniTTLanguage.INSTANCE) {
	override fun getDefaultExtension() = MINI_TT_EXTENSION
	override fun getName() = MiniTTBundle.message("minitt.name")
	override fun getIcon() = MiniTTIcons.MINI_TT_FILE
	override fun getDescription() = MiniTTBundle.message("minitt.name.description")
}

class MiniTTFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, MiniTTLanguage.INSTANCE) {
	override fun getFileType() = MiniTTFileType
}

class MiniTTFileTypeFactory : FileTypeFactory() {
	override fun createFileTypes(consumer: FileTypeConsumer) {
		consumer.consume(MiniTTFileType, MINI_TT_EXTENSION)
	}
}

object MiniTTBundle {
	@NonNls private const val BUNDLE = "org.ice1000.minitt.minitt-bundle"
	private val bundle: ResourceBundle by lazy { ResourceBundle.getBundle(BUNDLE) }

	@JvmStatic
	fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
		CommonBundle.message(bundle, key, *params)
}
