package com.majesty.scantest.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.google.android.material.dialog.MaterialAlertDialogBuilder

internal fun Context?.createAlertDialog(
    title: String,
    caption: String? = null,
    positiveButtonText: String,
    negativeButtonText: String,
    positiveListener: (() -> Unit)? = null,
    negativeListener: (() -> Unit)? = null
) {
    this?.let { context ->
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(caption)
            .setPositiveButton(positiveButtonText) {
                _, _ -> positiveListener?.invoke()
            }
            .setNegativeButton(negativeButtonText) {
                _, _ -> negativeListener?.invoke()
            }
            .show()
    }
}

internal fun Context.createAppSettingIntent() = Intent().apply {
    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    data = Uri.fromParts("package", packageName, null)
}