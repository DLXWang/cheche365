package com.cheche365.cheche.ordercenter.service.insurance;

import com.cheche365.cheche.ordercenter.OrderCenterBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by yinJianBin on 2017/10/11.
 */
public class InsuranceDataImportServiceTest extends OrderCenterBaseTest {

    private MultipartFile multipartFile;

    @Autowired
    private InsuranceDataImportService insuranceDataImportService;

    @Before
    public void setUp() throws Exception {
        // 读入 文件
        File file = new File("C:/Users/Administrator/Desktop/副本offline_data_import_template.xlsx");
        FileInputStream in_file = new FileInputStream(file);

        // 转 MultipartFile
        String fileName = file.getName();
        multipartFile = new MockMultipartFile(fileName, fileName, null, in_file);

    }

    @Test
    public void commonTest() throws Exception {
        testImportReport();
        Thread.sleep(1000 * 3600 * 5L);
    }


    public void testImportReport() throws Exception {
//        insuranceDataImportService.importReport(multipartFile);
    }

    @Test
    public void testResolveJinLianAnExcel() throws Exception {

    }

    @Test
    public void testResolveTIANDAOExcel() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

}