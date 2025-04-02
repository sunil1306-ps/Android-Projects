package com.example.scrollview.downlaodmanagerguide


interface Downloader {
    fun downloadFile(
        name: String,
        url: String
    ): Long
}