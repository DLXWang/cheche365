package com.cheche365.cheche.manage.common.util;

import com.cheche365.cheche.manage.common.web.model.ResultModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by xu.yelong on 2016/12/29.
 */
public class ResponseOutUtil {
    private static Logger logger = LoggerFactory.getLogger(ResponseOutUtil.class);

    public static ResultModel excelExport(Workbook workbook, HttpServletResponse response, String fileName) {
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        try {
            response.setHeader("Content-Disposition",
                "attachment; filename=" + new String(fileName.getBytes(), "iso-8859-1"));
            out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();
            return null;
        } catch (IOException e) {
            return new ResultModel(false, e.getMessage());
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException ex) {
                logger.error("export result, close OutputStream has error", ex);
            }
        }
    }

    public static void outPrint(HttpServletResponse response, String ajaxString) throws IOException {
        PrintWriter out = null;
        try {
            response.setContentType("text/html; charset=utf-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("X-Frame-Options", "SAMEORIGIN");
            out = response.getWriter();
            out.write(ajaxString);
            out.flush();
        } catch (Exception ex) {
            throw new RuntimeException("write return string has error", ex);
        } finally {
            if (out != null)
                out.close();
        }
    }
}
