package com.cheche365.cheche.externalapi.writer

import com.cheche365.cheche.common.util.XmlUtils

import javax.ws.rs.Produces
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.ext.MessageBodyWriter
import java.lang.annotation.Annotation
import java.lang.reflect.Type

import static com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider.writeToAsString

/**
 * Created by zhengwei on 09/02/2018.
 */
@Produces("application/xml")
class XMLBodyWriter implements MessageBodyWriter {

    @Override
    boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return MediaType.APPLICATION_XML_TYPE == mediaType && Map.isAssignableFrom(type)
    }

    @Override
    long getSize(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1
    }

    @Override
    void writeTo(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        writeToAsString(XmlUtils.mapToXml(o), entityStream, mediaType)
    }

}
