package org.ice1000.tt.gradle

import org.intellij.lang.annotations.Language

fun LanguageUtilityGenerationTask.lexHighlight(configName: String, nickname: String) {
	val textAttributes = highlightTokenPairs.joinToString("\n\t") { (l, r) ->
		"@JvmField val $l = TextAttributesKey.createTextAttributesKey(\"${constantPrefix}_$l\", DefaultLanguageHighlighterColors.$r)"
	}
	val textAttributeKeys = highlightTokenPairs.joinToString("\n\t") { (l, r) ->
		"@JvmField val ${l}_KEY = arrayOf($l)"
	}
	@Language("kotlin")
	val parser = """
package $basePackage.editing.$nickname

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import icons.TTIcons
import org.ice1000.tt.${languageName}FileType
import org.ice1000.tt.psi.$nickname.${configName}Lexer

abstract class ${languageName}GeneratedSyntaxHighlighter : SyntaxHighlighter {
	$textAttributes

	$textAttributeKeys

	override fun getHighlightingLexer() = ${configName}Lexer()
}

class ${languageName}HighlighterFactory : SyntaxHighlighterFactory() {
	override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?) = ${languageName}Highlighter
}

abstract class ${languageName}GeneratedColorSettingsPage : ColorSettingsPage {
	override fun getHighlighter(): SyntaxHighlighter = ${languageName}Highlighter
	override fun getIcon() = TTIcons.$constantPrefix
	override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
	override fun getDisplayName() = ${languageName}FileType.name
}
"""
	outDir.resolve("editing")
		.resolve(nickname)
		.apply { mkdirs() }
		.resolve("generated.kt")
		.apply { if (!exists()) createNewFile() }
		.apply { writeText(parser) }
}
