package org.ice1000.tt;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

import static org.ice1000.tt.ConstantsKt.AGDA_LANGUAGE_NAME;

/**
 * @author ice1000
 */
public class AgdaLanguage extends Language {
	public static final @NotNull AgdaLanguage INSTANCE = new AgdaLanguage();

	private AgdaLanguage() {
		super(AGDA_LANGUAGE_NAME, "text/" + AGDA_LANGUAGE_NAME);
	}
}
