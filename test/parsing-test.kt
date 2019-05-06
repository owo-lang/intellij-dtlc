package org.ice1000.tt

import com.intellij.lang.ParserDefinition
import com.intellij.testFramework.ParsingTestCase
import org.ice1000.tt.psi.acore.ACoreParserDefinition
import org.ice1000.tt.psi.minitt.MiniTTParserDefinition
import org.ice1000.tt.psi.mlpolyr.MLPolyRParserDefinition

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
	fun `test singleton`() = doTest(true, true)
	fun `test twosel`() = doTest(true, true)
}
