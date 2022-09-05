package com.daniil.shevtsov.fightsimulator.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MultipleStorage<Key, Value> @Inject constructor(
    initialValues: List<Value>,
    private val keyExtractor: (value: Value) -> Key,
) {
    private val storedData: MutableStateFlow<Map<Key, Value>> =
        MutableStateFlow(initialValues.associateBy { keyExtractor(it) })

    fun observeAll(): Flow<List<Value>> {
        return storedData.map { it.values.toList() }
    }

    fun updateAll(newValues: List<Value>) {
        storedData.value = newValues.associateBy { keyExtractor(it) }
    }

    fun getByKey(key: Key): Value? {
        val map = storedData.value
        return map[key]
    }

    fun updateByKey(key: Key, newValue: Value) {
        updateByKey(key = key, transformation = { newValue })
    }

    fun updateByKey(key: Key, transformation: (oldValue: Value?) -> Value) {
        val map = storedData.value
        val modifiedMap = map
            .toMutableMap()
            .apply {
                val oldValue = get(key)
                val newValue = transformation(oldValue)
                put(key, newValue)
            }
            .toMap()
        storedData.value = modifiedMap
    }

}
