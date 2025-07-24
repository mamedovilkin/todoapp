/*
    IMPORTANT TO KNOW:
    TO RUN TESTS IN THIS FILE YOU NEED TO COMMENT THAT PART OF CODE
    IN HomeActivity.kt FILE

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ...
    }

    ALSO RUN EACH FUNCTION SEPARATELY
    AND DON'T FORGET UNCOMMENT THAT PART OF CODE AFTER ALL TESTS WILL FINISH.
*/
package io.github.mamedovilkin.todoapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import io.github.mamedovilkin.todoapp.ui.activity.home.HomeActivity
import io.github.mamedovilkin.todoapp.util.convertToTime
import org.junit.Rule
import org.junit.Test
import java.util.Calendar

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<HomeActivity>()

    @Test
    fun homeScreen_settingsButtonClickedSettingsDisplayed() {
        composeTestRule.waitUntil {
            composeTestRule
                .onNodeWithTag("Settings")
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag("Settings")
            .performClick()

        composeTestRule.waitUntil {
            composeTestRule
                .onAllNodesWithText("Settings")
                .onFirst()
                .isDisplayed()
        }
    }

    @Test
    fun homeScreen_noTasksYetTextDisplayed() {
        composeTestRule.waitUntil {
            composeTestRule
                .onNodeWithText("No tasks yet")
                .isDisplayed()
        }
    }

    @Test
    fun homeScreen_addNewTaskTaskDisplayed() {
        composeTestRule
            .onNodeWithText("New task")
            .performClick()

        composeTestRule
            .onNodeWithTag("Title")
            .performTextInput("Walk my dog")

        composeTestRule
            .onNodeWithText("Save")
            .performClick()

        composeTestRule
            .onNodeWithText("Walk my dog")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_toggleDoneTaskChecked() {
        repeat(15) { i ->
            composeTestRule
                .onNodeWithText("New task")
                .performClick()

            composeTestRule
                .onNodeWithTag("Title")
                .performTextInput("Task #$i")

            composeTestRule
                .onNodeWithText("Save")
                .performClick()
        }

        composeTestRule
            .onAllNodesWithTag("Toggle")
            .onFirst()
            .performClick()

        composeTestRule
            .onNodeWithTag("Tasks List")
            .performScrollToIndex(14)

        composeTestRule
            .onAllNodesWithTag("Toggle")
            .onLast()
            .assertIsOn()
    }

    @Test
    fun homeScreen_updateTaskUpdatedTaskDisplayed() {
        composeTestRule
            .onNodeWithText("New task")
            .performClick()

        composeTestRule
            .onNodeWithTag("Title")
            .performTextInput("Walk my dog")

        composeTestRule
            .onNodeWithText("Save")
            .performClick()

        composeTestRule
            .onNodeWithTag("Task")
            .performClick()

        composeTestRule
            .onNodeWithTag("Title")
            .performTextClearance()

        composeTestRule
            .onNodeWithTag("Title")
            .performTextInput("Clean my room up")

        composeTestRule
            .onNodeWithText("Save")
            .performClick()

        composeTestRule
            .onNodeWithText("Clean my room up")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_deleteTaskTaskNotDisplayed() {
        composeTestRule
            .onNodeWithText("New task")
            .performClick()

        composeTestRule
            .onNodeWithTag("Title")
            .performTextInput("Walk my dog")

        composeTestRule
            .onNodeWithText("Save")
            .performClick()

        composeTestRule
            .onNodeWithTag("Toggle")
            .performClick()

        composeTestRule
            .onNodeWithText("Walk my dog")
            .performTouchInput {
                swipeLeft()
            }

        composeTestRule.waitUntil {
            composeTestRule
                .onNodeWithTag("Delete")
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag("Delete")
            .performClick()

        composeTestRule
            .onNodeWithText("Walk my dog")
            .assertIsNotDisplayed()

        composeTestRule
            .onNodeWithText("No tasks yet")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_statisticsCardDisplayed() {
        composeTestRule
            .onNodeWithTag("Settings")
            .performClick()

        composeTestRule
            .onNodeWithTag("Show Statistics")
            .performClick()

        composeTestRule
            .onNodeWithTag("Back")
            .performClick()

        composeTestRule
            .onNodeWithText("New task")
            .performClick()

        composeTestRule
            .onNodeWithTag("Title")
            .performTextInput("Walk my dog")

        composeTestRule
            .onNodeWithText("Save")
            .performClick()

        composeTestRule
            .onNodeWithText("You have task to do!")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_statisticsCardAllTasksCompletedDisplayed() {
        composeTestRule
            .onNodeWithTag("Settings")
            .performClick()

        composeTestRule
            .onNodeWithTag("Show Statistics")
            .performClick()

        composeTestRule
            .onNodeWithTag("Back")
            .performClick()

        composeTestRule
            .onNodeWithText("New task")
            .performClick()

        composeTestRule
            .onNodeWithTag("Title")
            .performTextInput("Walk my dog")

        composeTestRule
            .onNodeWithText("Save")
            .performClick()

        composeTestRule
            .onNodeWithTag("Toggle")
            .performClick()

        composeTestRule
            .onNodeWithText("All tasks completed!")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_scrollListUpButtonDisplayed() {
        repeat(15) { i ->
            composeTestRule
                .onNodeWithText("New task")
                .performClick()

            composeTestRule
                .onNodeWithTag("Title")
                .performTextInput("Task #$i")

            composeTestRule
                .onNodeWithText("Save")
                .performClick()
        }

        composeTestRule
            .onNodeWithTag("Tasks List")
            .performScrollToIndex(10)

        composeTestRule
            .onNodeWithTag("Up")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_upButtonClickedScrollToFirstItemDisplayed() {
        repeat(15) { i ->
            composeTestRule
                .onNodeWithText("New task")
                .performClick()

            composeTestRule
                .onNodeWithTag("Title")
                .performTextInput("Task #$i")

            composeTestRule
                .onNodeWithText("Save")
                .performClick()
        }

        composeTestRule
            .onNodeWithTag("Tasks List")
            .performScrollToIndex(10)

        composeTestRule.waitUntil {
            composeTestRule
                .onNodeWithText("Task #10")
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag("Up")
            .performClick()

        composeTestRule
            .onNodeWithText("Task #0")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_scrollToBottomNewTaskButtonTextNotDisplayed() {
        repeat(15) { i ->
            composeTestRule
                .onNodeWithText("New task")
                .performClick()

            composeTestRule
                .onNodeWithTag("Title")
                .performTextInput("Task #$i")

            composeTestRule
                .onNodeWithText("Save")
                .performClick()
        }

        composeTestRule
            .onNodeWithTag("Tasks List")
            .performScrollToIndex(10)

        composeTestRule
            .onNodeWithText("New task")
            .assertIsNotDisplayed()

        composeTestRule
            .onNodeWithTag("Tasks List")
            .performScrollToIndex(0)

        composeTestRule
            .onNodeWithText("New task")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_stickySearchBarClearButtonIsDisplayed() {
        composeTestRule
            .onNodeWithText("New task")
            .performClick()

        composeTestRule
            .onNodeWithTag("Title")
            .performTextInput("Task")

        composeTestRule
            .onNodeWithText("Save")
            .performClick()

        composeTestRule
            .onNodeWithText("Search")
            .performTextInput("Test")

        composeTestRule
            .onNodeWithTag("Clear")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_stickySearchBarClearButtonClickedQueryIsEmpty() {
        composeTestRule
            .onNodeWithText("New task")
            .performClick()

        composeTestRule
            .onNodeWithTag("Title")
            .performTextInput("Task")

        composeTestRule
            .onNodeWithText("Save")
            .performClick()

        composeTestRule
            .onNodeWithText("Search")
            .performTextInput("Test")

        composeTestRule
            .onNodeWithTag("Clear")
            .performClick()

        composeTestRule
            .onNodeWithText("Test")
            .assertIsNotDisplayed()
    }

    @Test
    fun homeScreen_stickySearchBarResultDisplayed() {
        repeat(2) { i ->
            composeTestRule
                .onNodeWithText("New task")
                .performClick()

            composeTestRule
                .onNodeWithTag("Title")
                .performTextInput("Task #$i")

            composeTestRule
                .onNodeWithText("Save")
                .performClick()
        }

        composeTestRule
            .onNodeWithText("Search")
            .performTextInput("Task #2")

        composeTestRule
            .onNodeWithText("Task #2")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_snackbarDisplayedUndoClicked() {
        composeTestRule
            .onNodeWithText("New task")
            .performClick()

        composeTestRule
            .onNodeWithTag("Title")
            .performTextInput("Task")

        composeTestRule
            .onNodeWithText("Save")
            .performClick()

        composeTestRule
            .onNodeWithTag("Toggle")
            .performClick()

        composeTestRule
            .onNodeWithText("Task")
            .performTouchInput {
                swipeLeft()
            }

        composeTestRule.waitUntil {
            composeTestRule
                .onNodeWithTag("Delete")
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag("Delete")
            .performClick()

        composeTestRule
            .onNodeWithText("UNDO")
            .performClick()

        composeTestRule
            .onNodeWithText("Task")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_deleteTaskButtonClickedTaskNotDisplayed() {
        composeTestRule
            .onNodeWithText("New task")
            .performClick()

        composeTestRule
            .onNodeWithTag("Title")
            .performTextInput("Walk my dog")

        composeTestRule
            .onNodeWithText("Save")
            .performClick()

        composeTestRule
            .onNodeWithTag("Task")
            .performClick()

        composeTestRule
            .onNodeWithText("Delete")
            .performClick()

        composeTestRule
            .onNodeWithText("Walk my dog")
            .assertIsNotDisplayed()
    }

    @Test
    fun homeScreen_taskWithDescriptionDescriptionDisplayed() {
        composeTestRule
            .onNodeWithText("New task")
            .performClick()

        composeTestRule
            .onNodeWithTag("Title")
            .performTextInput("Task")

        composeTestRule
            .onNodeWithText("Description")
            .performTextInput("Description")

        composeTestRule
            .onNodeWithText("Save")
            .performClick()

        composeTestRule
            .onNodeWithText("Description")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_taskWithCategorySelectedCategoryCategoryDisplayed() {
        composeTestRule
            .onNodeWithText("New task")
            .performClick()

        composeTestRule
            .onNodeWithTag("Title")
            .performTextInput("Task")

        composeTestRule
            .onNodeWithText("Category")
            .performTextInput("Category")

        composeTestRule
            .onNodeWithText("Save")
            .performClick()

        composeTestRule
            .onAllNodesWithText("category")
            .onFirst()
            .performClick()

        composeTestRule
            .onAllNodesWithText("category")
            .onLast()
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_premiumAdDisplayedClickedCloseButtonPremiumAdIsNotDisplayed() {
        composeTestRule
            .onNodeWithText("New task")
            .performClick()

        composeTestRule
            .onNodeWithTag("Title")
            .performTextInput("Task")

        composeTestRule
            .onNodeWithText("Save")
            .performClick()

        composeTestRule
            .onNodeWithText("Premium")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("Close")
            .performClick()

        composeTestRule
            .onNodeWithText("Premium")
            .assertIsNotDisplayed()
    }

    @Test
    fun homeScreen_addReminderCheckedTaskWithReminderCreated() {
        composeTestRule
            .onNodeWithText("New task")
            .performClick()

        composeTestRule
            .onNodeWithTag("Title")
            .performTextInput("Task")

        composeTestRule
            .onNodeWithTag("Add reminder")
            .performClick()

        composeTestRule
            .onNodeWithText("Save")
            .performClick()

        val calendar = Calendar.getInstance()
        val time = convertToTime(
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            composeTestRule.activity.applicationContext
        )

        composeTestRule
            .onNodeWithText("Today at $time")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Task")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_addedTaskWithCategorySuggestionsIsDisplayed() {
        composeTestRule
            .onNodeWithText("New task")
            .performClick()

        composeTestRule
            .onNodeWithTag("Title")
            .performTextInput("Task #1")

        composeTestRule
            .onNodeWithTag("Category")
            .performTextInput("task")

        composeTestRule
            .onNodeWithText("Save")
            .performClick()

        composeTestRule
            .onNodeWithText("New task")
            .performClick()

        composeTestRule
            .onNodeWithTag("Category")
            .performClick()

        composeTestRule
            .onAllNodesWithText("task")
            .onLast()
            .assertIsDisplayed()
    }
}