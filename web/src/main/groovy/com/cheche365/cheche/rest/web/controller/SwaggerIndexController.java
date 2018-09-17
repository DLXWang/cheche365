package com.cheche365.cheche.rest.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhengwei on 3/23/15.
 */
@RestController
public class SwaggerIndexController {

    @Autowired
    ApplicationContext appContext;


    private static final Pattern PATTERN_SWAGGER_DOC_URL = Pattern.compile("url\\s*:\\s*\"([\\w\\d]+://[^\"]+)\"");

    @RequestMapping(value = "/internal/swagger.ui", method = RequestMethod.GET)
    protected void toIndex(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        String html = renderIndexHTML(req, resp);
        if (html != null)
            resp.getWriter().write(html);
    }

    @RequestMapping(value = "/internal/api-docs", method = RequestMethod.GET)
    protected ModelAndView toAPIDoc() {
        return new ModelAndView("redirect:/internal/api-docs/api-docs");
    }

    private String renderIndexHTML(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String requestURL = req.getRequestURL().toString();
        String realURL;

        if (requestURL.endsWith("/swagger.ui")) {
            realURL = "/internal/api-docs";
        } else {
            if (requestURL.endsWith("/"))
                resp.sendRedirect(requestURL + "swagger.ui");
            else {
                resp.sendRedirect(requestURL + "/swagger.ui");
            }
            return null;
        }

        String orgHtml = readHTML();
        Matcher m = PATTERN_SWAGGER_DOC_URL.matcher(orgHtml);
        String defaultURL = null;
        if (m.find()) {
            defaultURL = m.group(1);
        }
        if ((realURL != null) && (defaultURL != null)) {    //replace the hard coded Swagger Host with the incoming request host
            return orgHtml.replace(defaultURL, realURL);
        }
        return orgHtml;
    }


    private  String readHTML() throws IOException {
        InputStream indexStream = null;
        try {

            indexStream  = this.appContext.getResource("/internal/index.html").getInputStream();
            String str = readAsString(indexStream);
            return str;
        }
        finally
        {
            if (indexStream != null)
                indexStream.close();
        }
    }

    private  String readAsString(InputStream inStream) throws IOException {
        if (inStream == null)
            return null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inStream.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            String str = baos.toString();
            return str;
        }
        finally
        {
            if (baos != null)
                baos.close();
        }
    }
}
