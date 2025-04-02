package com.study.prototype1.downloadmanagerguide

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DownloadCompletedReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        intent.let {
            if (it != null) {
                if (it.action == "android.intent.action.DOWNLOAD_COMPLETE") {
                    val id = it.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                    if (id != -1L) {
                        println("Download with $id finished!")
                    }
//                    val downloadManager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//                    val downloadUri = downloadManager.getUriForDownloadedFile(id)
//
//                    if (downloadUri != null) {
//                        // Create an Intent to open the downloaded file.
//                        val intent1 = Intent(Intent.ACTION_VIEW).apply {
//                            setDataAndType(downloadUri, "image/jpg")
//                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        }
//
//                        try {
//                            context.startActivity(intent1)
//                        }catch (e: ActivityNotFoundException) {
//                            println(e.message)
//                        }
//                    }

                }
            }
        }
    }
//    private fun getMimeType(uri: String): String? {
//        val extension = MimeTypeMap.getFileExtensionFromUrl(uri)
//        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
//    }
}

