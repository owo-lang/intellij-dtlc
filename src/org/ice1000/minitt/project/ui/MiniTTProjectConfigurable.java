package org.ice1000.minitt.project.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

@SuppressWarnings("NullableProblems")
public abstract class MiniTTProjectConfigurable implements Configurable {
	protected @NotNull JPanel mainPanel;
	protected @NotNull TextFieldWithBrowseButton exePathField;
	protected @NotNull JLabel versionLabel;
	protected @NotNull JButton guessExeButton;
}
