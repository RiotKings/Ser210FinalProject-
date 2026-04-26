package com.example.ser210_final_client.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ser210_final_client.R
import com.example.ser210_final_client.adapters.RepoAdapter
import com.example.ser210_final_client.model.Repo

class RepoScreen : Fragment() {

    private lateinit var adapter: RepoAdapter
    private val repoList = mutableListOf(
        Repo("my-portfolio", "Personal portfolio website", "https://github.com"),
        Repo("android-app", "SER210 Final Project client app", "https://github.com")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_repo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_repos)
        adapter = RepoAdapter(repoList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Create Repo button
        val btnCreateRepo = view.findViewById<Button>(R.id.btn_create_repo)
        btnCreateRepo.setOnClickListener {
            showCreateRepoDialog()
        }
    }

    private fun showCreateRepoDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_create_repo, null)

        val etName = dialogView.findViewById<EditText>(R.id.et_repo_name)
        val etDescription = dialogView.findViewById<EditText>(R.id.et_repo_description)
        val etUrl = dialogView.findViewById<EditText>(R.id.et_repo_url)

        AlertDialog.Builder(requireContext())
            .setTitle("Create New Repository")
            .setView(dialogView)
            .setPositiveButton("Create") { _, _ ->
                val name = etName.text.toString().trim()
                val desc = etDescription.text.toString().trim()
                val url = etUrl.text.toString().trim()
                    .ifEmpty { "https://github.com" }

                if (name.isNotEmpty()) {
                    adapter.addRepo(Repo(name, desc, url))
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}