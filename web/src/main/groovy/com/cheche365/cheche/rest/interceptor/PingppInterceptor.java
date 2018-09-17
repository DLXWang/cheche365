package com.cheche365.cheche.rest.interceptor;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.externalpayment.constants.PingPlusConstant;
import com.cheche365.cheche.rest.web.TestController;
import com.cheche365.cheche.signature.api.ServletPreSignRequest;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

/**
 * @Author shanxf
 * @Date 2017/12/29  16:43
 * ping++ 数据签名拦截器
 */
public class PingppInterceptor extends HandlerInterceptorAdapter {

    private Logger logger = LoggerFactory.getLogger(PingppInterceptor.class);

    private static final String PING_SIGNATURE = "x-pingplusplus-signature";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String signature = request.getHeader(PING_SIGNATURE);
        ServletPreSignRequest signRequest = new ServletPreSignRequest(request);
        logger.info("ping++ incoming signature:{}",signature);
        if (StringUtils.isBlank(signature)){
            logger.info("ping++ signature not exist");
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR,"ping++ signature not exist");
        }
        String entity =  String.valueOf(signRequest.getEntity());
        logger.info("ping++ incoming body:{}",entity);
        if(StringUtils.isBlank(entity)){
            logger.info("ping++ body invalid");
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR,"ping++ body not exist");
        }
        if(!verifyData(entity,signature,getPubKey())){
            logger.info("ping++ invalid failed");
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR,"ping++ signature failed");
        }
        return super.preHandle(request, response, handler);
    }

    private boolean verifyData(String dataString, String signatureString, PublicKey publicKey)
        throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
        byte[] signatureBytes = Base64.decodeBase64(signatureString);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(dataString.getBytes("UTF-8"));
        return signature.verify(signatureBytes);
    }

    private   PublicKey getPubKey() throws Exception {
        String pubKeyString = getStringFromFile(PingPlusConstant.PEM_FILE_PATH);
        pubKeyString = pubKeyString.replaceAll("(-+BEGIN PUBLIC KEY-+\\r?\\n|-+END PUBLIC KEY-+\\r?\\n?)", "");
        byte[] keyBytes = Base64.decodeBase64(pubKeyString);

        // generate public key
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(spec);
        return publicKey;
    }

    private static String getStringFromFile(String filePath) throws Exception {
        FileInputStream in = new FileInputStream(filePath);
        InputStreamReader inReader = new InputStreamReader(in, "UTF-8");
        BufferedReader bf = new BufferedReader(inReader);
        StringBuilder sb = new StringBuilder();
        String line;
        do {
            line = bf.readLine();
            if (line != null) {
                if (sb.length() != 0) {
                    sb.append("\n");
                }
                sb.append(line);
            }
        } while (line != null);

        return sb.toString();
    }
}
