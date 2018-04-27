package com.example.wechat2;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
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

    @Resource
    ImageInfoService imageInfoService;

    @Autowired
    Environment env;

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

    @Test
    public void testmd5() throws Exception {
        Map<String,String> mapParams=new HashMap<>();
        mapParams.put("app_id","1106789251");
        mapParams.put("time_stamp",System.currentTimeMillis()/1000+"");
        mapParams.put("nonce_str",System.currentTimeMillis()+"");
        mapParams.put("key1","腾讯AI开放平台");
        mapParams.put("key2","示例仅供参考");
        String sign=imageInfoService.createSign(mapParams,"B1Pej0A9qdZnTN7E");
        String md5Str ="BE918C28827E0783D1E5F8E6D7C37A61";
        System.out.println("sign.equals(md5Str)="+sign.equals(md5Str));
        System.out.println("得到md5为"+sign);
    }

    @Test
    public void testReplace(){
        String s = "my.test.txt";
        System.out.println(s.replace(".", "#"));
        System.out.println(s.replaceAll(".", "#"));
        System.out.println(s.replaceAll("\\.", "#"));
        System.out.println(s.replaceFirst(".", "#"));

    }
}
