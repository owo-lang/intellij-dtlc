package org.ice1000.tt;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

import static org.ice1000.tt.ConstantsKt.MINI_TT_LANGUAGE_NAME;

/**
 * @author ice1000
 */
public class MiniTTLanguage extends Language {
	public static final @NotNull MiniTTLanguage INSTANCE = new MiniTTLanguage(MINI_TT_LANGUAGE_NAME);

	private MiniTTLanguage(String name) {
		super(name, "text/" + MINI_TT_LANGUAGE_NAME);
	}
}
