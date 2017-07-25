package com.catherine.webservices.xml;

import com.catherine.webservices.toolkits.CLog;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Catherine on 2017/7/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class DOMParser implements ParserService {
    private final static String TAG = "DOMParser";
    private List<String> messageList;
    private XMLParserListener listener;
    private Document doc;

    @Override
    public void init(InputStream content, XMLParserListener listener) {
        try {
            messageList = new ArrayList<>();
            this.listener = listener;
            SAXReader reader = new SAXReader();
            doc = reader.read(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void parser() {
        //根节点
        Element root = doc.getRootElement();
        CLog.v(TAG, root.getName());

        //第一层
        List<Element> elementList = root.elements();
        for (int i = 0; i < elementList.size(); i++) {
            Element channels = elementList.get(i);
            Attribute attr = channels.attribute(0);
            CLog.v(TAG, String.format("%s (%s=%s):", channels.getName(), attr.getName(), attr.getValue()));

            //第二层
            List<Element> channelList = channels.elements();
            for (int j = 0; j < channelList.size(); j++) {
                Element e = channelList.get(j);
                CLog.v(TAG, String.format("%s:%s", e.getName(), e.getStringValue()));
            }
        }
    }

    @Override
    public void getValue(String tag) {
        //根节点
        Element root = doc.getRootElement();
//        CLog.v(TAG, root.getName());

        //第一层
        List<Element> elementList = root.elements();
        for (int i = 0; i < elementList.size(); i++) {
            Element channels = elementList.get(i);
            Attribute attr = channels.attribute(0);
//            CLog.v(TAG, String.format("%s (%s=%s):", channels.getName(), attr.getName(), attr.getValue()));

            //第二层
            List<Element> channelList = channels.elements();
            for (int j = 0; j < channelList.size(); j++) {
                Element e = channelList.get(j);
//                CLog.v(TAG, String.format("%s:%s", e.getName(), e.getStringValue()));
                if (e.getName().equals(tag))
                    messageList.add(e.getStringValue());
            }
        }
        if (!messageList.isEmpty()) {
            listener.onSuccess(messageList);
            messageList.clear();
        }
    }

    public void modify(String parentAttrName, String parentAttrValue, String tag, String value) {
        //根节点
        Element root = doc.getRootElement();
//        CLog.v(TAG, root.getName());

        //第一层
        List<Element> elementList = root.elements();
        for (int i = 0; i < elementList.size(); i++) {
            Element channels = elementList.get(i);
            Attribute attr = channels.attribute(parentAttrName);
//            CLog.v(TAG, String.format("%s (%s=%s):", channels.getName(), attr.getName(), attr.getValue()));

            if (parentAttrValue.equals(attr.getValue())) {
                //第二层
                List<Element> channelList = channels.elements();
                for (int j = 0; j < channelList.size(); j++) {
                    Element e = channelList.get(j);
//                CLog.v(TAG, String.format("%s:%s", e.getName(), e.getStringValue()));
                    if (e.getName().equals(tag))
                        e.setText(value);
                }
            }
        }
        parser();
    }
}
