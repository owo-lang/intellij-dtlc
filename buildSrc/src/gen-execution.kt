package org.ice1000.tt.gradle

import org.ice1000.tt.gradle.json.LangData
import org.intellij.lang.annotations.Language
import java.io.File

fun LangData.execution(nickname: String, configName: String, outDir: File) {
	@Language("java")
	val runConfigFactoryJava = """
	package $basePackage.execution;

	import com.intellij.execution.configurations.ConfigurationFactory;
	import com.intellij.openapi.project.Project;
	import org.jetbrains.annotations.NotNull;
	
	public class ${languageName}RunConfigurationFactory extends ConfigurationFactory {
		public ${languageName}RunConfigurationFactory(${languageName}RunConfigurationType type) {
			super(type);
		}
		
		@NotNull
		@Override
		public ${languageName}RunConfiguration createTemplateConfiguration(@NotNull Project project) {
			return new ${languageName}RunConfiguration(project, this);
		}
	}
	""".trimIndent()

	@Language("java")
	val runConfigTypeJava = """
	package $basePackage.execution;

	import com.intellij.execution.configurations.ConfigurationFactory;
	import com.intellij.execution.configurations.ConfigurationType;
	import icons.TTIcons;
	import ${basePackage}.TTBundle;
	import org.jetbrains.annotations.Nls;
	import org.jetbrains.annotations.NotNull;

	import javax.swing.*;

	public class ${languageName}RunConfigurationType implements ConfigurationType {

		ConfigurationFactory factory;
		private ConfigurationFactory[] factories;
		private static ${languageName}RunConfigurationType instance;
		
		synchronized public static ${languageName}RunConfigurationType getInstance() {
			if (instance == null) {
				instance = new ${languageName}RunConfigurationType();
			}
			return instance;
		}

		private ${languageName}RunConfigurationType() {
			factory = new ${languageName}RunConfigurationFactory(this);
			factories = new ConfigurationFactory[]{factory};
		}

		@NotNull
		@Override
		public String getDisplayName() {
			return TTBundle.message("$nickname.name");
		}

		@Nls
		@Override
		public String getConfigurationTypeDescription() {
			return TTBundle.message("$nickname.run-config.description");
		}

		@Override
		public Icon getIcon() {
			return TTIcons.$constantPrefix;
		}

		@NotNull
		@Override
		public String getId() {
			return "${constantPrefix}_RUN_CONFIG_ID";
		}

		@Override
		public ConfigurationFactory[] getConfigurationFactories() {
			return factories;
		}
	}
		
	""".trimIndent()

	@Language("java")
	val runConfigJava = """
	package $basePackage.execution;
	
	import com.intellij.execution.ExecutionException;
	import com.intellij.execution.Executor;
	import com.intellij.execution.configurations.ConfigurationFactory;
	import com.intellij.execution.configurations.RunConfiguration;
	import com.intellij.execution.configurations.RunProfileState;
	import com.intellij.execution.runners.ExecutionEnvironment;
	import com.intellij.openapi.options.SettingsEditor;
	import com.intellij.openapi.project.Project;
	import com.intellij.openapi.util.JDOMExternalizerUtil;
	import ${basePackage}.TTBundle;
	import ${basePackage}.execution.${languageName}CommandLineState;
	import ${basePackage}.execution.${languageName}RunConfigurationEditor;
	import ${basePackage}.execution.InterpretedRunConfiguration;
	import org.jdom.Element;
	import org.jetbrains.annotations.NotNull;
	import org.jetbrains.annotations.Nullable;

	public class ${languageName}RunConfiguration extends InterpretedRunConfiguration<${languageName}CommandLineState> {
		public String ${configName}Executable;
		private Project project;
		private ConfigurationFactory factory;
		public ${languageName}RunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory) {
			super(project, factory, TTBundle.message("$nickname.name"));
			this.project = project;
			this.factory = factory;
			${runConfigInit};
		}

		@NotNull
		@Override
		public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
			return new ${languageName}RunConfigurationEditor(this, project);
		}

		@Nullable
		@Override
		public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
			return new ${languageName}CommandLineState(this, environment);
		}

		@Override
		public void readExternal(@NotNull Element element) {
			super.readExternal(element);
			String field = JDOMExternalizerUtil.readField(element, "${configName}Executable");
			if (field == null) {
				field = "";
			}
			${configName}Executable = field;
		}

		@Override
		public void writeExternal(@NotNull Element element) {
			super.writeExternal(element);
			JDOMExternalizerUtil.writeField(element, "${configName}Executable", ${configName}Executable);
		}
	}
	""".trimIndent()

	@Language("java")
	val runConfigEditorJava = """
	package $basePackage.execution;
	
	import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
	import com.intellij.openapi.project.Project;
	import ${basePackage}.TTBundle;
	import ${basePackage}.execution.${languageName}RunConfiguration;
	import ${basePackage}.execution.ui.InterpretedRunConfigurationEditorImpl;
	import $basePackage.${languageName}FileType;
	import org.jetbrains.annotations.NotNull;

	import javax.naming.ConfigurationException;
	import java.lang.reflect.Executable;

	public class ${languageName}RunConfigurationEditor extends InterpretedRunConfigurationEditorImpl<${languageName}RunConfiguration> {

		private ${languageName}RunConfiguration configuration;
		private Project project;

		public ${languageName}RunConfigurationEditor(${languageName}RunConfiguration configuration, Project project) {
			super(project);
			this.configuration = configuration;
			this.project = project;
			targetFileField.addBrowseFolderListener(TTBundle.message("$nickname.ui.run-config.select-$nickname-file"),
				TTBundle.message("$nickname.ui.run-config.select-$nickname-file.description"),
				project,
				FileChooserDescriptorFactory.createSingleFileDescriptor(${languageName}FileType.INSTANCE));
			exePathField.addBrowseFolderListener(TTBundle.message("$nickname.ui.project.select-compiler"),
				TTBundle.message("$nickname.ui.project.select-compiler.description"),
				project,
				FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor());
			resetEditorFrom(configuration);
		}

		@Override
		protected void resetEditorFrom(@NotNull ${languageName}RunConfiguration s) {
			super.resetEditorFrom(s);
			exePathField.setText(s.${configName}Executable);
		}

		@Override
		protected void applyEditorTo(@NotNull ${languageName}RunConfiguration s) {
			try {
				super.applyEditorTo(s);
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}
			s.${configName}Executable = exePathField.getText();

		}
	}
	""".trimIndent()

	@Language("kotlin")
	val runConfig = """
	package $basePackage.execution

	import com.intellij.execution.Executor
	import com.intellij.execution.actions.ConfigurationContext
	import com.intellij.execution.actions.LazyRunConfigurationProducer
	import com.intellij.execution.actions.RunConfigurationProducer
	import com.intellij.execution.configurations.ConfigurationFactory
	import com.intellij.execution.configurations.ConfigurationType
	import com.intellij.execution.runners.ExecutionEnvironment
	import com.intellij.openapi.actionSystem.CommonDataKeys
	import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
	import com.intellij.openapi.project.Project
	import com.intellij.openapi.util.JDOMExternalizerUtil
	import com.intellij.openapi.util.Ref
	import com.intellij.openapi.util.io.FileUtilRt
	import com.intellij.psi.PsiElement
	import icons.TTIcons
	import $basePackage.${languageName}FileType
	import $basePackage.TTBundle
	import $basePackage.execution.ui.InterpretedRunConfigurationEditorImpl
	import $basePackage.project.*
	import $basePackage.validateExe
	import org.jdom.Element

	class ${languageName}RunConfigurationProducer : LazyRunConfigurationProducer<${languageName}RunConfiguration>() {
		override fun getConfigurationFactory() = ${languageName}RunConfigurationType.getInstance().factory
		override fun isConfigurationFromContext(
			configuration: ${languageName}RunConfiguration, context: ConfigurationContext) =
			configuration.targetFile == context.dataContext.getData(CommonDataKeys.VIRTUAL_FILE)?.path

		override fun setupConfigurationFromContext(
			configuration: ${languageName}RunConfiguration, context: ConfigurationContext, ref: Ref<PsiElement>): Boolean {
			val file = context.dataContext.getData(CommonDataKeys.VIRTUAL_FILE)
			if (file?.fileType != ${languageName}FileType) return false
			val config = context.project.${configName}SettingsNullable ?: return false
			configuration.targetFile = file.path
			configuration.workingDir = context.project.basePath.orEmpty()
			configuration.name = FileUtilRt.getNameWithoutExtension(configuration.targetFile)
				.takeLastWhile { it != '/' && it != '\\' }
			val existPath = config.settings.exePath
			if (validateExe(existPath)) configuration.${configName}Executable = existPath
			else {
				val exePath = ${configName}Path ?: return true
				if (validateExe(exePath)) configuration.${configName}Executable = exePath
			}
			return true
		}
	}
	"""
	@Language("kotlin")
	val cliState = """
	package $basePackage.execution

	import com.intellij.execution.runners.ExecutionEnvironment

	class ${languageName}CommandLineState(
		configuration: ${languageName}RunConfiguration,
		env: ExecutionEnvironment
	) : InterpretedCliState<${languageName}RunConfiguration>(configuration, env) {
		override fun ${languageName}RunConfiguration.pre(params: MutableList<String>) {
			params += ${configName}Executable
		}
	}
	"""
	val exe = outDir.resolve("execution").apply { mkdirs() }
	exe.resolve("$nickname-generated.kt").writeText(runConfig)
	exe.resolve("${languageName}RunConfigurationFactory.java").writeText((runConfigFactoryJava))
	exe.resolve("${languageName}RunConfigurationType.java").writeText((runConfigTypeJava))
	exe.resolve("${languageName}RunConfiguration.java").writeText((runConfigJava))
	exe.resolve("${languageName}RunConfigurationEditor.java").writeText((runConfigEditorJava))
	if (generateCliState) exe.resolve("$nickname-cli-state.kt").writeText(cliState)
}
