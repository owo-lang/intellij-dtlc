package icons;

import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public interface TTIcons {
	@NotNull Icon MINI_TT_FILE = IconLoader.getIcon("/icons/minitt/minitt_file.png");
	@NotNull Icon MINI_TT = IconLoader.getIcon("/icons/minitt/minitt.png");
	@NotNull Icon OWO_FILE = IconLoader.getIcon("/icons/owo_file.png");
	@NotNull Icon AGDA_CORE_FILE = IconLoader.getIcon("/icons/mtt/mtt_file.png");
	@NotNull Icon AGDA_CORE = AGDA_CORE_FILE;
	@NotNull Icon MLPOLYR_FILE = IconLoader.getIcon("/icons/mlpr/mlpr_file.png");
	@NotNull Icon MLPOLYR = MLPOLYR_FILE;
	@NotNull Icon CUBICAL_TT_FILE = IconLoader.getIcon("/icons/ctt/ctt_file.png");
	@NotNull Icon CUBICAL_TT = CUBICAL_TT_FILE;
	@NotNull Icon YACC_TT_FILE = IconLoader.getIcon("/icons/ytt/ytt_file.png");
	@NotNull Icon YACC_TT = YACC_TT_FILE;
	@NotNull Icon AGDA_FILE = IconLoader.getIcon("/icons/agda/agda_file.png");
	@NotNull Icon AGDA = IconLoader.getIcon("/icons/agda/agda.png");
	@NotNull Icon RED_PRL_FILE = IconLoader.getIcon("/icons/redprl/redprl_file.png");
	@NotNull Icon RED_PRL = IconLoader.getIcon("/icons/redprl/redprl.png");
	@NotNull Icon VOILE_FILE = IconLoader.getIcon("/icons/voile/voile_file.png");
	@NotNull Icon VOILE = IconLoader.getIcon("/icons/voile/voile.png");
}
