package com.example.scrollview.downlaodmanagerguide

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DownloadCompletedReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            if (it.action == "android.intent.action.DOWNLOAD_COMPLETE") {
                val id = it.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                if (id != -1L) {
                    println("Download with $id finished!")
                }
            }
        }
    }

}