package com.daniil.shevtsov.fightsimulator.core.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Preferences {
    var username: String by FileDelegate()
}

class FileDelegate() : ReadWriteProperty<Preferences, String> {

    private var cachedValue: String = ""

    override fun getValue(thisRef: Preferences, property: KProperty<*>): String {
        return cachedValue
    }

    override fun setValue(thisRef: Preferences, property: KProperty<*>, value: String) {
        cachedValue = value
    }

}

class CacheTest {

    @Test
    fun `kek`() {
        val preferences = Preferences()
        assertThat(preferences.username).isEqualTo("")
        preferences.username = "kek"
        assertThat(preferences.username).isEqualTo("kek")
    }

}
