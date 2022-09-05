package com.daniil.shevtsov.fightsimulator.common.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MainCoroutineRule : TestRule {

    private val dispatcher = TestCoroutineDispatcher()
    private val scope = TestCoroutineScope(dispatcher)

    override fun apply(base: Statement, description: Description?) = object : Statement() {
        @Throws(Throwable::class)
        override fun evaluate() {
            Dispatchers.setMain(dispatcher)

            base.evaluate()

            Dispatchers.resetMain()
            scope.cleanupTestCoroutines()
        }
    }

    fun runBlockingTest(block: suspend TestCoroutineScope.() -> Unit) =
        scope.runBlockingTest { block() }
}
