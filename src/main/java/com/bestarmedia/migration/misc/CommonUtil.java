package com.bestarmedia.migration.misc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

    public static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String createUUID() {
        return UUID.randomUUID().toString();
    }


    public static String deleteSpaceAndUpperFirst(String name) {
        if (StringUtils.isEmpty(name)) {
            return "";
        }
        String casebrief = deleteStartEndSpaceChar(name).toLowerCase();
        String[] strs = casebrief.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String strTmp : strs) {
            String text = deleteStartEndSpaceChar(strTmp);
            if (!StringUtils.isEmpty(text)) {
                if (text.contains(".") || text.contains("·") || isContainChinese(text)) {
                    sb.append(text.toUpperCase()).append(" ");
                } else {
                    char[] ch = text.toCharArray();
                    if (ch[0] >= 'a' && ch[0] <= 'z') {
                        ch[0] = (char) (ch[0] - 32);
                    }
                    String strT = new String(ch);
                    sb.append(strT).append(" ");
                }
            }
        }
        return sb.toString().trim();
    }


    public static String deleteStartEndSpaceChar(String textContent) {
        if (StringUtils.isEmpty(textContent)) {
            return "";
        }
        textContent = textContent.trim();
        while (textContent.startsWith("　")) {
            textContent = textContent.substring(1).trim();
        }
        while (textContent.endsWith("　")) {
            textContent = textContent.substring(0, textContent.length() - 1).trim();
        }
        return textContent;
    }


    /**
     * 字符串是否包含中文
     *
     * @param str 待校验字符串
     * @return true 包含中文字符 false 不包含中文字符
     */
    public static boolean isContainChinese(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        Pattern p = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static boolean isAllChinese(String name) {
        char[] cTemp = name.toCharArray();
        for (int i = 0; i < name.length(); i++) {
            char ch = cTemp[i];
            if (!Character.isWhitespace(ch) && !isChinese(ch)) {//不比对空格符
                return false;
            }
        }
        return true;
    }

    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }


    public static String deleteParenthesesEnd(String songName) {
        if (songName.endsWith(")") && songName.contains("(")) {//处理括号结尾
            String name = songName.substring(0, songName.lastIndexOf("("));
            if (!StringUtils.isEmpty(name)) {
                songName = name;
            }
        } else if (songName.endsWith("）") && songName.contains("（")) {//处理括号结尾
            String name = songName.substring(0, songName.lastIndexOf("（"));
            if (!StringUtils.isEmpty(name)) {
                songName = name;
            }
        }
        return songName;
    }


    public static String deleteSpecialChar(String text) {
        return text.replace("\"", "").replace("“", "").replace("”", "")
                .replace("(", "").replace(")", "").replace("（", "").replace("）", "")
                .replace("/", "|").replace("｜", "|").replace("，", "|").replace("&", "|");
    }
}
