package com.study.prototype1.downloadmanagerguide

interface Downloader {
    fun downloadFile (
        name: String,
        url: String
    ): Long
}