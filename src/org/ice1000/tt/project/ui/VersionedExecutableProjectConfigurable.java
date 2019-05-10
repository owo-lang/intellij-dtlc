package org.ice1000.tt.project.ui;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.labels.LinkLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class VersionedExecutableProjectConfigurable implements CommonConfigurable {
	private JPanel mainPanel;
	private TextFieldWithBrowseButton exePathField;
	private JButton guessExeButton;
	private LinkLabel<Object> websiteLabel;

	@SuppressWarnings("NullableProblems")
	@NotNull
	protected JLabel versionLabel;

	@NotNull
	@Override
	public final JButton getGuessExeButton() {
		return guessExeButton;
	}

	@Override
	@NotNull
	public final JPanel getMainPanel() {
		return mainPanel;
	}

	@NotNull
	@Override
	public final TextFieldWithBrowseButton getExePathField() {
		return exePathField;
	}

	@Override
	@NotNull
	public final LinkLabel<Object> getWebsiteLabel() {
		return websiteLabel;
	}
}
