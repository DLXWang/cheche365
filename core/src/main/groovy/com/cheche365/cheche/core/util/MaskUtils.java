package com.cheche365.cheche.core.util;

/**
 * 
 * @author healy
 * 
 */
public class MaskUtils {


	/**
	 * 隐藏卡号信息
	 * 
	 * @param cardNo
	 * @return
	 */
	private static String maskCardNo(String cardNo) {
		if (cardNo == null || cardNo.trim().length() <= 8) {
			return cardNo;
		}
		cardNo = cardNo.trim();
		int length = cardNo.length();
		String lastFourNo = cardNo.substring(length - 4);
		StringBuffer mask = new StringBuffer("");
		for (int i = 0; i < length - 4; i++) {
			mask.append("*");
		}
        return mask.toString() + lastFourNo;
	}


	/**
	 * 隐藏银行卡号码
	 * 
	 * @param bankCardNo
	 * @return
	 */
	public static String maskBankCardNo(String bankCardNo) {
		return maskCardNo(bankCardNo);
	}
}
