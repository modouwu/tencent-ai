package com.example.wechat2;

import net.minidev.json.JSONObject;
import okhttp3.*;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.util.StringUtil;
import org.apache.http.client.HttpClient;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

public class ImageInfoService {
    public static final MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
    public static int app_id=1106789251;
    public static String key="B1Pej0A9qdZnTN7E";
    public static String url="https://api.ai.qq.com/fcgi-bin/ocr/ocr_driverlicenseocr";
    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
    public static String postInfo(String url,Map<String,String> jsonStr) throws IOException {
        OkHttpClient okHttpClient=new OkHttpClient();
        RequestBody requestBody=RequestBody.create(mediaType,mapToStr(jsonStr));
        System.err.println("拼接APPKEY的参数："+mapToStr(jsonStr));
        Request request=new Request.Builder().url(url).post(requestBody).build();
        Response response=okHttpClient.newCall(request).execute();
        return response.body().string();
    }
    public static String createSign(Map<String,String> mapParams, String key) throws UnsupportedEncodingException {
        mapParams=sortMapByKey(mapParams);
        mapParams.remove("sign");
        StringBuilder signBuild=new StringBuilder();
        for(Map.Entry<String,String> entry:mapParams.entrySet()){
            String value=entry.getValue();
            if(StringUtil.isNotBlank(value)&&value!=null){
                signBuild.append("&").append(entry.getKey()).append("=").append(URLEncoder.encode( value, "UTF-8" ));
//                signBuild.append("&").append(entry.getKey()).append("=").append(value);
            }
        }
        signBuild.deleteCharAt(0);
        signBuild.append("&app_key=").append(key);
        String stringSignTemp=signBuild.toString();
        return creatMd5("MD5",stringSignTemp).toUpperCase();
    }

    //md5签名
    public static String creatMd5(String type, String message) {
        try {
            MessageDigest md = MessageDigest.getInstance(type);
            byte[] bytes = md.digest(message.getBytes("utf-8"));
            return toHex(bytes);
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }
    public static String toHex(byte[] bytes) {
        StringBuilder ret = new StringBuilder(bytes.length * 2);

        for(int i = 0; i < bytes.length; ++i) {
            ret.append(HEX_DIGITS[bytes[i] >> 4 & 15]);
            ret.append(HEX_DIGITS[bytes[i] & 15]);
        }
        return ret.toString();
    }
    public static Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, String> sortMap = new TreeMap<>();
        new TreeMap<String, String>(
                new MapKeyComparator());
        sortMap.putAll(map);
        return sortMap;
    }

    public static String getSignature(Map<String,String> params) throws IOException {
        // 先将参数以其参数名的字典序升序进行排序
        Map<String, String> sortedParams = new TreeMap<>(params);
        Set<Map.Entry<String, String>> entrys = sortedParams.entrySet();
        // 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
        StringBuilder baseString = new StringBuilder();
        for (Map.Entry<String, String> param : entrys) {
            //sign参数 和 空值参数 不加入算法
            if(param.getValue()!=null && !"".equals(param.getKey().trim()) && !"sign".equals(param.getKey().trim()) && !"".equals(param.getValue().trim())) {
                baseString.append(param.getKey().trim()).append("=").append(URLEncoder.encode(param.getValue().trim(),"UTF-8")).append("&");
            }
        }
        System.err.println("未拼接APPKEY的参数："+baseString.toString());
        if(baseString.length() > 0 ) {
            baseString.deleteCharAt(baseString.length()-1).append("&app_key="+key);
        }
        System.err.println("拼接APPKEY后的参数："+baseString.toString());
        // 使用MD5对待签名串求签
        try {
            String sign = creatMd5("MD5",baseString.toString()).toUpperCase();
            return sign;
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    public static String mapToStr(Map<String,String> mapParams){
        StringBuilder signBuild=new StringBuilder();
        for(Map.Entry<String,String> entry:mapParams.entrySet()){
            String value=entry.getValue();
            if(StringUtil.isNotBlank(value)&&value!=null){
                signBuild.append("&").append(entry.getKey()).append("=").append(value);
            }
        }
        signBuild.deleteCharAt(0);
        //signBuild.append("&app_key=").append(key);
        return signBuild.toString();
    }

    public static String doPostTencentAI(String url, Map<String,String> bodys)
            throws Exception {
        OkHttpClient okHttpClient  = new OkHttpClient.Builder().build();

        //post方式提交的数据
        FormBody formBody = new FormBody.Builder()
                .add("app_id", bodys.get("app_id"))
                .add("time_stamp", bodys.get("time_stamp"))
                .add("nonce_str", bodys.get("nonce_str"))
                .add("image", bodys.get("image"))
                .add("type", "0")
                .add("sign", bodys.get("sign")).build();
        /*FormBody formBody = new FormBody.Builder()
                .add("app_id", "10000")
                .add("time_stamp", "1493449657")
                .add("nonce_str", "20e3408a79")
                .add("key1","腾讯AI开放平台")
                .add("key2","示例仅供参考")
                .add("sign", "BE918C28827E0783D1E5F8E6D7C37A61").build();*/
        final Request request = new Request.Builder()
                .url(url)//请求的url
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(formBody)
                .build();
        Response response=okHttpClient.newCall(request).execute();



        /*OkHttpClient okHttpClient=new OkHttpClient();
        RequestBody body = new FormBody.Builder().add("app_id", bodys.get("app_id"))
                .add("time_stamp", bodys.get("time_stamp"))
                .add("nonce_str", bodys.get("nonce_str"))
                .add("sign", bodys.get("sign"))
                .add("image", bodys.get("image"))
                .add("type", "0").build();

        Request request=new Request.Builder().url(url).header("Content-type", "application/x-www-form-urlencoded").post(body).build();
        Response response=okHttpClient.newCall(request).execute();*/
        return response.body().string();

/*

        HttpClient httpClient = createSSLInsecureClient();
        HttpPost post = new HttpPost(url);
        // 构造消息头
        post.setHeader("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
        // 构建消息实体
        if (bodys != null) {
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            for (String key : bodys.keySet()) {
                nameValuePairList.add(new BasicNameValuePair(key, bodys.get(key)));
            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairList);
            post.setEntity(formEntity);
        }
        StringEntity entity = new StringEntity(mapToStr(bodys), "UTF-8");
        entity.setContentEncoding("UTF-8");
//        entity.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
        post.setEntity(entity);
        HttpResponse response = httpClient.execute(post);
        return EntityUtils.toString(response.getEntity());*/
    }



    public static CloseableHttpClient createSSLInsecureClient() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 默认信任所有证书
                public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    return true;
                }
            }).build();
            // AllowAllHostnameVerifier: 这种方式不对主机名进行验证，验证功能被关闭，是个空操作(域名验证)
            SSLConnectionSocketFactory sslcsf = new SSLConnectionSocketFactory(sslContext,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return HttpClients.custom().setSSLSocketFactory(sslcsf).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        //如果创建失败，就创建一个默认的Http的连接
        return HttpClients.createDefault();
    }
}
