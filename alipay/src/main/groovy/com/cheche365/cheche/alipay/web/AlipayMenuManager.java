package com.cheche365.cheche.alipay.web;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayRequest;
import com.alipay.api.AlipayResponse;
import com.alipay.api.request.*;
import com.cheche365.cheche.alipay.web.factory.AlipayAPIClientFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by zhaozhong on 2015/10/19.
 */
@Service
public class AlipayMenuManager {

    public String create(String name) {
        final String menu = readMenuText(name);
        AlipayMobilePublicMenuAddRequest request = new AlipayMobilePublicMenuAddRequest();
        request.setBizContent(menu);
        return execute(request);
    }

    public String create() {
        return create(getMenuFileName());
    }

    public String update(String name) {
        final String menu = readMenuText(name);
        AlipayMobilePublicMenuUpdateRequest request = new AlipayMobilePublicMenuUpdateRequest();
        request.setBizContent(menu);
        return execute(request);
    }

    public String update() {
        return update(getMenuFileName());
    }

    public String delete() {
        AlipayMobilePublicMenuDeleteRequest request = new AlipayMobilePublicMenuDeleteRequest();
        return execute(request);
    }

    public String query() {
        AlipayMobilePublicMenuGetRequest query = new AlipayMobilePublicMenuGetRequest();
        return execute(query);
    }

    private String getMenuFileName() {
        String active = System.getProperty("spring.profiles.active");
        return active;
    }

    private String readMenuText(String name) {
        BufferedReader br = null;
        try {
            File file = new File(AlipayMenuManager.class.getResource("/com/cheche365/cheche/alipay/channels/" + name + "_menu.txt").toURI());
            br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            br.lines().forEach(line -> sb.append(line.trim()));
            return sb.toString();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }finally {
            if(br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private <T extends AlipayResponse> String execute(AlipayRequest<T> request) {
        try {
            AlipayClient alipayClient = AlipayAPIClientFactory.getAlipayClient();
            AlipayResponse response = alipayClient.execute(request);
            if(response.isSuccess()) {
                String body = response.getBody();
                return body;
            }
        }catch(AlipayApiException exception) {
            exception.printStackTrace();
        }
        return null;
    }

}
