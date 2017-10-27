package com.yjing.openglnote;

import android.content.Context;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangjinyuan on 2015/10/13.
 */
public class Utils {


    /**
     * 弹出软键盘
     */
    public static boolean showSoftInput(Context context, EditText editText) {
        try {
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            return inputManager.showSoftInput(editText, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断list是否为空
     */
    public static boolean isListEmpty(List<?> list) {
        return null == list || 0 >= list.size();
    }

    private static String sImei2;



    private static String getDeviceSerial() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
//            LogUtils.d("jialiwei-hj", "getDeviceSerial: ");(TAG, "", e);
        }

        return serial;
    }


    public static byte[] getBytes(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return str.getBytes();
        }
    }


    public static String md5Appkey(String str) {
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(getBytes(str));
            byte[] arrayOfByte = localMessageDigest.digest();
            StringBuffer localStringBuffer = new StringBuffer(64);
            for (int i = 0; i < arrayOfByte.length; i++) {
                int j = 0xFF & arrayOfByte[i];
                if (j < 16)
                    localStringBuffer.append("0");
                localStringBuffer.append(Integer.toHexString(j));
            }
            return localStringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }



    /**
     * 关闭输入流
     *
     * @param is
     */
    public static void closeInStream(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 关闭输出流
     *
     * @param os
     */
    public static void closeOutStream(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
            }
        }
    }

    public static void flushOutStream(OutputStream os) {
        if (os != null) {
            try {
                os.flush();
            } catch (IOException e) {
            }
        }
    }

    public static void closeReader(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }
    }


    public static String format(String jsonStr) {
        int level = 0;
        StringBuffer jsonForMatStr = new StringBuffer();
        for (int i = 0; i < jsonStr.length(); i++) {
            char c = jsonStr.charAt(i);
            if (level > 0 && '\n' == jsonForMatStr.charAt(jsonForMatStr.length() - 1)) {
                jsonForMatStr.append(getLevelStr(level));
            }
            switch (c) {
                case '{':
                case '[':
                    jsonForMatStr.append(c + "\n");
                    level++;
                    break;
                case ',':
                    jsonForMatStr.append(c + "\n");
                    break;
                case '}':
                case ']':
                    jsonForMatStr.append("\n");
                    level--;
                    jsonForMatStr.append(getLevelStr(level));
                    jsonForMatStr.append(c);
                    break;
                default:
                    jsonForMatStr.append(c);
                    break;
            }
        }

        return jsonForMatStr.toString();

    }

    private static String getLevelStr(int level) {
        StringBuffer levelStr = new StringBuffer();
        for (int levelI = 0; levelI < level; levelI++) {
            levelStr.append("\t");
        }
        return levelStr.toString();
    }
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
    public static List<Map.Entry<String, Integer>> sortMapDescByValue(Map<String,Integer> map){
        List<Map.Entry<String, Integer>> infoIds =
                new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue() - o1.getValue());
            }
        });
        return infoIds;
    }

    /**
     * 获取url的host，代替{@link java.net.URL#getHost()}；
     * 解决使用java.net.URL进行url解析并判断url是否为指定域名时产生漏洞的问题；
     * <p/>
     * 1.java.net.URL对url里存在回车符和换行符被认为是合法的：
     * <%
     * String goUrl=”http://618119.com/\r\nX-Location: http://www.lizongbo.com/”;
     * //goUrl=java.net.URLEncoder.encode(goUrl, “UTF-8″);
     * response.sendRedirect(goUrl);
     * %>
     * 例如上面的代码即使使用java.net.URL进行解析，也能正常解析，而被认为是个合法的url。
     * 加上reponse.setheader的时候没做参数检查，导致写入了非法的head，这样会导致XSS注入攻击。
     * <p/>
     * 2.”http://618119.com#www.lizongbo.com/”
     * 这样的url被java.net.URL解析得到的host是618119.com#www.lizongbo.com，因此按域名后缀判断的话会被误放过，
     * 在浏览器地址栏里实际请求会变成：http://618119.com/#www.lizongbo.com/
     * 这样也会产生非法跳转漏洞。
     * 使用java.net.URI进行解析则不会出现这样的问题。
     * <p/>
     * 一个完整的域名，由根域、顶级域、二级、三级……域名构成，每级域名之间用点分开，每级域名由字母、数字和减号构成（第一个字母不能是减号），不区分大小写，长度不超过63；
     *
     * @param url
     * @return
     */
    public static String getUrlHost(String url) {
        if (!TextUtils.isEmpty(url)) {
            Matcher matcher = Pattern.compile("[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+").matcher(url);
            if (matcher.find()) {
                return matcher.group(0);
            }
        }
        return null;
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param context
     * @param spValue
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


}
