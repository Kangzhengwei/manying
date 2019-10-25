package com.kzw.manying.Util;

/**
 * author: kang4
 * Date: 2019/9/23
 * Description:
 */
public class StringUtil {
    public static String subString(String str, int index) {
        int a = str.indexOf("$");
        if (index == 0) {
            return str.substring(0, a);
        } else {
            return str.substring(a + 1);
        }
    }
}
