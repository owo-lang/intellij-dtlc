package org.ice1000.tt;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

import static org.ice1000.tt.ConstantsKt.AGDA_LANGUAGE_NAME;

/**
 * @author ice1000
 */
public class AgdaLanguage extends Language {
	public static final @NotNull AgdaLanguage INSTANCE = new AgdaLanguage(AGDA_LANGUAGE_NAME);

	private AgdaLanguage(String name) {
		super(name, "text/" + AGDA_LANGUAGE_NAME);
	}
}
