package dev.hubowen.appspawner

import android.graphics.drawable.Drawable

data class AppInfo(
    var label: CharSequence? = null,
    var packageName: String? = null,
    var icon: Drawable? = null
)
