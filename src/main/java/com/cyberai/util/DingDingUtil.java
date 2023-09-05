package com.cyberai.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class DingDingUtil {
    public static String dingdingUrl =PropertyUtils.getProperty("dingdingUrl");


    //通过钉钉机器人发送消息到钉钉群中
    public static void sendMessage(String context)throws Exception{
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpPost httpPost = new HttpPost(dingdingUrl);
        //设置http的请求头，发送json字符串，编码UTF-8
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        //生成json对象传入字符,根据需求创建请求的json字符串
        JSONObject result = new JSONObject();
        JSONObject text = new JSONObject();
        text.put("content", context);
        result.put("msgtype", "text");
        result.put("text", text);
        String jsonString = JSON.toJSONString(result);
        StringEntity entity = new StringEntity(jsonString, "UTF-8");

        //设置http请求的内容
        httpPost.setEntity(entity);
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public static void main(String[] args) throws Exception{
        sendMessage("cyberAI测试完成,报告地址:"+"\n" +
                "https://CyberAI-TestReport.html");
    }
}
