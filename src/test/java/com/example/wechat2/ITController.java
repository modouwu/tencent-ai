package com.example.wechat2;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Wechat2Application.class)
@Transactional
@Slf4j
public class ITController {

    @Test
    public void testbase64() throws IOException {
        String pathName="/home/yeyunlong/Desktop/aaa";
        BASE64Encoder encoder = new BASE64Encoder();
        InputStream inputStream=new FileInputStream(pathName);
        byte[] bytes=new byte[inputStream.available()];
        inputStream.read(bytes);
        inputStream.close();
        String image=encoder.encode(bytes);
        System.out.println(image);
    }

    /*@Test
    public void appSignBaseAI4FlowersDetect(Integer appId,
                                                      String nonce_str,String image,Integer scene) throws Exception {
        String time_stamp = System.currentTimeMillis()/1000 + "";
        String plain_text = "app_id=" + URLEncoder.encode(appId.toString(),"UTF-8")
                + "&image=" + URLEncoder.encode(image,"UTF-8")
                +"&nonce_str=" + URLEncoder.encode(nonce_str,"UTF-8")
                +"&scene=" + URLEncoder.encode(scene.toString(),"UTF-8")
                + "&time_stamp=" + URLEncoder.encode(time_stamp,"UTF-8");
        String plain_text_encode = plain_text+"&app_key="+TencentAPI.APP_KEY_AI;
        String sign = MD5.getMD5(plain_text_encode);
    }*/

    @Test
    public void testmd5() throws Exception {
        Map<String,String> mapParams=new HashMap<>();
        mapParams.put("app_id","1106789251");
        mapParams.put("time_stamp",System.currentTimeMillis()/1000+"");
        mapParams.put("nonce_str",System.currentTimeMillis()+"");
        mapParams.put("key1","腾讯AI开放平台");
        mapParams.put("key2","示例仅供参考");
        String sign=ImageInfoService.createSign(mapParams,"B1Pej0A9qdZnTN7E");
        String md5Str ="BE918C28827E0783D1E5F8E6D7C37A61";
        System.out.println("sign.equals(md5Str)="+sign.equals(md5Str));
        System.out.println("得到md5为"+sign);
        mapParams.put("sign",sign);
//        String result=ImageInfoService.doPostTencentAI("https://api.ai.qq.com/path/to/api",mapParams);
        String result=ImageInfoService.doPostTencentAI(ImageInfoService.url,mapParams);
        System.out.println("得到result为"+result);
    }
}
