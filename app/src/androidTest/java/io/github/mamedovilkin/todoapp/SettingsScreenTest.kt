package io.github.mamedovilkin.todoapp

import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import io.github.mamedovilkin.todoapp.ui.activity.SettingsActivity
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
}