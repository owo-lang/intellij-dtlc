package org.ice1000.tt

import com.intellij.CommonBundle
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.util.*

interface TTFile

object TTBundle {
	@NonNls private const val BUNDLE = "org.ice1000.tt.tt-bundle"
	private val bundle: ResourceBundle by lazy { ResourceBundle.getBundle(BUNDLE) }

	@JvmStatic
	fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
		CommonBundle.message(bundle, key, *params)
}
