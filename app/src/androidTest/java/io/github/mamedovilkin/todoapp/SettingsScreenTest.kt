package io.github.mamedovilkin.todoapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.github.mamedovilkin.todoapp.ui.activity.settings.SettingsActivity
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<SettingsActivity>()

    @Test
    fun settingsScreen_toggleShowStatistics() {
        composeTestRule
            .onNodeWithTag("Show Statistics")
            .performClick()

        composeTestRule
            .onNodeWithTag("Show Statistics")
            .assertIsOn()
    }

    @Test
    fun settingsScreen_deleteAllData() {
        composeTestRule
            .onNodeWithText("Delete all data")
            .performClick()

        composeTestRule
            .onNodeWithText("Do you really want to delete all data?")
            .assertIsDisplayed()
    }
}