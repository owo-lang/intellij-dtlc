package org.ice1000.tt.gradle

import org.intellij.lang.annotations.Language

fun LanguageUtilityGenerationTask.pluginXml(nickname: String) {
	pluginXmlDir.mkdirs()
	val pluginXml = pluginXmlDir.resolve("plugin-$nickname-generated.xml")
	@Language("XML")
	val pluginXmlContent = """
	<idea-plugin>
		<extensions defaultExtensionNs='com.intellij'>
			<liveTemplateContext implementation="org.ice1000.tt.editing.${languageName}DefaultContext"/>
			<configurationType implementation="org.ice1000.tt.execution.${languageName}RunConfigurationType"/>
			<runConfigurationProducer implementation="org.ice1000.tt.execution.${languageName}RunConfigurationProducer"/>
			<lang.refactoringSupport language="$languageName" implementationClass="org.ice1000.tt.editing.InplaceRenameRefactoringSupportProvider"/>

			<projectConfigurable
				groupId="language"
				id="TT.$languageName.Configurable"
				displayName="$languageName"
				instance="org.ice1000.tt.project.${languageName}ProjectConfigurable"/>
		</extensions>

		<project-components>
			<component>
				<implementation-class>org.ice1000.tt.project.${languageName}ProjectSettingsService</implementation-class>
			</component>
		</project-components>
	</idea-plugin>
	"""
	pluginXml.apply { if (!exists()) createNewFile() }.writeText(pluginXmlContent)
}
