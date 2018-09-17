/**
 * 微信公众平台开发模式(JAVA) SDK
 * (c) 2012-2013 ____′↘夏悸 <wmails@126.cn>, MIT Licensed
 * http://www.jeasyuicn.com/wechat
 */
package com.cheche365.cheche.wechat.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

import java.io.Writer;
import java.nio.charset.Charset;

public class XStreamFactory {
	protected static String PREFIX_CDATA = "<![CDATA[";
	protected static String SUFFIX_CDATA = "]]>";

	/**
	 * 初始化XStream 可支持某一字段可以加入CDATA标签 如果需要某一字段使用原文
	 * 就需要在String类型的text的头加上"<![CDATA["和结尾处加上"]]>"标签， 以供XStream输出时进行识别
	 * 
	 * @param isAddCDATA
	 *            是否支持CDATA标签
	 * @return
	 */
	public static XStream init(boolean isAddCDATA) {
		XStream xstream = null;
		if (isAddCDATA) {
			xstream = new XStream(new DomDriver("UTF-8") {
				public HierarchicalStreamWriter createWriter(Writer out) {
					return new PrettyPrintWriter(out,new XmlFriendlyNameCoder("_-","_")) {
						protected void writeText(QuickWriter writer, String text) {
							if (!text.startsWith(PREFIX_CDATA)) {
								text = PREFIX_CDATA + text + SUFFIX_CDATA;
							}
							writer.write(new String(text.getBytes(),Charset.forName("UTF-8")));
						}
					};
				};
			});
		} else {
			xstream = new XStream(new DomDriver("UTF-8"){
                public HierarchicalStreamWriter createWriter(Writer out) {
                    return new PrettyPrintWriter(out,new XmlFriendlyNameCoder("_-","_"));
                };
            });
		}
		return xstream;
	}
}
