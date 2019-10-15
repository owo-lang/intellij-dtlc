package org.ice1000.tt.gradle

import org.intellij.lang.annotations.Language
import java.io.File

fun LangData.pluginXml(nickname: String, pluginXmlDir: File) {
	pluginXmlDir.mkdirs()
	val pluginXml = pluginXmlDir.resolve("plugin-$nickname-generated.xml")
	@Language("XML")
	val pluginXmlContent = """
<idea-plugin>
	<extensions defaultExtensionNs='com.intellij'>
		<liveTemplateContext implementation="$basePackage.editing.${languageName}DefaultContext"/>
		<configurationType implementation="$basePackage.execution.${languageName}RunConfigurationType"/>
		<runConfigurationProducer implementation="$basePackage.execution.${languageName}RunConfigurationProducer"/>
		<lang.refactoringSupport language="$languageName" implementationClass="$basePackage.editing.InplaceRenameRefactoringSupportProvider"/>

		<projectConfigurable
			groupId="language"
			id="TT.$languageName.Configurable"
			displayName="$languageName"
			instance="$basePackage.project.${languageName}ProjectConfigurable"/>
	</extensions>

	<project-components>
		<component>
			<implementation-class>$basePackage.project.${languageName}ProjectSettingsService</implementation-class>
		</component>
	</project-components>
</idea-plugin>
"""
	pluginXml.apply { if (!exists()) createNewFile() }.writeText(pluginXmlContent)
	val pluginCss = pluginXmlDir.resolve("$languageName-template.css")

	pluginCss.apply { if (!exists()) createNewFile() }.writeText(buildString {
		appendln("/* Token types. */")
		highlightTokenPairs.forEach { (name, highlight) ->
			appendln(".$languageName .${constantPrefix}_$name {")
			when (highlight) {
				"KEYWORD" -> appendln("color: #000080;").appendln("font-weight: bold;")
				"LINE_COMMENT", "BLOCK_COMMENT" -> appendln("color: #808080;").appendln("font-style: italic;")
				"IDENTIFIER" -> appendln("color: #000000;")
				"STRING" -> appendln("color: #008000;")
				"NUMBER" -> appendln("color: #0000FF;")
				"METADATA" -> appendln("color: #808000;")
			}
			appendln("}")
		}
		appendln()
		appendln("/* Standard attributes. */")
		appendln(".$languageName a { text-decoration: none; }")
		appendln(".$languageName :target { border: 2px solid #ECFAEB; background-color: #ECFAEB; }")
		appendln(".$languageName a[href]:hover { color: #0000FF; text-decoration: underline; text-decoration-color: #0000FF; }")
	})
}
