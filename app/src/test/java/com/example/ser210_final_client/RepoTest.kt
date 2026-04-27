package com.example.ser210_final_client

import com.example.ser210_final_client.data.database.Repo
import org.junit.Assert.*
import org.junit.Test

class RepoTest {

    // Test that a Repo object is created correctly
    @Test
    fun repo_createdWithCorrectValues() {
        val repo = Repo(
            id = 0,
            userId = "user123",
            repoName = "my-project",
            description = "Cool project",
            url = "https://github.com/user123/my-project"
        )

        assertEquals("user123", repo.userId)
        assertEquals("my-project", repo.repoName)
        assertEquals("Cool project", repo.description)
        assertEquals("https://github.com/user123/my-project", repo.url)
    }

    // Test that repo name is not blank
    @Test
    fun repo_nameIsNotBlank() {
        val repoName = "my-project"
        assertTrue(repoName.isNotBlank())
    }

    // Test that empty repo name should not be allowed
    @Test
    fun repo_emptyNameShouldNotBeAdded() {
        val repoName = ""
        assertFalse(repoName.isNotBlank())
    }

    // Test default URL logic
    @Test
    fun repo_defaultUrlUsedWhenEmpty() {
        val inputUrl = ""
        val resolvedUrl = inputUrl.ifEmpty { "https://github.com" }

        assertEquals("https://github.com", resolvedUrl)
    }

    // Test list increases after adding a repo
    @Test
    fun repoList_increasesAfterAdding() {
        val repos = mutableListOf(
            Repo(0, "u1", "repo1", "desc1", "url"),
            Repo(0, "u2", "repo2", "desc2", "url")
        )

        repos.add(Repo(0, "u3", "repo3", "desc3", "url"))

        assertEquals(3, repos.size)
    }
}