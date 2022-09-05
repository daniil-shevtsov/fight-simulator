package com.daniil.shevtsov.fightsimulator

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

@ExperimentalCoroutinesApi
class MainCoroutineExtension constructor(
    private val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
) : BeforeAllCallback,
    AfterEachCallback,
    AfterAllCallback,
    TestCoroutineScope by TestCoroutineScope(dispatcher) {

    override fun beforeAll(context: ExtensionContext?) {
        Dispatchers.setMain(dispatcher)
    }

    override fun afterEach(context: ExtensionContext?) {
        cleanupTestCoroutines()
    }

    override fun afterAll(context: ExtensionContext?) {
        Dispatchers.resetMain()
    }
}
