# WebServices

### XML
- 保存数据和彼此关系的一种格式，把XML格式的数据存储到.xml的文件里，就称为xml文件。
- 用途：
  1. 跨平台传输数据
  2. 配置文件
- 部分符号不能直接使用，比如<, >, ", ', &，如何使用参考[转义字符]。
- 用CDATA忽略部分内容。
```xml
<![CDATA[Hi there!]]>
```
**xml约束**
- 目的：约束xml文档的写法、校验xml。
- 两种技术：XML DTD、XML Schema。
- DTD 可以外部引入，或直接在xml内部使用，现已逐渐淘汰，被XML Schema取代。

外部引入：
```xml
<!DOCTYPE article SYSTEM "sample.dtd">
```

内部引用：
```xml
<!DOCTYPE article [
<!ELEMENT article (channels+)>
<!ELEMENT channels (channel)>
<!ELEMENT channel (name,time,count,icon)>

<!ELEMENT name (#PCDATA)>
<!ELEMENT time  (#PCDATA)>
<!ELEMENT count (#PCDATA)>
<!ELEMENT icon (#PCDATA)>
]>
```
- XML Schema本身也是xml格式，对格式限制更加细致，比如可限制型别、范围等，参考[XML schema 官方文档]。

- 两种解析方式：DOM和SAX，一般查询操作多用SAX。
  1. DOM便于增删改查，一旦加载后可重复使用，但是解析时须全部解析完才能使用，若xml文件大，内存严重消耗。
  2. SAX分成解析器和事件处理两部分，解析器逐行扫描，再经由事件处理合理定义个标签的行为，所以不须等到整个xml文件加载至内存，**只能查询，不能增删改**。


[Tutorial]:<http://www.runoob.com/w3cnote/android-tutorial-http.html>
[转义字符]:<http://www.w3school.com.cn/xml/xml_cdata.asp>
[XML schema 官方文档]:<http://www.w3school.com.cn/schema/index.asp>
