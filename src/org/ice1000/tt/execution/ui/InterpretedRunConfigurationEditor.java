package org.ice1000.tt.execution.ui;

import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.fields.ExpandableTextField;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class InterpretedRunConfigurationEditor<T extends RunConfiguration> extends SettingsEditor<T> {
	protected @NotNull JPanel mainPanel;
	protected @NotNull TextFieldWithBrowseButton workingDirField;
	protected @NotNull TextFieldWithBrowseButton targetFileField;
	protected @NotNull TextFieldWithBrowseButton exePathField;
	protected @NotNull ExpandableTextField additionalArgumentsField;
}
