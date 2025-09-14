package com.javier.presentation.components

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.javier.presentation.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EmptyTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenEmptyScreenIsDisplayedWithoutMessageItDisplaysDefaultMessage() {
        composeTestRule.setContent {
            Empty()
        }

        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val defaultMessage = context.resources.getString(R.string.empty_list)

        composeTestRule.onNodeWithText(defaultMessage).assertIsDisplayed()
        composeTestRule.onNodeWithTag("errorMessageText").assertIsDisplayed()
    }

    @Test
    fun whenEmptyScreenIsDisplayedWithMessageItDisplaysCustomMessage() {
        val message = "empty list"

        composeTestRule.setContent {
            Empty(message = message)
        }

        composeTestRule.onNodeWithText(message).assertIsDisplayed()
        composeTestRule.onNodeWithTag("errorMessageText").assertIsDisplayed()
    }
}
