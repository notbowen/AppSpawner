package dev.hubowen.appspawner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.appRecyclerView)
        val appAdapter = AppAdapter(this)
        recyclerView.adapter = appAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}