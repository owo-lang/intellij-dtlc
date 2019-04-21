package org.ice1000.tt;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

import static org.ice1000.tt.ConstantsKt.VOILE_LANGUAGE_NAME;

/**
 * @author ice1000
 */
public class VoileLanguage extends Language {
	public static final @NotNull VoileLanguage INSTANCE = new VoileLanguage();

	private VoileLanguage() {
		super(VOILE_LANGUAGE_NAME, "text/" + VOILE_LANGUAGE_NAME);
	}
}
