package icons;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public interface TTIcons {
	@NotNull Icon MINI_TT_FILE = IconLoader.getIcon("/icons/minitt/minitt_file.svg");
	@NotNull Icon MINI_TT = IconLoader.getIcon("/icons/minitt/minitt.svg");
	@NotNull Icon OWO_FILE = IconLoader.getIcon("/icons/fileIcon/owo_file.svg");
	@NotNull Icon AGDA_CORE_FILE = IconLoader.getIcon("/icons/fileIcon/mtt_file.svg");
	@NotNull Icon AGDA_CORE = AGDA_CORE_FILE;
	// TODO: this is a placeholder
	@NotNull Icon M_LANG_FILE = AGDA_CORE_FILE;
	@NotNull Icon M_LANG = AGDA_CORE_FILE;
	@NotNull Icon MLPOLYR_FILE = IconLoader.getIcon("/icons/fileIcon/mlpr_file.svg");
	@NotNull Icon MLPOLYR = MLPOLYR_FILE;
	@NotNull Icon CUBICAL_TT_FILE = IconLoader.getIcon("/icons/fileIcon/ctt_file.svg");
	@NotNull Icon CUBICAL_TT = CUBICAL_TT_FILE;
	@NotNull Icon YACC_TT_FILE = IconLoader.getIcon("/icons/fileIcon/ytt_file.svg");
	@NotNull Icon YACC_TT = YACC_TT_FILE;
	@NotNull Icon AGDA_FILE = IconLoader.getIcon("/icons/agda/agda_file.svg");
	@NotNull Icon AGDA = IconLoader.getIcon("/icons/agda/agda.svg");
	@NotNull Icon RED_PRL_FILE = IconLoader.getIcon("/icons/redprl/redprl_file.svg");
	@NotNull Icon RED_PRL = IconLoader.getIcon("/icons/redprl/redprl.svg");
	@NotNull Icon VOILE_FILE = IconLoader.getIcon("/icons/voile/voile_file.svg");
	@NotNull Icon VOILE = IconLoader.getIcon("/icons/voile/voile.svg");
	@NotNull Icon NARC_FILE = IconLoader.getIcon("/icons/narc/narc_file.svg");
	@NotNull Icon NARC = IconLoader.getIcon("/icons/narc/narc.svg");
	@NotNull Icon VITALYR_FILE = AllIcons.FileTypes.Custom;
	@NotNull Icon VITALYR = SemanticIcons.BLUE_HOLE;
}
