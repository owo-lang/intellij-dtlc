package org.ice1000.tt;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

import static org.ice1000.tt.ConstantsKt.OWO_LANGUAGE_NAME;

/**
 * @author ice1000
 */
public class OwOLanguage extends Language {
	public static final @NotNull OwOLanguage INSTANCE = new OwOLanguage();

	private OwOLanguage() {
		super(OWO_LANGUAGE_NAME, "text/" + OWO_LANGUAGE_NAME);
	}
}
