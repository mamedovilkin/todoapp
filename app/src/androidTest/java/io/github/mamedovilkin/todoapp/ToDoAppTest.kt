package io.github.mamedovilkin.todoapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import io.github.mamedovilkin.todoapp.ui.theme.ToDoAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ToDoAppTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        composeTestRule.setContent {
            ToDoAppTheme {
                ToDoApp()
            }
        }
    }

    @Test
    fun homeScreen_noTasksYetTextDisplayed() {
        composeTestRule
            .onNodeWithText("No tasks yet")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_addNewTaskTaskDisplayed() {
        composeTestRule
            .onNodeWithText("New task")
            .performClick()

        composeTestRule
            .onNodeWithTag("New task")
            .performTextInput("Walk my dog")

        composeTestRule
            .onNodeWithTag("Date")
            .performClick()

        composeTestRule
            .onNodeWithText("OK")
            .performClick()

        composeTestRule
            .onNodeWithTag("Time")
            .performClick()

        composeTestRule
            .onNodeWithText("OK")
            .performClick()

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
                .onNodeWithTag("New task")
                .performTextInput("Task #$i")

            composeTestRule
                .onNodeWithTag("Date")
                .performClick()

            composeTestRule
                .onNodeWithText("OK")
                .performClick()

            composeTestRule
                .onNodeWithTag("Time")
                .performClick()

            composeTestRule
                .onNodeWithText("OK")
                .performClick()

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
            .onNodeWithTag("New task")
            .performTextInput("Walk my dog")

        composeTestRule
            .onNodeWithTag("Date")
            .performClick()

        composeTestRule
            .onNodeWithText("OK")
            .performClick()

        composeTestRule
            .onNodeWithTag("Time")
            .performClick()

        composeTestRule
            .onNodeWithText("OK")
            .performClick()

        composeTestRule
            .onNodeWithText("Save")
            .performClick()

        composeTestRule
            .onNodeWithTag("Task")
            .performClick()

        composeTestRule
            .onNodeWithTag("Edit task")
            .performTextClearance()

        composeTestRule
            .onNodeWithTag("Edit task")
            .performTextInput("Clean my room up")

        composeTestRule
            .onNodeWithTag("Date")
            .performClick()

        composeTestRule
            .onNodeWithText("OK")
            .performClick()

        composeTestRule
            .onNodeWithTag("Time")
            .performClick()

        composeTestRule
            .onNodeWithText("OK")
            .performClick()

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
            .onNodeWithTag("New task")
            .performTextInput("Walk my dog")

        composeTestRule
            .onNodeWithTag("Date")
            .performClick()

        composeTestRule
            .onNodeWithText("OK")
            .performClick()

        composeTestRule
            .onNodeWithTag("Time")
            .performClick()

        composeTestRule
            .onNodeWithText("OK")
            .performClick()

        composeTestRule
            .onNodeWithText("Save")
            .performClick()

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
            .onNodeWithText("New task")
            .performClick()

        composeTestRule
            .onNodeWithTag("New task")
            .performTextInput("Walk my dog")

        composeTestRule
            .onNodeWithTag("Date")
            .performClick()

        composeTestRule
            .onNodeWithText("OK")
            .performClick()

        composeTestRule
            .onNodeWithTag("Time")
            .performClick()

        composeTestRule
            .onNodeWithText("OK")
            .performClick()

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
            .onNodeWithText("New task")
            .performClick()

        composeTestRule
            .onNodeWithTag("New task")
            .performTextInput("Walk my dog")

        composeTestRule
            .onNodeWithTag("Date")
            .performClick()

        composeTestRule
            .onNodeWithText("OK")
            .performClick()

        composeTestRule
            .onNodeWithTag("Time")
            .performClick()

        composeTestRule
            .onNodeWithText("OK")
            .performClick()

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
                .onNodeWithTag("New task")
                .performTextInput("Task #$i")

            composeTestRule
                .onNodeWithTag("Date")
                .performClick()

            composeTestRule
                .onNodeWithText("OK")
                .performClick()

            composeTestRule
                .onNodeWithTag("Time")
                .performClick()

            composeTestRule
                .onNodeWithText("OK")
                .performClick()

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
                .onNodeWithTag("New task")
                .performTextInput("Task #$i")

            composeTestRule
                .onNodeWithTag("Date")
                .performClick()

            composeTestRule
                .onNodeWithText("OK")
                .performClick()

            composeTestRule
                .onNodeWithTag("Time")
                .performClick()

            composeTestRule
                .onNodeWithText("OK")
                .performClick()

            composeTestRule
                .onNodeWithText("Save")
                .performClick()
        }

        composeTestRule
            .onNodeWithTag("Tasks List")
            .performScrollToIndex(10)

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
                .onNodeWithTag("New task")
                .performTextInput("Task #$i")

            composeTestRule
                .onNodeWithTag("Date")
                .performClick()

            composeTestRule
                .onNodeWithText("OK")
                .performClick()

            composeTestRule
                .onNodeWithTag("Time")
                .performClick()

            composeTestRule
                .onNodeWithText("OK")
                .performClick()

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
    fun homeScreen_stickySearchBarResultDisplayed() {
        repeat(2) { i ->
            composeTestRule
                .onNodeWithText("New task")
                .performClick()

            composeTestRule
                .onNodeWithTag("New task")
                .performTextInput("Task #$i")

            composeTestRule
                .onNodeWithTag("Date")
                .performClick()

            composeTestRule
                .onNodeWithText("OK")
                .performClick()

            composeTestRule
                .onNodeWithTag("Time")
                .performClick()

            composeTestRule
                .onNodeWithText("OK")
                .performClick()

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
            .onNodeWithTag("New task")
            .performTextInput("Task")

        composeTestRule
            .onNodeWithTag("Date")
            .performClick()

        composeTestRule
            .onNodeWithText("OK")
            .performClick()

        composeTestRule
            .onNodeWithTag("Time")
            .performClick()

        composeTestRule
            .onNodeWithText("OK")
            .performClick()

        composeTestRule
            .onNodeWithText("Save")
            .performClick()

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
            .onNodeWithTag("New task")
            .performTextInput("Walk my dog")

        composeTestRule
            .onNodeWithTag("Date")
            .performClick()

        composeTestRule
            .onNodeWithText("OK")
            .performClick()

        composeTestRule
            .onNodeWithTag("Time")
            .performClick()

        composeTestRule
            .onNodeWithText("OK")
            .performClick()

        composeTestRule
            .onNodeWithText("Save")
            .performClick()

        composeTestRule
            .onNodeWithTag("Task")
            .performClick()

        composeTestRule
            .onNodeWithTag("Delete")
            .performClick()

        composeTestRule
            .onNodeWithText("Walk my dog")
            .assertIsDisplayed()
    }
}