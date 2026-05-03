package com.example.ser210_final_client.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ser210_final_client.data.database.AppDatabase
import com.example.ser210_final_client.model.MainViewModel
import com.example.ser210_final_client.model.MainViewModelFactory
import com.example.ser210_final_client.model.Repo

class RepoScreen : Fragment() {

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val db = AppDatabase.getInstance(requireContext().applicationContext)
        viewModel = ViewModelProvider(
            requireActivity(),
            MainViewModelFactory(db)
        )[MainViewModel::class.java]

        return ComposeView(requireContext()).apply {
            setContent {
                RepoScreenContent(viewModel)
            }
        }
    }
}

@Composable
fun RepoScreenContent(viewModel: MainViewModel) {
    var repos by remember { mutableStateOf(viewModel.repos) }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        CreateRepoDialog(
            onDismiss = { showDialog = false },
            onCreate = { repo ->
                viewModel.addRepo(repo.name, repo.description, repo.githubUrl)
                repos = viewModel.repos
                showDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            text = "Code-Gram",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6A0DAD),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE8D5FF),
                contentColor = Color.Black
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Create Repo", fontSize = 16.sp)
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(repos) { repo ->
                RepoCard(repo = repo)
            }
        }
    }
}

@Composable
fun RepoCard(repo: Repo) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repo.githubUrl))
                context.startActivity(intent)
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(3.dp, Color(0xFF6A0DAD))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = repo.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6A0DAD)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = repo.description,
                fontSize = 14.sp,
                color = Color(0xFF555555)
            )
        }
    }
}

@Composable
fun CreateRepoDialog(onDismiss: () -> Unit, onCreate: (Repo) -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Repository", color = Color(0xFF6A0DAD)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Repository Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("GitHub URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) {
                    onCreate(
                        Repo(
                            name = name.trim(),
                            description = description.trim(),
                            githubUrl = url.trim().ifEmpty { "https://github.com" }
                        )
                    )
                }
            }) { Text("Create", color = Color(0xFF6A0DAD)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color(0xFF6A0DAD)) }
        }
    )
}