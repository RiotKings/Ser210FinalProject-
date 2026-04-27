package com.example.ser210_final_client

import com.example.ser210_final_client.model.Repo
import org.junit.Assert.*
import org.junit.Test

class RepoScreenTest {

    // Test that a Repo object is created correctly
    @Test
    fun repo_createdWithCorrectValues() {
        val repo = Repo(
            name = "my-portfolio",
            description = "Personal portfolio website",
            githubUrl = "https://github.com/user/my-portfolio"
        )

        assertEquals("my-portfolio", repo.name)
        assertEquals("Personal portfolio website", repo.description)
        assertEquals("https://github.com/user/my-portfolio", repo.githubUrl)
    }

    // Test that a repo name is not blank
    @Test
    fun repo_nameIsNotBlank() {
        val repo = Repo(
            name = "my-portfolio",
            description = "A portfolio",
            githubUrl = "https://github.com"
        )

        assertTrue(repo.name.isNotBlank())
    }

    // Test that empty name should NOT be added (mirrors the UI logic)
    @Test
    fun repo_emptyNameShouldNotBeAdded() {
        val name = ""
        assertFalse(name.isNotBlank())
    }

    // Test that a default GitHub URL is used when none is provided
    @Test
    fun repo_defaultUrlUsedWhenEmpty() {
        val inputUrl = ""
        val resolvedUrl = inputUrl.ifEmpty { "https://github.com" }
        assertEquals("https://github.com", resolvedUrl)
    }

    // Test that a list of repos increases when one is added
    @Test
    fun repoList_increasesAfterAdding() {
        val repos = mutableListOf(
            Repo("repo1", "First repo", "https://github.com"),
            Repo("repo2", "Second repo", "https://github.com")
        )

        repos.add(Repo("repo3", "Third repo", "https://github.com"))

        assertEquals(3, repos.size)
    }
}