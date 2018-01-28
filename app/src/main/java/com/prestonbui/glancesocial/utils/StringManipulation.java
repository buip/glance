package com.prestonbui.glancesocial.utils;

/**
 * Created by phuongbui on 10/22/17.
 */

public class StringManipulation {

    public static String getTags(String string) {
        if (string.indexOf("#") > 0) {
            StringBuilder sb = new StringBuilder();
            char[] charArray = string.toCharArray();
            boolean foundWord = false;
            for (char c : charArray) {
                if (c == '#') {
                    foundWord = true;
                    sb.append(c);
                } else {
                    if (foundWord) {
                        sb.append(c);
                    }
                }
                if (c == ' ') {
                    foundWord = false;
                }
            }
            String s = sb.toString().replace(" ", "").replace("#", ",#");
            return s.substring(1, s.length());
        }
        return string;
    }
}
