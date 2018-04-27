package com.example.wechat2;

import org.apache.http.HttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Controller
public class TcController {


    @RequestMapping("/img")
    @ResponseBody
    public String getImageInfo() throws Exception {
        String pathName="/home/yeyunlong/Downloads/aaa.jpg";
        int type=0;//识别类型，0-行驶证识别，1-驾驶证识别
        Map<String,String> mapParams=new HashMap<>();
        mapParams.put("app_id",String.valueOf(ImageInfoService.app_id));
        mapParams.put("time_stamp",Long.toString(System.currentTimeMillis()/1000));
        mapParams.put("nonce_str",Long.toString(System.currentTimeMillis()));
        BASE64Encoder encoder = new BASE64Encoder();
        InputStream inputStream=new FileInputStream(pathName);
        byte[] bytes=new byte[inputStream.available()];
        inputStream.read(bytes);
        inputStream.close();
        String image=encoder.encode(bytes).replace("\n","");
//        image =  URLEncoder.encode(image, "UTF-8");
        mapParams.put("image", image);
        mapParams.put("type",type+"");
        String sign=ImageInfoService.createSign(mapParams,ImageInfoService.key);
        System.out.println("MD5:"+sign);
        //String sign2=ImageInfoService.getSignature(mapParams);
        mapParams.put("sign",sign);
        //mapParams.remove("app_key");
        //String result=ImageInfoService.postInfo(ImageInfoService.url,mapParams);
        String result=ImageInfoService.doPostTencentAI(ImageInfoService.url,mapParams);
        //String result=HttpUtil.doPostSSL(ImageInfoService.url,mapParams);
        //String result=HttpUtil.sendPost(ImageInfoService.url,mapParams,null);
        return result;
    }
}
