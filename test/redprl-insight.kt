package org.ice1000.tt

import com.intellij.testFramework.LightPlatformTestCase
import org.ice1000.tt.psi.redprl.RedPrlTokenType

class RedPrlCreateAstTest : LightPlatformTestCase() {
	fun testCreate() {
		val project = getProject()
		RedPrlTokenType.createOpDecl("Marisa", project)!!
		RedPrlTokenType.createOpUsage("Alice", project)!!
		RedPrlTokenType.createVarDecl("reimu", project)!!
		RedPrlTokenType.createVarUsage("suika", project)!!
		RedPrlTokenType.createBoundVar("yukari", project)!!
		RedPrlTokenType.createMetaDecl("#baka", project)!!
	}
}
