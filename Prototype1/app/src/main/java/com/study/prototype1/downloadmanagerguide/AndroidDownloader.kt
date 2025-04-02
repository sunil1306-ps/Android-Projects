package com.study.prototype1.downloadmanagerguide

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri

class AndroidDownloader(
    private val context: Context
): Downloader {
    private val downloadManager: DownloadManager = context.getSystemService(DownloadManager::class.java)

    override fun downloadFile(
        name: String,
        url: String
    ): Long {
        val downloadUri = url.toUri()
        val request = DownloadManager.Request(downloadUri)
            .setMimeType("image/jpg")
            .setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI
            )
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle("$name")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "image.jpg")

        return downloadManager.enqueue((request))

    }
}
