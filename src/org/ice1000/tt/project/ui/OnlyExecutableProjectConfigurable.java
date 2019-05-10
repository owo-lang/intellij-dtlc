package org.ice1000.tt.project.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.labels.LinkLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

@SuppressWarnings("NullableProblems")
public abstract class OnlyExecutableProjectConfigurable implements Configurable {
	protected @NotNull JPanel mainPanel;
	protected @NotNull TextFieldWithBrowseButton exePathField;
	protected @NotNull JButton guessExeButton;
	protected @NotNull LinkLabel<Object> websiteLabel;
}
