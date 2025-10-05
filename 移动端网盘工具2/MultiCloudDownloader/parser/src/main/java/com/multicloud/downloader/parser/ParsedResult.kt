package com.multicloud.downloader.parser

data class ParsedResult(
    val fileName: String,
    val fileSize: Long?,
    val directLink: String,
    val requiresPassword: Boolean = false,
    val extractionCode: String? = null
)