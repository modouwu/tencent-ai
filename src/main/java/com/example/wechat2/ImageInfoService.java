package com.example.wechat2;

import okhttp3.*;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;
import java.io.*;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.*;

@Component
public class ImageInfoService {
    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

    public String base64Encode(String pathName) throws IOException {
        BASE64Encoder encoder = new BASE64Encoder();
        InputStream inputStream=new FileInputStream(pathName);
        byte[] bytes=new byte[inputStream.available()];
        inputStream.read(bytes);
        inputStream.close();
        String image=encoder.encode(bytes).replace("\n","");
        return image;
    }
    public String createSign(Map<String,String> mapParams, String key) throws UnsupportedEncodingException {
        mapParams=sortMapByKey(mapParams);
        mapParams.remove("sign");
        StringBuilder signBuild=new StringBuilder();
        for(Map.Entry<String,String> entry:mapParams.entrySet()){
            String value=entry.getValue();
            if(StringUtil.isNotBlank(value)&&value!=null){
                signBuild.append("&").append(entry.getKey()).append("=").append(URLEncoder.encode( value, "UTF-8" ));
            }
        }
        signBuild.deleteCharAt(0);
        signBuild.append("&app_key=").append(key);
        String stringSignTemp=signBuild.toString();
        return creatMd5("MD5",stringSignTemp).toUpperCase();
    }

    //md5签名
    public String creatMd5(String type, String message) {
        try {
            MessageDigest md = MessageDigest.getInstance(type);
            byte[] bytes = md.digest(message.getBytes("utf-8"));
            return toHex(bytes);
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }
    public String toHex(byte[] bytes) {
        StringBuilder ret = new StringBuilder(bytes.length * 2);

        for(int i = 0; i < bytes.length; ++i) {
            ret.append(HEX_DIGITS[bytes[i] >> 4 & 15]);
            ret.append(HEX_DIGITS[bytes[i] & 15]);
        }
        return ret.toString();
    }
    public Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, String> sortMap = new TreeMap<>();
        new TreeMap<String, String>(
                new MapKeyComparator());
        sortMap.putAll(map);
        return sortMap;
    }

    public String doPostUrl(String url, Map<String,String> bodys)
            throws Exception {
        OkHttpClient okHttpClient  = new OkHttpClient.Builder().build();
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        //post方式提交的数据
        for(Map.Entry<String,String> entry:bodys.entrySet()){
            formBodyBuilder.add(entry.getKey(),entry.getValue());
        }
        RequestBody formBody = formBodyBuilder.build();
        final Request request = new Request.Builder()
                .url(url)//请求的url
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(formBody)
                .build();
        Response response=okHttpClient.newCall(request).execute();
        return response.body().string();
    }

    public String doGetUrl(String url, Map<String,String> bodys)
            throws Exception {
        StringBuilder sb=new StringBuilder(url);
        for(Map.Entry<String,String> entry:bodys.entrySet()){
            sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        sb.deleteCharAt(0);
        url=sb.toString();
        OkHttpClient okHttpClient  = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }
}
