package com.example.scrollview.downlaodmanagerguide

import android.app.DownloadManager
import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.core.net.toUri

class AndroidDownloader(
    private val context: Context
): Downloader {

    @RequiresApi(Build.VERSION_CODES.M)
    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun downloadFile(
        name: String,
        url: String
    ): Long {
        val downloadUri = url.toUri()
        val request = DownloadManager.Request(downloadUri)
            .setMimeType("*/*")
            .setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI
            )
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle("$name")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "image.jpg")

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