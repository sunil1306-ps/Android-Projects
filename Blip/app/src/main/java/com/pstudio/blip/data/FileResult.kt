package com.pstudio.blip.data

import android.net.Uri
import java.io.File

data class CopiedFile(
    val file: File,
    val uri: Uri
)