package com.javier.presentation.components

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.javier.presentation.R
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ErrorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenErrorScreenIsDisplayedWithoutMessageItDisplaysDefaultMessage() {

        composeTestRule.setContent {
            Error(onRetry = { })
        }

        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val defaultMessage = context.resources.getString(R.string.unknown_error)

        composeTestRule.onNodeWithText(defaultMessage).assertIsDisplayed()
        composeTestRule.onNodeWithTag("errorMessageText").assertIsDisplayed()

        composeTestRule.onNodeWithTag("retryButton").assertIsDisplayed()
    }

    @Test
    fun whenErrorScreenIsDisplayedWithMessageItDisplaysCustomMessage() {
        val errorMessage = "there has been an error"

        composeTestRule.setContent {
            Error(message = errorMessage, onRetry = { })
        }

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithTag("errorMessageText").assertIsDisplayed()
    }

    @Test
    fun whenErrorScreenIsDisplayedRetryButtonWorks() {
        var retried = false

        composeTestRule.setContent {
            Error(onRetry = { retried = true })
        }

        composeTestRule.onNodeWithTag("retryButton").assertIsDisplayed().performClick()
        assertTrue(retried)
    }
}
