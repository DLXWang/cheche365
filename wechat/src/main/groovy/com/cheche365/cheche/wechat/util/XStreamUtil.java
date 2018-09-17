package com.cheche365.cheche.wechat.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liqiang on 4/7/15.
 */
public class XStreamUtil {

    public static <T> T parse(String xmlString, Class<T> clazz) {
        XStream xs = XStreamFactory.init(true);
        xs.ignoreUnknownElements();
        xs.alias("xml", clazz);
        return (T) xs.fromXML(xmlString);
    }

    public static Map<String, Object> parseToMap(String xmlString) {
        XStream xStream = new XStream(new DomDriver());
        xStream.registerConverter(new MapEntryConverter());
        xStream.alias("xml", Map.class);
        return (Map<String, Object>) xStream.fromXML(xmlString);
    }

    public static class MapEntryConverter implements Converter {

        public boolean canConvert(Class clazz) {
            return AbstractMap.class.isAssignableFrom(clazz);
        }

        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {

            AbstractMap map = (AbstractMap) value;
            for (Object obj : map.entrySet()) {
                Map.Entry entry = (Map.Entry) obj;
                writer.startNode(entry.getKey().toString());
                Object val = entry.getValue();
                if (null != val) {
                    writer.setValue(val.toString());
                }
                writer.endNode();
            }

        }

        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

            Map<String, String> map = new HashMap<>();

            while (reader.hasMoreChildren()) {
                reader.moveDown();

                String key = reader.getNodeName(); // nodeName aka element's name
                String value = reader.getValue();
                map.put(key, value);

                reader.moveUp();
            }

            return map;
        }

    }

}
