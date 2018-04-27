package com.example.wechat2;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import javax.annotation.Resource;

import java.util.HashMap;
import java.util.Map;

@Controller
public class TcController {

    @Resource
    ImageInfoService imageInfoService;

    @Autowired
    Environment env;

    @RequestMapping("/img")
    @ResponseBody
    public String getImageInfo() throws Exception {
        Map<String,String> mapParams=new HashMap<>();
        mapParams.put("app_id",env.getProperty("app_id"));
        mapParams.put("time_stamp",Long.toString(System.currentTimeMillis()/1000));
        mapParams.put("nonce_str",Long.toString(System.currentTimeMillis()));
        String image=imageInfoService.base64Encode(env.getProperty("pathName"));
        mapParams.put("image", image);
        mapParams.put("type",env.getProperty("type"));
        String sign=imageInfoService.createSign(mapParams,env.getProperty("key"));
        mapParams.put("sign",sign);
        String result=imageInfoService.doPostUrl(env.getProperty("url"),mapParams);
        return result;
    }

    @RequestMapping("/access_token")
    @ResponseBody
    public String getAccessToken() throws Exception {
        Map<String,String> map=new HashMap<>();
        map.put("grant_type",env.getProperty("grant_type"));
        map.put("appid",env.getProperty("appID"));
        map.put("secret",env.getProperty("appsecret"));
        String result=imageInfoService.doGetUrl(env.getProperty("accesstokenurl"),map);
        return result;
    }

    @RequestMapping("/uerinfo")
    @ResponseBody
    public String getUserInfo(@RequestParam(name = "openid") String openid) throws Exception {
        Map<String,String> map=new HashMap<>();
        map.put("access_token","");
        map.put("openid",openid);
        map.put("lang",env.getProperty("lang"));
        String result=imageInfoService.doGetUrl(env.getProperty("accesstokenurl"),map);
        return result;
    }
}
