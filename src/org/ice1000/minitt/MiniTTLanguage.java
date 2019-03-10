package org.ice1000.minitt;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

import static org.ice1000.minitt.Minitt_constantsKt.MINI_TT_LANGUAGE_NAME;

/**
 * @author ice1000
 */
public class MiniTTLanguage extends Language {
	public static final @NotNull
	MiniTTLanguage INSTANCE = new MiniTTLanguage();

	private MiniTTLanguage() {
		super(MINI_TT_LANGUAGE_NAME, "text/" + MINI_TT_LANGUAGE_NAME);
	}
}
