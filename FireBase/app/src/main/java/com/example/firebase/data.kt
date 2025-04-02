package com.example.firebase

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.UploadTask

data class data (
    var downloadUri: UploadTask.TaskSnapshot? = null,
    var downloadurl: Task<Uri>? = null
)

object uriData {
    var UriData = data()
}