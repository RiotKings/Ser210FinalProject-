package com.example.ser210_final_client.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ser210_final_client.R
import com.example.ser210_final_client.model.Repo

class RepoAdapter(private val repos: MutableList<Repo>) :
    RecyclerView.Adapter<RepoAdapter.RepoViewHolder>() {

    inner class RepoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val repoName: TextView = itemView.findViewById(R.id.tv_repo_name)
        val repoInfo: TextView = itemView.findViewById(R.id.tv_repo_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_repo, parent, false)
        return RepoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val repo = repos[position]
        holder.repoName.text = repo.name
        holder.repoInfo.text = repo.description

        // Tap the card to open GitHub
        holder.itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repo.githubUrl))
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = repos.size

    fun addRepo(repo: Repo) {
        repos.add(repo)
        notifyItemInserted(repos.size - 1)
    }
}