package com.example.downloadphotos.downlaodmanagerguide

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri

class AndroidDownloader(
    private val context: Context
): Downloader {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    override fun downlaodFile(url: String): Long {
        val downloadUri = url.toUri()
        val request = DownloadManager.Request(downloadUri)
            .setMimeType("*/*")
            .setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI
            )
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle("document.txt")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "document.txt")

        return downloadManager.enqueue((request))


//        val downloadUri = Uri.parse(url)
//        val request = DownloadManager.Request(
//            downloadUri
//        )
//        request.setAllowedNetworkTypes(
//            DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
//        )
//            .setAllowedOverRoaming(true).setTitle("Some name")
//            .setDescription("Downloading file")
//            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//            .setDestinationInExternalPublicDir(
//                Environment.DIRECTORY_DOWNLOADS, "hello.txt"
//            )
//
//
//        Toast.makeText(
//            context,
//            "Download successfully to ${downloadUri?.path}",
//            Toast.LENGTH_LONG
//        ).show()
//
//        return downloadManager.enqueue(request)

    }

}