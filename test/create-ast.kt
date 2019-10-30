package org.ice1000.tt

import com.intellij.testFramework.LightPlatformTestCase
import org.ice1000.tt.psi.agda.AgdaTokenType
import org.ice1000.tt.psi.cubicaltt.CubicalTTTokenType
import org.ice1000.tt.psi.mlang.MlangTokenType
import org.ice1000.tt.psi.mlpolyr.MLPolyRTokenType
import org.ice1000.tt.psi.narc.NarcTokenType
import org.ice1000.tt.psi.redprl.RedPrlTokenType
import org.ice1000.tt.psi.vitalyr.VitalyRTokenType
import org.ice1000.tt.psi.voile.VoileTokenType

class RedPrlCreateAstTest : LightPlatformTestCase() {
	fun testCreate() {
		val project = project
		RedPrlTokenType.createOpDecl("Marisa", project)!!
		RedPrlTokenType.createOpUsage("Alice", project)!!
		RedPrlTokenType.createVarDecl("reimu", project)!!
		RedPrlTokenType.createVarUsage("suika", project)!!
		RedPrlTokenType.createBoundVar("yukari", project)!!
		RedPrlTokenType.createMetaDecl("#baka", project)!!
		RedPrlTokenType.createMetaUsage("#veryBaka", project)!!
	}
}

class AgdaCreateAstTest : LightPlatformTestCase() {
	fun testCreate() {
		val project = project
		AgdaTokenType.createStr("\"Come and get your love!\"", project)!!
		AgdaTokenType.createNameExp("OhOh", project)!!
	}
}

class CubicalTTCreateAstTest : LightPlatformTestCase() {
	fun testCreate() {
		val project = project
		CubicalTTTokenType.createNameDecl("hulk", project)!!
		CubicalTTTokenType.createNameExp("thor", project)!!
	}
}

class VoileCreateAstTest : LightPlatformTestCase() {
	fun testCreate() {
		val project = project
		VoileTokenType.createNameDecl("hulk", project)!!
		VoileTokenType.createNameUsage("thor", project)!!
	}
}

class MLPolyRCreateAstTest : LightPlatformTestCase() {
	fun testCreate() {
		val project = project
		MLPolyRTokenType.createLabel("Van", project)!!
		MLPolyRTokenType.createCon("`Van", project)!!
		MLPolyRTokenType.createStr("\"What's the matter with your head\"", project)!!
	}
}

class MlangCreateAstTest : LightPlatformTestCase() {
	fun testCreate() {
		val project = project
		MlangTokenType.createIdent("sticky", project)!!
		MlangTokenType.createRefExpr("fingers", project)!!
	}
}

class NarcCreateAstTest : LightPlatformTestCase() {
	fun testCreate() {
		val project = project
		NarcTokenType.createNameDecl("gold", project)!!
		NarcTokenType.createNameUsage("experience", project)!!
	}
}

class VitalyRCreateAstTest : LightPlatformTestCase() {
	fun testCreate() {
		val project = project
		VitalyRTokenType.createNameDecl("moody", project)!!
		VitalyRTokenType.createNameUsage("blues", project)!!
	}
}
