package icons;

import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public interface SemanticIcons {
	@NotNull Icon BLUE_T = IconLoader.getIcon("/icons/ast/blue_t.png");
	@NotNull Icon BLUE_C = IconLoader.getIcon("/icons/ast/blue_c.png");
	@NotNull Icon PURPLE_T = IconLoader.getIcon("/icons/ast/purple_t.png");
	@NotNull Icon PURPLE_P = IconLoader.getIcon("/icons/ast/purple_p.png");
	@NotNull Icon PINK_LAMBDA = IconLoader.getIcon("/icons/ast/pink_lambda.png");
	@NotNull Icon ORANGE_F = IconLoader.getIcon("/icons/ast/orange_f.png");
	@NotNull Icon ORANGE_LAMBDA = IconLoader.getIcon("/icons/ast/orange_lambda.png");
	@NotNull Icon ORANGE_V = IconLoader.getIcon("/icons/ast/orange_v.png");
	@NotNull Icon ORANGE_P = IconLoader.getIcon("/icons/ast/orange_p.png");
}
