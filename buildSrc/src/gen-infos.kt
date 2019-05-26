package org.ice1000.tt.gradle

import org.intellij.lang.annotations.Language

fun LanguageUtilityGenerationTask.infos(nickname: String) {
	@Language("Java")
	val language = """
package $basePackage;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

import static org.ice1000.tt.ConstantsKt.${constantPrefix}_LANGUAGE_NAME;

/**
 * @author ice1000
 */
public final class ${languageName}Language extends Language {
	public static final @NotNull ${languageName}Language INSTANCE =
			new ${languageName}Language(${constantPrefix}_LANGUAGE_NAME);

	private ${languageName}Language(@NotNull String name) {
		super(name, "text/" + ${constantPrefix}_LANGUAGE_NAME);
	}
}
"""
	outDir.resolve("${languageName}Language.java").writeText(language)
	@Language("kotlin")
	val infos = """
package org.ice1000.tt

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.psi.FileViewProvider
import icons.TTIcons

object ${languageName}FileType : LanguageFileType(${languageName}Language.INSTANCE) {
	override fun getDefaultExtension() = ${constantPrefix}_EXTENSION
	override fun getName() = TTBundle.message("$nickname.name")
	override fun getIcon() = TTIcons.${constantPrefix}_FILE
	override fun getDescription() = TTBundle.message("$nickname.name.description")
}

open class ${languageName}File(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, ${languageName}Language.INSTANCE) {
	override fun getFileType() = ${languageName}FileType
}
"""
	outDir.resolve("$nickname-generated.kt").writeText(infos)
}
