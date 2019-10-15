package org.ice1000.tt.gradle

import org.intellij.lang.annotations.Language
import java.io.File

fun LangData.lexHighlight(nickname: String, outDir: File) {
	val outEditingDir = outDir.resolve("editing").resolve(nickname)
	outEditingDir.mkdirs()

	val colorSettingsClassName = "${languageName}GeneratedColorSettingsPage"
	@Language("JAVA")
	val colorSettingsClassContent = """
package $basePackage.editing.$nickname;

import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import icons.TTIcons;
import $basePackage.${languageName}FileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public abstract class $colorSettingsClassName implements ColorSettingsPage {
	public @NotNull @Override ${languageName}GeneratedHighlighter getHighlighter() {
		return ${languageName}Highlighter.INSTANCE;
	}

	public @Nullable @Override Icon getIcon() { return TTIcons.$constantPrefix; }
	public @NotNull @Override ColorDescriptor[] getColorDescriptors() {
		return ColorDescriptor.EMPTY_ARRAY;
	}

	public @NotNull @Override String getDisplayName() {
		return ${languageName}FileType.INSTANCE.getName();
	}
}"""
	outEditingDir.resolve("$colorSettingsClassName.java").writeText(colorSettingsClassContent)

	val tokenPairs = highlightTokenPairs.toList()
	val textAttributes = tokenPairs.joinToString("\n\t") { (l, r) ->
		"public static @NotNull TextAttributesKey $l = TextAttributesKey.createTextAttributesKey(\"${constantPrefix}_$l\", DefaultLanguageHighlighterColors.$r);"
	}
	val textAttributeKeys = tokenPairs.joinToString("\n\t") { (l, _) ->
		"public static @NotNull TextAttributesKey[] ${l}_KEY = new TextAttributesKey[]{$l};"
	}

	val highlighterClassName = "${languageName}GeneratedHighlighter"
	@Language("JAVA")
	val highlighterClassContent = """
package $basePackage.editing.$nickname;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import $basePackage.psi.$nickname.${languageName}ElementType;
import org.jetbrains.annotations.NotNull;

public abstract class $highlighterClassName implements SyntaxHighlighter {
	$textAttributes
	$textAttributeKeys

	public @Override @NotNull Lexer getHighlightingLexer() {
		return ${languageName}ElementType.${nickname}Lexer();
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
