package com.multicloud.downloader.parser;

import org.junit.Test;
import static org.junit.Assert.*;

public class BaiduParserTest {

    @Test
    public void testCanHandle() {
        BaiduParser parser = new BaiduParser();
        assertTrue(parser.canHandle("https://pan.baidu.com/s/1abc"));
        assertFalse(parser.canHandle("https://aliyundrive.com/s/1abc"));
    }

    @Test
    public void testParse() {
        BaiduParser parser = new BaiduParser();
        ParsedResult result = parser.parse("https://pan.baidu.com/s/1abc");
        
        assertNotNull(result);
        assertEquals("example_file.zip", result.getFileName());
        assertEquals(Long.valueOf(1024000L), result.getFileSize());
        assertEquals("https://example.com/direct-download-link", result.getDirectLink());
        assertTrue(result.isRequiresPassword());
        assertEquals("1234", result.getExtractionCode());
    }
}