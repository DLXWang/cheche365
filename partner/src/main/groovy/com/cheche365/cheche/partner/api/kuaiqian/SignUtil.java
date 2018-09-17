package com.cheche365.cheche.partner.api.kuaiqian;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cheche365.cheche.partner.config.app.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;



/**
 * @Description 加验签工具函数
 * 
 */
public class SignUtil {
	
	private static Logger logger = LoggerFactory.getLogger(SignUtil.class);
	/**
	 * str空判断
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isnull(String str) {
		if (null == str || str.equalsIgnoreCase("null") || str.equals("")) {
			return true;
		} else
			return false;
	}

	/**
	 * 生成待签名串
	 * 
	 * @param paramMap
	 * @return
	 */
	public static String genSignData(JSONObject jsonObject) {
		StringBuffer content = new StringBuffer();

		// 按照key做首字母升序排列
		List<String> keys = new ArrayList<String>(jsonObject.keySet());
		Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);
		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			if ("sign".equals(key)) {
				continue;
			}
			
			String value = jsonObject.getString(key);
	
			// 空串不参与签名
			if (isnull(value)) {
				continue;
			}
			
			Object o = jsonObject.get(key);
			if(o instanceof  List){
				List list = (List)o;
				StringBuilder sb = new StringBuilder("[");
				for (int loop = 0; loop < list.size(); loop++) {
					String tmpStr = JSON.toJSONString(list.get(0));
					JSONObject tmpJsonObj = JSON.parseObject(tmpStr);
					sb.append("{");
					sb.append(genSignData(tmpJsonObj));
					sb.append("}");
				}
				sb.append("]");
				value = sb.toString();
			}
			
			if(o instanceof  JSONObject){
		 		JSONObject tmp = (JSONObject)o;
				StringBuilder sb = new StringBuilder("[");
				String tmpStr = JSON.toJSONString(tmp);
				JSONObject tmpJsonObj = JSON.parseObject(tmpStr);
				sb.append("{");
				sb.append(genSignData(tmpJsonObj));
				sb.append("}");
				sb.append("]");
				value = sb.toString();
			}
			
			content.append((i == 0 ? "" : "&") + key + "=" + value);
		}
		
		String signSrc = content.toString();
		if (signSrc.startsWith("&")) {
			signSrc = signSrc.replaceFirst("&", "");
		}
		return signSrc;
	}

	/**
	 * 加签
	 * 
	 * @param reqObj
	 * @param rsa_private
	 * @param md5_key
	 * @return
	 */
	public static String addSign(JSONObject reqObj , String md5_key) {
		if (reqObj == null) {
			return "";
		}
		return addSignMD5(reqObj, md5_key);
	}

	/**
	 * 签名验证
	 * 
	 * @param reqStr
	 * @return
	 */
	public static boolean checkSign(String reqStr, String md5_key) {
		JSONObject reqObj = JSON.parseObject(reqStr);
		if (reqObj == null) {
			return false;
		}
		return checkSignMD5(reqObj, md5_key);
	}


	/**
	 * MD5签名验证
	 * 
	 * @param signSrc
	 * @param sign
	 * @return
	 */
	private static boolean checkSignMD5(JSONObject reqObj, String md5_key) {
		if (reqObj == null) {
			return false;
		}
		String sign = reqObj.getString("sign");
		// 生成待签名串
		String sign_src = genSignData(reqObj);
		sign_src += "&key=" + md5_key;

		try {
			if (sign.equals(Md5Algorithm.getInstance().md5Digest(sign_src.getBytes("utf-8")))) {
				logger.info("MD5签名验证通过");
				return true;
			} else {
				logger.info("MD5签名验证未通过");
				return false;
			}
		} catch (UnsupportedEncodingException e) {
			logger.info("MD5签名验证异常" + e.getMessage());
			return false;
		}
	}

	/**
	 * MD5加签名
	 * 
	 * @param reqObj
	 * @param md5_key
	 * @return
	 */
	private static String addSignMD5(JSONObject reqObj, String md5_key) {
		if (reqObj == null) {
			return "";
		}
		// 生成待签名串
		String sign_src = genSignData(reqObj);
		sign_src += "&key=" + md5_key;
        logger.info("待签名的串：{}=",sign_src);
		try {
			return Md5Algorithm.getInstance().md5Digest(sign_src.getBytes("utf-8"));
		} catch (Exception e) {
			logger.info("MD5加签名异常" + e.getMessage());
			return "";
		}
	}

}

