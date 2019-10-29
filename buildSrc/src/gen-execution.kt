package org.ice1000.tt.gradle

import org.ice1000.tt.gradle.json.LangData
import org.intellij.lang.annotations.Language
import java.io.File

fun LangData.execution(nickname: String, configName: String, outDir: File) {
	@Language("JAVA")
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

	@Language("JAVA")
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
			return $basePackage.ConstantsKt.${constantPrefix}_LANGUAGE_NAME;
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

	@Language("JAVA")
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
		public ${languageName}RunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory) {
			super(project, factory, $basePackage.ConstantsKt.${constantPrefix}_LANGUAGE_NAME);
			${runConfigInit};
		}

		@NotNull
		@Override
		public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
			return new ${languageName}RunConfigurationEditor(this, getProject());
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

	@Language("JAVA")
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

	@Language("JAVA")
	val runConfigProducerJava = """
	package $basePackage.execution;
	
	import com.google.common.base.Strings;
	import com.intellij.execution.actions.ConfigurationContext;
	import com.intellij.execution.actions.LazyRunConfigurationProducer;
	import com.intellij.execution.configurations.ConfigurationFactory;
	import com.intellij.openapi.actionSystem.CommonDataKeys;
	import com.intellij.openapi.util.Ref;
	import com.intellij.openapi.util.io.FileUtilRt;
	import com.intellij.openapi.vfs.VirtualFile;
	import com.intellij.psi.PsiElement;
	import kotlin.text.StringsKt;
	import ${basePackage}.${languageName}FileType;
	import ${basePackage}.UtilsKt;
	import ${basePackage}.execution.${languageName}RunConfiguration;
	import ${basePackage}.project.${languageName}ProjectSettingsService;
	import ${basePackage}.project.${languageName}Settings;
	import ${basePackage}.project.ProjectGenerated;
	import ${basePackage}.execution.${languageName}RunConfigurationType;
	import org.jetbrains.annotations.NotNull;

	import java.util.Objects;

	public class ${languageName}RunConfigurationProducer extends LazyRunConfigurationProducer<${languageName}RunConfiguration> {
		@NotNull
		@Override
		public ConfigurationFactory getConfigurationFactory() {
			return ${languageName}RunConfigurationType.getInstance().factory;
		}

		@Override
		public boolean isConfigurationFromContext(@NotNull ${languageName}RunConfiguration configuration, @NotNull ConfigurationContext context) {
			String path = null;
			if (context.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE) != null) {
				path = Objects.requireNonNull(context.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE)).getPath();
			}
			return Objects.equals(configuration.getTargetFile(), path);
		}

		@Override
		protected boolean setupConfigurationFromContext(@NotNull ${languageName}RunConfiguration configuration,
																										@NotNull ConfigurationContext context,
																										@NotNull Ref<PsiElement> sourceElement) {
			VirtualFile file = context.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);
			if (file == null) return false;
			if (!file.getFileType().equals(${languageName}FileType.INSTANCE)) return false;
			if (ProjectGenerated.get${configName.capitalize()}SettingsNullable(context.getProject()) == null) {
				return false;
			}
			${languageName}ProjectSettingsService config = ProjectGenerated.get${configName.capitalize()}SettingsNullable(context.getProject());
			configuration.setTargetFile(file.getPath());
			String basePath = context.getProject().getBasePath();
			if (basePath == null) basePath = "";
			configuration.setWorkingDir(basePath);
			configuration.setName(StringsKt.takeLastWhile(
				FileUtilRt.getNameWithoutExtension(configuration.getTargetFile()),
				it -> it != '/' && it != '\\'));
			String existPath = config.getSettings().getExePath();
			if (UtilsKt.validateExe(existPath)) {
				configuration.${configName}Executable = existPath;
			} else {
				String exePath = ProjectGenerated.get${configName.capitalize()}Path();
				if (exePath == null) return true;
				if (UtilsKt.validateExe(exePath)) {
					configuration.${configName}Executable = exePath;
				}
			}
			return true;
		}
	}
	""".trimIndent()

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
	exe.resolve("${languageName}RunConfigurationFactory.java").writeText(runConfigFactoryJava)
	exe.resolve("${languageName}RunConfigurationType.java").writeText(runConfigTypeJava)
	exe.resolve("${languageName}RunConfiguration.java").writeText(runConfigJava)
	exe.resolve("${languageName}RunConfigurationEditor.java").writeText(runConfigEditorJava)
	exe.resolve("${languageName}RunConfigurationProducer.java").writeText(runConfigProducerJava)
	if (generateCliState) exe.resolve("$nickname-cli-state.kt").writeText(cliState)
}
