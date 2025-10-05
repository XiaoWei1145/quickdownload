package com.multicloud.downloader.parser

import java.util.regex.Pattern

class TianyiParser : LinkParser {
    private val TIANYI_PATTERN = Pattern.compile(".*cloud\\.189\\.cn.*")
    
    override fun canHandle(url: String): Boolean {
        return TIANYI_PATTERN.matcher(url).matches()
    }
    
    override fun parse(url: String): ParsedResult? {
        return try {
            // For demonstration, we'll return a mock result
            ParsedResult(
                fileName = "tianyi_file.zip",
                fileSize = 4096000L,
                directLink = "https://example.com/tianyi_direct_link",
                requiresPassword = true,
                extractionCode = extractShareCode(url)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun extractShareCode(url: String): String? {
        // Extract share code from URL like: https://cloud.189.cn/t/abc123
        val matchResult = Regex("/t/([a-zA-Z0-9]+)").find(url)
        return matchResult?.groupValues?.getOrNull(1)
    }
}