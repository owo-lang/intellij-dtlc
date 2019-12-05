package org.ice1000.tt.gradle

import org.ice1000.tt.gradle.json.LangData
import org.intellij.lang.annotations.Language
import java.io.File

fun dir(outDir: File, relative: String, nickname: String): File {
	val outPsiDir = outDir.resolve(relative).resolve(nickname)
	outPsiDir.mkdirs()
	return outPsiDir
}

fun LangData.infos(nickname: String, outDir: File) {
	@Language("Java")
	val language = """
package $basePackage;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

import static $basePackage.ConstantsKt.${constantPrefix}_LANGUAGE_NAME;

/**
 * @author ice1000
 */
public final class ${languageName}Language extends Language {
	public static final @NotNull ${languageName}Language INSTANCE =
			new ${languageName}Language(${constantPrefix}_LANGUAGE_NAME);

	private ${languageName}Language(@NotNull String name) {
		super(name, "text/" + name);
	}
}
"""
	outDir.resolve("${languageName}Language.java").writeText(language)
	@Language("kotlin")
	val infos = """
package $basePackage

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import icons.TTIcons
import $basePackage.psi.childrenRevWithLeaves

object ${languageName}FileType : LanguageFileType(${languageName}Language.INSTANCE) {
	override fun getDefaultExtension() = ${constantPrefix}_EXTENSION
	override fun getName() = ${constantPrefix}_LANGUAGE_NAME
	override fun getIcon() = TTIcons.${constantPrefix}_FILE
	override fun getDescription() = TTBundle.message("tt.name.description", ${constantPrefix}_LANGUAGE_NAME)
}

open class ${languageName}File(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, ${languageName}Language.INSTANCE), TTFile {
	override fun getFileType() = ${languageName}FileType
	override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement) =
		childrenRevWithLeaves.all { it.processDeclarations(processor, state, lastParent, place) }
}
"""
	outDir.resolve("$nickname-generated.kt").writeText(infos)
}
