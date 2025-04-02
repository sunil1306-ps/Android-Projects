package com.example.docintent

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

fun intcall(context: Context, path: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(path, "application/pdf")
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }
    val intentChooser = Intent.createChooser(intent, "Choose app...")
    try {
        context.startActivity(intentChooser)
    }catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No app found", Toast.LENGTH_SHORT).show()
    }
}

fun dialPhoneNumber(context: Context, phoneNumber: String, packageManager: PackageManager) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber))
    if (intent.resolveActivity(packageManager) != null) {
        context.startActivity(intent)
    }
}