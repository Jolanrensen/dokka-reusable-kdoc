package nl.jolanrensen.dokkaReusableKdocPlugin

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.jetbrains.dokka.base.testApi.testRunner.BaseAbstractTest
import org.junit.Test

class MyAwesomePluginTest : BaseAbstractTest() {
    private val configuration = dokkaConfiguration {
        sourceSets {
            sourceSet {
                sourceRoots = listOf("src/main/kotlin")
            }
        }
    }

    @Test
    fun `my awesome plugin should find packages and classes`() {
        testInline(
            """
            |/src/main/kotlin/sample/Test.kt
            |package sample
            |data class TestingIsEasy(val reason: String)
            """.trimIndent(), configuration
        ) {
            documentablesTransformationStage = { module ->
                val testedPackage = module.packages.find { it.name == "sample" }
                val testedClass = testedPackage?.classlikes?.find { it.name == "TestingIsEasy" }

                assertNotNull(testedPackage)
                assertNotNull(testedClass)
            }
        }
    }

    @Test
    fun `should hide annotated functions`() {
        val configuration = dokkaConfiguration {
            sourceSets {
                sourceSet {
                    sourceRoots = listOf("src/main/kotlin/basic/Test.kt")
                }
            }
        }
        val hideInternalPlugin = DokkaReusableKdocPlugin()

        testInline(
            """
            |/src/main/kotlin/basic/Test.kt
            |package org.jetbrains.dokka.internal.test
            |
            |annotation class Internal
            |
            |fun shouldBeVisible() {}
            |
            |@Internal
            |fun shouldBeExcludedFromDocumentation() {}
        """.trimMargin(),
            configuration = configuration,
            pluginOverrides = listOf(hideInternalPlugin)
        ) {
            preMergeDocumentablesTransformationStage = { modules ->
                val testModule = modules.single { it.name == "root" }
                val testPackage = testModule.packages.single { it.name == "org.jetbrains.dokka.internal.test" }

                val packageFunctions = testPackage.functions
                assertEquals(1, packageFunctions.size)
                assertEquals("shouldBeVisible", packageFunctions[0].name)
            }
        }
    }
}