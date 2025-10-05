package com.multicloud.downloader.core

import com.multicloud.downloader.parser.*

class ParserFactory private constructor() {
    private val parsers: List<LinkParser> = listOf(
        BaiduParser(),
        AliyunParser(),
        QuarkParser(),
        TianyiParser(),
        XunleiParser()
    )
    
    companion object {
        private var INSTANCE: ParserFactory? = null
        
        fun getInstance(): ParserFactory {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ParserFactory().also { INSTANCE = it }
            }
        }
    }
    
    fun getParser(url: String): LinkParser? {
        return parsers.find { it.canHandle(url) }
    }
}