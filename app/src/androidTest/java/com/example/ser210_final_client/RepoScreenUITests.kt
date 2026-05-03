package com.example.ser210_final_client

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ser210_final_client.model.MainViewModel
import com.example.ser210_final_client.model.MainViewModelFactory
import com.example.ser210_final_client.data.database.AppDatabase
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ser210_final_client.screens.RepoScreenContent
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepoScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun buildViewModel(): MainViewModel {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = AppDatabase.getInstance(context)
        return MainViewModelFactory(db).create(MainViewModel::class.java)
    }

    // Test that the header text is visible
    @Test
    fun repoScreen_headerIsDisplayed() {
        val viewModel = buildViewModel()
        composeTestRule.setContent {
            RepoScreenContent(viewModel)
        }
        composeTestRule.onNodeWithText("Code-Gram").assertIsDisplayed()
    }

    // Test that the Create Repo button is visible
    @Test
    fun repoScreen_createRepoButtonIsDisplayed() {
        val viewModel = buildViewModel()
        composeTestRule.setContent {
            RepoScreenContent(viewModel)
        }
        composeTestRule.onNodeWithText("Create Repo").assertIsDisplayed()
    }

    // Test that pre-loaded repos are visible
    @Test
    fun repoScreen_preloadedReposAreDisplayed() {
        val viewModel = buildViewModel()
        composeTestRule.setContent {
            RepoScreenContent(viewModel)
        }
        composeTestRule.onNodeWithText("my-portfolio").assertIsDisplayed()
        composeTestRule.onNodeWithText("android-app").assertIsDisplayed()
    }

    // Test that tapping Create Repo opens the dialog
    @Test
    fun repoScreen_createRepoDialogOpens() {
        val viewModel = buildViewModel()
        composeTestRule.setContent {
            RepoScreenContent(viewModel)
        }
        composeTestRule.onNodeWithText("Create Repo").performClick()
        composeTestRule.onNodeWithText("Create New Repository").assertIsDisplayed()
    }

    // Test that a new repo appears in the list after creation
    @Test
    fun repoScreen_newRepoAppearsAfterCreation() {
        val viewModel = buildViewModel()
        composeTestRule.setContent {
            RepoScreenContent(viewModel)
        }

        // Open dialog
        composeTestRule.onNodeWithText("Create Repo").performClick()

        // Type repo name
        composeTestRule.onNodeWithText("Repository Name").performTextInput("test-repo")

        // Click Create
        composeTestRule.onNodeWithText("Create").performClick()

        // Verify new repo appears in the list
        composeTestRule.onNodeWithText("test-repo").assertIsDisplayed()
    }
}