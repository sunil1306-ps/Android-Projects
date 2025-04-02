package com.example.downloadphotos.downlaodmanagerguide

interface Downloader {
    fun downlaodFile(url: String): Long
}