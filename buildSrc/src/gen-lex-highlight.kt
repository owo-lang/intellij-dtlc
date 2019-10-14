package org.ice1000.tt.gradle

import org.intellij.lang.annotations.Language

fun LanguageUtilityGenerationTask.lexHighlight(configName: String, nickname: String) {
	val outEditingDir = outDir.resolve("editing").resolve(nickname)
	outEditingDir.mkdirs()

	val textAttributes = highlightTokenPairs.joinToString("\n\t") { (l, r) ->
		"public static @NotNull TextAttributesKey $l = TextAttributesKey.createTextAttributesKey(\"${constantPrefix}_$l\", DefaultLanguageHighlighterColors.$r);"
	}
	val textAttributeKeys = highlightTokenPairs.joinToString("\n\t") { (l, _) ->
		"public static @NotNull TextAttributesKey[] ${l}_KEY = new TextAttributesKey[]{$l};"
	}
	@Language("kotlin")
	val parser = """
package $basePackage.editing.$nickname;

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import icons.TTIcons
import org.ice1000.tt.${languageName}FileType
import org.ice1000.tt.psi.$nickname.${languageName}ElementType.${configName}Lexer

abstract class ${languageName}GeneratedColorSettingsPage : ColorSettingsPage {
	override fun getHighlighter() = ${languageName}Highlighter
	override fun getIcon() = TTIcons.$constantPrefix
	override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
	override fun getDisplayName() = ${languageName}FileType.name
}
"""
	outEditingDir.resolve("generated.kt").writeText(parser)

	val highlighterClassName = "${languageName}GeneratedHighlighter"
	@Language("JAVA")
	val highlighterClassContent = """
package $basePackage.editing.$nickname;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import org.ice1000.tt.psi.$nickname.${languageName}ElementType;
import org.jetbrains.annotations.NotNull;

public abstract class $highlighterClassName implements SyntaxHighlighter {
	$textAttributes
	$textAttributeKeys

	public @Override @NotNull Lexer getHighlightingLexer() {
		return ${languageName}ElementType.${configName}Lexer();
	}
}
"""
	outEditingDir.resolve("$highlighterClassName.java").writeText(highlighterClassContent)

	val highlightFactoryClassName = "${languageName}HighlighterFactory"
	@Language("JAVA")
	val highlightFactoryClassContent = """
package $basePackage.editing.$nickname;

import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public final class $highlightFactoryClassName extends SyntaxHighlighterFactory {
	public @Override SyntaxHighlighter getSyntaxHighlighter(Project project, VirtualFile virtualFile) {
		return ${languageName}Highlighter.INSTANCE;
	}
}
"""
	outEditingDir.resolve("$highlightFactoryClassName.java").writeText(highlightFactoryClassContent)
}
