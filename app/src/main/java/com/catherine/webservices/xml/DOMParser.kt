package com.catherine.webservices.xml

import com.catherine.webservices.toolkits.CLog

import org.dom4j.Attribute
import org.dom4j.Document
import org.dom4j.Element
import org.dom4j.io.SAXReader
import java.io.InputStream
import java.util.ArrayList

/**
 * Created by Catherine on 2017/7/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

class DOMParser : ParserService {
    private var messageList: MutableList<String>? = null
    private var listener: XMLParserListener? = null
    private var doc: Document? = null

    override fun init(content: InputStream, listener: XMLParserListener) {
        try {
            messageList = ArrayList<String>()
            this.listener = listener
            val reader = SAXReader()
            doc = reader.read(content)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun parser() {
        //根节点
        val root = doc!!.rootElement
        CLog.v(TAG, root.name)

        //第一层
        val elementList = root.elements()
        for (i in elementList.indices) {
            val channel = elementList[i]
            val attr = channel.attribute(0)
            CLog.v(TAG, String.format("%s (%s=%s):", channel.name, attr.name, attr.value))

            //第二层
            val channelList = channel.elements()
            for (j in channelList.indices) {
                val e = channelList[j]
                CLog.v(TAG, String.format("%s:%s", e.name, e.stringValue))
            }
        }
    }

    override fun getValue(tag: String) {
        //根节点
        val root = doc!!.rootElement
        //        CLog.v(TAG, root.getName());

        //第一层
        val elementList = root.elements()
        for (i in elementList.indices) {
            val channel = elementList[i]
            val attr = channel.attribute(0)
            //            CLog.v(TAG, String.format("%s (%s=%s):", channels.getName(), attr.getName(), attr.getValue()));

            //第二层
            val channelList = channel.elements()
            for (j in channelList.indices) {
                val e = channelList[j]
                //                CLog.v(TAG, String.format("%s:%s", e.getName(), e.getStringValue()));
                if (e.name == tag)
                    messageList!!.add(e.stringValue)
            }
        }
        if (!messageList!!.isEmpty()) {
            listener!!.onSuccess(messageList)
            messageList!!.clear()
        }
    }

    fun modify() {
        //根节点
        val root = doc!!.rootElement
        CLog.v(TAG, root.name)

        //第一层
        val elementList = root.elements()
        for (i in elementList.indices) {
            val channel = elementList[i]
            val attr = channel.attribute("id")

            //第二层
            if (attr.value == "3") {
                channel.element("name").text = "Passengers Stranded In Vegas, Stay In Vegas"
                channel.element("time").text = "Tue, 25 Jul 2017 13:42:48 -0400"
                channel.element("count").text = "35879"
                channel.element("icon").text = "https://s.yimg.com/ny/api/res/1.2/pqo8Ie7JhbI2nUW2Hvd36g--/YXBwaWQ9aGlnaGxhbmRlcjtzbT0xO3c9MTI4MDtoPTk2MA--/http://media.zenfs.com/en-US/homerun/ibtimes_176/278138a60651bd54861f8ca28034ba39"
            }
        }
        listener!!.onSuccess(doc)
    }

    fun delete() {
        //根节点
        val root = doc!!.rootElement
        CLog.v(TAG, root.name)

        //第一层
        val elementList = root.elements()
        for (i in elementList.indices) {
            val channel = elementList[i]
            val attr = channel.attribute("id")
            //第二层
            if (attr.value == "8") {
                channel.parent.remove(channel)
            }
        }
        listener!!.onSuccess(doc)
    }

    companion object {
        private val TAG = "DOMParser"
    }
}
