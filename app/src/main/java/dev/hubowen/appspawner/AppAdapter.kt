package dev.hubowen.appspawner

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("NotifyDataSetChanged")
class AppAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val appsList: MutableList<AppInfo> = mutableListOf()
    private var isLoadingApps = false

    init {
        appsList.add(AppInfo())
        isLoadingApps = true
        notifyDataSetChanged()

        CoroutineScope(Dispatchers.IO).launch {
            val packageManager = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)

            val allApps = packageManager.queryIntentActivities(intent, 0)
            val appInfoList = mutableListOf<AppInfo>()

            for (app in allApps) {
                if (app.activityInfo.packageName == context.packageName) continue

                val appInfo = AppInfo()
                appInfo.label = app.loadLabel(packageManager)
                appInfo.packageName = app.activityInfo.packageName
                appInfo.icon = app.activityInfo.loadIcon(packageManager)
                appInfoList.add(appInfo)
            }

            withContext(Dispatchers.Main) {
                appsList.clear()
                appsList.addAll(appInfoList)

                isLoadingApps = false
                notifyDataSetChanged()
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AppAdapter.AppViewHolder) {
            val appInfo = appsList[position]
            holder.textView.text = appInfo.label.toString()
            holder.img.setImageDrawable(appInfo.icon)
        }
    }

    override fun getItemCount(): Int {
        return appsList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PROGRESS -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.app_progress, parent, false)
                ProgressViewHolder(view)
            }
            VIEW_TYPE_ITEM -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.app_row, parent, false)
                AppViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoadingApps || appsList.firstOrNull() == null) VIEW_TYPE_PROGRESS else VIEW_TYPE_ITEM
    }

    companion object {
        private const val VIEW_TYPE_PROGRESS = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    inner class ProgressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val textView: TextView = itemView.findViewById(R.id.appTextView)
        val img: ImageView = itemView.findViewById(R.id.appImageView)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val pos = layoutPosition
            val context = v?.context

            val launchIntent =
                context?.packageManager?.getLaunchIntentForPackage(appsList[pos].packageName.toString())
            context?.startActivity(launchIntent)
        }
    }
}