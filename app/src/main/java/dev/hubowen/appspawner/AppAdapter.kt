package dev.hubowen.appspawner

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppAdapter(context: Context) : RecyclerView.Adapter<AppAdapter.ViewHolder>() {
    private val appsList: MutableList<AppInfo> = mutableListOf();

    init {
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val allApps = packageManager.queryIntentActivities(intent, 0)
        for (app in allApps) {
            if (app.activityInfo.packageName == context.packageName) continue

            val appInfo = AppInfo()
            appInfo.label = app.loadLabel(packageManager)
            appInfo.packageName = app.activityInfo.packageName
            appInfo.icon = app.activityInfo.loadIcon(packageManager)
            appsList.add(appInfo)
        }
    }

    override fun onBindViewHolder(holder: AppAdapter.ViewHolder, position: Int) {
        val appInfo = appsList[position]
        holder.textView.text = appInfo.label.toString()
        holder.img.setImageDrawable(appInfo.icon)
    }

    override fun getItemCount(): Int {
        return appsList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.app_row, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val textView: TextView = itemView.findViewById(R.id.appTextView)
        val img: ImageView = itemView.findViewById(R.id.appImageView)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val pos = layoutPosition
            val context = v?.context

            val launchIntent = context?.packageManager?.getLaunchIntentForPackage(appsList[pos].packageName.toString())
            context?.startActivity(launchIntent)
        }
    }
}