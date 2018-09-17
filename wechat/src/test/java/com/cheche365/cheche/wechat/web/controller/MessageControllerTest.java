package com.cheche365.cheche.wechat.web.controller;


import com.cheche365.cheche.wechat.web.controller.com.cheche365.cheche.wechat.TestAppConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Random;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by liqiang on 3/21/15.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration

@ContextConfiguration(classes={TestAppConfig.class})
public class MessageControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void testEchoMessage() throws Exception {
        String signature = "65c91e5b497c632cc33c34de65a44d706818b14a";
        String echostr = "1231196430446571518";
        String timestamp = "1426927358";
        String nonce="1278036036";

        mockMvc.perform(get("/web/wechat").param("signature",signature)
                .param("timestamp",timestamp)
                .param("nonce", nonce)
                .param("echostr",echostr)).andExpect(content().string(echostr));
    }


}
