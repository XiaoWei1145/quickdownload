package com.multicloud.downloader.core;

import com.multicloud.downloader.parser.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class ParserFactoryTest {

    @Test
    public void testGetBaiduParser() {
        ParserFactory factory = ParserFactory.getInstance();
        LinkParser parser = factory.getParser("https://pan.baidu.com/s/1abc");
        assertNotNull(parser);
        assertTrue(parser instanceof BaiduParser);
    }

    @Test
    public void testGetAliyunParser() {
        ParserFactory factory = ParserFactory.getInstance();
        LinkParser parser = factory.getParser("https://aliyundrive.com/s/1abc");
        assertNotNull(parser);
        assertTrue(parser instanceof AliyunParser);
    }

    @Test
    public void testGetQuarkParser() {
        ParserFactory factory = ParserFactory.getInstance();
        LinkParser parser = factory.getParser("https://quark.cn/s/1abc");
        assertNotNull(parser);
        assertTrue(parser instanceof QuarkParser);
    }

    @Test
    public void testGetTianyiParser() {
        ParserFactory factory = ParserFactory.getInstance();
        LinkParser parser = factory.getParser("https://cloud.189.cn/s/1abc");
        assertNotNull(parser);
        assertTrue(parser instanceof TianyiParser);
    }

    @Test
    public void testGetXunleiParser() {
        ParserFactory factory = ParserFactory.getInstance();
        LinkParser parser = factory.getParser("https://share.weiyun.com/s/1abc");
        assertNotNull(parser);
        assertTrue(parser instanceof XunleiParser);
    }

    @Test
    public void testGetNullParser() {
        ParserFactory factory = ParserFactory.getInstance();
        LinkParser parser = factory.getParser("https://unknown.com/s/1abc");
        assertNull(parser);
    }
}