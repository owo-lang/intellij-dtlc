package org.ice1000.minitt.project

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil

interface MiniTTProjectSettingsService {
	val settings: MiniTTSettings
}

val Project.minittSettings: MiniTTProjectSettingsService
	get() = ServiceManager.getService(this, MiniTTProjectSettingsService::class.java)

@State(
	name = "MiniTTProjectSettings",
	storages = [Storage(file = "miniTTConfig.xml")])
class MiniTTProjectSettingsServiceImpl :
	MiniTTProjectSettingsService, PersistentStateComponent<MiniTTSettings> {
	override val settings = MiniTTSettings()
	override fun getState(): MiniTTSettings? = XmlSerializerUtil.createCopy(settings)
	override fun loadState(state: MiniTTSettings) {
		XmlSerializerUtil.copyBean(state, settings)
	}
}