package com.catherine.webservices.xml;


import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Catherine on 2017/7/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class SAXParser implements ParserService {
    private final static String TAG = "SAXParser";
    private javax.xml.parsers.SAXParser sp;
    private XMLParserListener listener;
    private InputStream content;
    private String tag;

    @Override
    public void init(InputStream content, XMLParserListener listener) {
        this.listener = listener;
        this.content = content;
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            sp = spf.newSAXParser();
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFail();
        }
    }

    @Override
    public void parser() {
        try {
            sp.parse(new InputSource(new InputStreamReader(content)), new DefaultHandler());
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFail();
        }
    }

    @Override
    public void getValue(String tag) {
        try {
            this.tag = tag;
            DefaultHandler handler = new MyDefaultHandler();
            sp.parse(new InputSource(new InputStreamReader(content)), handler);
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFail();
        }
    }

    private class MyDefaultHandler extends DefaultHandler {
        private String currTag = "";
        private String message = "";
        private List<String> messageList;


        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            messageList = new ArrayList<>();
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            if (!messageList.isEmpty())
                listener.onSuccess(messageList);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            currTag = qName;
            super.startElement(uri, localName, qName, attributes);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (currTag.equals(tag)) {
                messageList.add(message);
            }
            message = "";
            super.endElement(uri, localName, qName);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            String value = new String(ch, start, length);
            value = value.trim();
            if (value.length() != 0)
                message = String.format(Locale.ENGLISH, "%s%s", message, value);
        }

        @Override
        public void processingInstruction(String target, String data) throws SAXException {
            super.processingInstruction(target, data);
            //XML的处理指令，比如<?xml-stylesheet type="text/css" href="sample.css"?>
        }

        @Override
        public void warning(SAXParseException e) throws SAXException {
            super.warning(e);
            listener.onFail();
        }

        @Override
        public void error(SAXParseException e) throws SAXException {
            super.error(e);
            listener.onFail();
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            super.fatalError(e);
            listener.onFail();
        }
    }
}
