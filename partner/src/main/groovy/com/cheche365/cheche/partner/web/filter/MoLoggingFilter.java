package com.cheche365.cheche.partner.web.filter;

/**
 * Created by zhengwei on 4/21/16.
 */

import com.cheche365.cheche.core.model.LogType;
import com.cheche365.cheche.core.model.MoHttpClientLog;
import com.cheche365.cheche.core.mongodb.repository.MoHttpClientLogRepository;
import com.sun.jersey.api.client.filter.ClientFilter;

import javax.ws.rs.core.MultivaluedMap;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import com.sun.jersey.api.client.AbstractClientRequestAdapter;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientRequestAdapter;
import com.sun.jersey.api.client.ClientResponse;

/**
 * 从{@link com.sun.jersey.api.client.filter.LoggingFilter} copy过来，做一些定制化的功能，比如log每一行都以订单号开头，方便搜索。
 *
 * @author zhengwei
 */
public class MoLoggingFilter extends ClientFilter {

    private static final Logger LOGGER = Logger.getLogger(MoLoggingFilter.class.getName());

    private static final String NOTIFICATION_PREFIX = "* ";

    private String requestPrefix = "> ";

    private String responsePrefix = "< ";

    private Map additionalParam = null;

    private final class Adapter extends AbstractClientRequestAdapter {
        private final StringBuilder b;

        Adapter(ClientRequestAdapter cra, StringBuilder b) {
            super(cra);
            this.b = b;
        }

        public OutputStream adapt(ClientRequest request, OutputStream out) throws IOException {
            return new LoggingOutputStream(getAdapter().adapt(request, out), b);
        }

    }

    public void setAdditionalParam(Map additionalParam) {
        this.requestPrefix = String.valueOf(additionalParam.get("prefix"));
        this.responsePrefix = String.valueOf(additionalParam.get("prefix"));
        this.additionalParam = additionalParam;
    }

    private final class LoggingOutputStream extends OutputStream {
        private final OutputStream out;

        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        private final StringBuilder b;

        LoggingOutputStream(OutputStream out, StringBuilder b) {
            this.out = out;
            this.b = b;
        }

        @Override
        public void write(byte[] b)  throws IOException {
            baos.write(b);
            out.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len)  throws IOException {
            baos.write(b, off, len);
            out.write(b, off, len);
        }

        @Override
        public void write(int b) throws IOException {
            baos.write(b);
            out.write(b);
        }

        @Override
        public void close() throws IOException {
            printEntity(b, baos.toByteArray());
            log(b);
            out.close();
        }
    }

    private final PrintStream loggingStream;
    private final Logger logger;
    private final int maxEntitySize;
    private final MoHttpClientLogRepository logRepository;

    private long _id = 0;

    public MoLoggingFilter(MoHttpClientLogRepository logRepository) {
        this(logRepository, LOGGER);
    }

    public MoLoggingFilter(MoHttpClientLogRepository logRepository, Logger logger) {
        this(logRepository, logger, null, 10 * 1024);
    }

    private MoLoggingFilter(MoHttpClientLogRepository logRepository, Logger logger, PrintStream loggingStream, int maxEntitySize) {
        this.loggingStream = loggingStream;
        this.logger = logger;
        this.maxEntitySize = maxEntitySize;
        this.logRepository = logRepository;
    }

    private void log(StringBuilder b) {
        if (logger != null) {
            logger.info(b.toString());

            if(logRepository!=null){
                logRepository.save(this.createHttpClientLog(b));
            }
        } else {
            loggingStream.print(b);
        }
    }

    private MoHttpClientLog createHttpClientLog(StringBuilder b){
        MoHttpClientLog httpClientLog = new MoHttpClientLog();
        httpClientLog.setLogType((LogType) additionalParam.get("logType"));
        httpClientLog.setLogMessage(b.toString());
        httpClientLog.setObjTable(String.valueOf(additionalParam.get("objTable")));
        httpClientLog.setObjId(String.valueOf(additionalParam.get("objId")));
        httpClientLog.setCreateTime(new Date());
        return httpClientLog;
    }

    private StringBuilder prefixId(StringBuilder b, long id) {
        b.append(Long.toString(id)).append(" ");
        return b;
    }

    @Override
    public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
        long id = ++this._id;

        logRequest(id, request);

        ClientResponse response = getNext().handle(request);

        logResponse(id, response);

        return response;
    }

    private void logRequest(long id, ClientRequest request) {
        StringBuilder b = new StringBuilder();

        printRequestLine(b, id, request);
        printRequestHeaders(b, id, request.getHeaders());

        if (request.getEntity() != null) {
            request.setAdapter(new Adapter(request.getAdapter(), b));
        } else {
            log(b);
        }
    }

    private void printRequestLine(StringBuilder b, long id, ClientRequest request) {
        prefixId(b, id).append(NOTIFICATION_PREFIX).append("Client out-bound request").append("\n");
        prefixId(b, id).append(requestPrefix).append(request.getMethod()).append(" ").
            append(request.getURI().toASCIIString()).append("\n");
    }

    private void printRequestHeaders(StringBuilder b, long id, MultivaluedMap<String, Object> headers) {
        for (Map.Entry<String, List<Object>> e : headers.entrySet()) {
            List<Object> val = e.getValue();
            String header = e.getKey();

            if(val.size() == 1) {
                prefixId(b, id).append(requestPrefix).append(header).append(": ").append(ClientRequest.getHeaderValue(val.get(0))).append("\n");
            } else {
                StringBuilder sb = new StringBuilder();
                boolean add = false;
                for(Object o : val) {
                    if(add) sb.append(',');
                    add = true;
                    sb.append(ClientRequest.getHeaderValue(o));
                }
                prefixId(b, id).append(requestPrefix).append(header).append(": ").append(sb.toString()).append("\n");
            }
        }
    }

    private void logResponse(long id, ClientResponse response) {
        StringBuilder b = new StringBuilder();

        printResponseLine(b, id, response);
        printResponseHeaders(b, id, response.getHeaders());

        InputStream stream = response.getEntityInputStream();
        try {
            if (!response.getEntityInputStream().markSupported()) {
                stream = new BufferedInputStream(stream);
                response.setEntityInputStream(stream);
            }

            stream.mark(maxEntitySize + 1);
            byte[] entity = new byte[maxEntitySize + 1];
            int entitySize = stream.read(entity);

            if (entitySize > 0) {
                b.append(new String(entity, 0, Math.min(entitySize, maxEntitySize)));
                if (entitySize > maxEntitySize) {
                    b.append("...more...");
                }
                b.append('\n');
                stream.reset();
            }
        } catch (IOException ex) {
            throw new ClientHandlerException(ex);
        }
        log(b);
    }

    private void printResponseLine(StringBuilder b, long id, ClientResponse response) {
        prefixId(b, id).append(NOTIFICATION_PREFIX).
            append("Client in-bound response").append("\n");
        prefixId(b, id).append(responsePrefix).
            append(Integer.toString(response.getStatus())).
            append("\n");
    }

    private void printResponseHeaders(StringBuilder b, long id, MultivaluedMap<String, String> headers) {
        for (Map.Entry<String, List<String>> e : headers.entrySet()) {
            String header = e.getKey();
            for (String value : e.getValue()) {
                prefixId(b, id).append(responsePrefix).append(header).append(": ").
                    append(value).append("\n");
            }
        }
        prefixId(b, id).append(responsePrefix).append("\n");
    }

    private void printEntity(StringBuilder b, byte[] entity) throws IOException {
        if (entity.length == 0)
            return;
        b.append(new String(entity)).append("\n");
    }
}
