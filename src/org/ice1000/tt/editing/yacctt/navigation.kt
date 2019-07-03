package org.ice1000.tt.editing.yacctt

import com.intellij.navigation.ChooseByNameContributor
import com.intellij.navigation.GotoClassContributor
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.stubs.StubIndexKey
import org.ice1000.tt.psi.yacctt.*

abstract class YaccTTNavigationContributor<T>(
	private val indexKey: StubIndexKey<String, T>,
	private val clazz: Class<T>
) : ChooseByNameContributor, GotoClassContributor where T : PsiElement, T : NavigationItem {

	override fun getNames(project: Project?, includeNonProjectItems: Boolean): Array<out String> = when (project) {
		null -> emptyArray()
		else -> StubIndex.getInstance().getAllKeys(indexKey, project).toTypedArray()
	}

	override fun getItemsByName(
		name: String?,
		pattern: String?,
		project: Project?,
		includeNonProjectItems: Boolean
	): Array<out NavigationItem> {
		if (project == null || name == null) return emptyArray()
		val scope = when {
			includeNonProjectItems -> GlobalSearchScope.allScope(project)
			else -> GlobalSearchScope.projectScope(project)
		}

		return StubIndex.getElements(indexKey, name, project, scope, clazz).toTypedArray<NavigationItem>()
	}

	override fun getQualifiedName(item: NavigationItem?): String? = item?.name
	override fun getQualifiedNameSeparator(): String = "."
}

class YaccTTModuleNavigationContributor : YaccTTNavigationContributor<YaccTTModule>(
	YaccTTModuleStubKey.key,
	YaccTTModule::class.java
)

class YaccTTDeclNavigationContributor : YaccTTNavigationContributor<YaccTTDef>(
	YaccTTDefStubKey.key,
	YaccTTDef::class.java
)

class YaccTTDataNavigationContributor : YaccTTNavigationContributor<YaccTTData>(
	YaccTTDataStubKey.key,
	YaccTTData::class.java
)

class YaccTTLabelNavigationContributor : YaccTTNavigationContributor<YaccTTLabel>(
	YaccTTLabelStubKey.key,
	YaccTTLabel::class.java
)
