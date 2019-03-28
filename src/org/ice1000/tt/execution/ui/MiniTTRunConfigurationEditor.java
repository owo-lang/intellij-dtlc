package org.ice1000.tt.execution.ui;

import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.fields.ExpandableTextField;
import org.ice1000.tt.execution.MiniTTRunConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

@SuppressWarnings("NullableProblems")
public abstract class MiniTTRunConfigurationEditor extends SettingsEditor<MiniTTRunConfiguration> {
	protected @NotNull JPanel mainPanel;
	protected @NotNull TextFieldWithBrowseButton workingDirField;
	protected @NotNull TextFieldWithBrowseButton targetFileField;
	protected @NotNull TextFieldWithBrowseButton exePathField;
	protected @NotNull ExpandableTextField additionalArgumentsField;
}
