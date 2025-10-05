package com.multicloud.downloader.download

data class DownloadTask(
    val id: String,
    val fileName: String,
    val url: String,
    var fileSize: Long = 0L,
    var downloadedSize: Long = 0L,
    var status: Int = 0 // 0: 等待, 1: 下载中, 2: 暂停, 3: 完成, 4: 错误
) {
    val progress: Int
        get() = if (fileSize <= 0) 0 else (downloadedSize * 100 / fileSize).toInt()
}