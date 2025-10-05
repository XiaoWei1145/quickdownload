package com.multicloud.downloader.parser

interface LinkParser {
    fun canHandle(url: String): Boolean
    fun parse(url: String): ParsedResult?
}