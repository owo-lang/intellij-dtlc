package org.ice1000.tt

import com.intellij.lang.ParserDefinition
import com.intellij.testFramework.ParsingTestCase
import org.ice1000.tt.psi.acore.ACoreParserDefinition
import org.ice1000.tt.psi.agda.AgdaParserDefinition
import org.ice1000.tt.psi.minitt.MiniTTParserDefinition
import org.ice1000.tt.psi.mlpolyr.MLPolyRParserDefinition
import org.ice1000.tt.psi.redprl.RedPrlParserDefinition

abstract class DtlcParsingTestCase(
	dataPath: String,
	fileExt: String,
	parserDefinition: ParserDefinition
) : ParsingTestCase(dataPath, fileExt, parserDefinition) {
	override fun getTestDataPath() = "testData"
	override fun getTestName(lowercaseFirstLetter: Boolean) =
		super.getTestName(lowercaseFirstLetter).trim()
}

class MiniTTParsingTest : DtlcParsingTestCase("parse/minitt", "minitt", MiniTTParserDefinition()) {
	fun `test parse-only`() = doTest(true, true)
	fun `test local-binding`() = doTest(true, true)
	fun `test reference`() = doTest(true, true)
	fun `test simple`() = doTest(true, true)
	fun `test subtyping`() = doTest(true, true)
	fun `test sum-literals`() = doTest(true, true)
	fun `test syntacic-sugar`() = doTest(true, true)
	fun `test unicode`() = doTest(true, true)
	fun `test univese`() = doTest(true, true)
	fun `test bool`() = doTest(true, true)
	fun `test maybe`() = doTest(true, true)
	fun `test merge`() = doTest(true, true)
	fun `test nat`() = doTest(true, true)
}

class ACoreParsingTest : DtlcParsingTestCase("parse/acore", "mtt", ACoreParserDefinition()) {
	fun testExample() = doTest(true, true)
	fun testMaybe() = doTest(true, true)
	fun testBlockComment() = doTest(true, true)
	fun testFunSum() = doTest(true, true)
}

class MLPolyRParsingTest : DtlcParsingTestCase("parse/mlpolyr", "mlpr", MLPolyRParserDefinition()) {
	fun `test comments`() = doTest(true, true)
	fun `test singleton`() = doTest(true, true)
	fun `test twosel`() = doTest(true, true)
	fun `test catchall`() = doTest(true, true)
	fun `test catchall2`() = doTest(true, true)
	fun `test catchall3`() = doTest(true, true)
	fun `test assoc`() = doTest(true, true)
	fun `test astest`() = doTest(true, true)
	fun `test file template`() = doTest(true, true)
	fun `test cps-convert-cc2`() = doTest(true, true)
	fun `test cps-convert-cc2-wc`() = doTest(true, true)
}

class RedPrlParsingTest : DtlcParsingTestCase("parse/redprl", "prl", RedPrlParserDefinition()) {
	fun `test category`() = doTest(true, true)
	fun `test connection`() = doTest(true, true)
	fun `test groupoid`() = doTest(true, true)
	fun `test hlevels`() = doTest(true, true)
	fun `test isotoequiv`() = doTest(true, true)
	fun `test J`() = doTest(true, true)
	fun `test metalanguage`() = doTest(true, true)
	fun `test theorem-of-choice`() = doTest(true, true)
	fun `test invariance`() = doTest(true, true)
	fun `test equality-elim`() = doTest(true, true)
}

class AgdaParsingTest : DtlcParsingTestCase("parse/agda", "agda", AgdaParserDefinition()) {
	fun `test module-layout`() = doTest(true, true)
	fun `test private-primitive-variable`() = doTest(true, true)
	fun `test complex-nested-layouts`() = doTest(true, true)
	fun `test imports`() = doTest(true, true)
	fun `test records`() = doTest(true, true)
	fun `test one-linear-layout`() = doTest(true, true)

	fun testBetterExampleOfDot() = doTest(true, true)
	fun testConstSquare() = doTest(true, true)
}
