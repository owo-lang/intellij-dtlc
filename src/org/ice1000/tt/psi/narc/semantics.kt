package org.ice1000.tt.psi.narc

import com.intellij.lang.ASTNode
import org.ice1000.tt.psi.GeneralReference

abstract class NarcNameUsageMixin(node: ASTNode) : GeneralReference(node), NarcNameUsage {
}
