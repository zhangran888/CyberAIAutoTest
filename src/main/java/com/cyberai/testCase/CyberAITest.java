package com.cyberai.testCase;


import com.cyberai.entity.CustomResponse;
import com.cyberai.util.*;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang3.StringUtils;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@ContextConfiguration(locations = {"classpath:config/applicationContext.xml"})
public class CyberAITest {

    private ITestContext testContext;


    @Test(enabled = true, dataProvider = "dataSource", dataProviderClass = CyberDataProvider.class)
    public void testMain(ITestContext iTestContext, String key, JSONObject data) throws Exception {
        testContext = iTestContext;
        this.runCase(iTestContext, key, data);
    }


    private void runCase(ITestContext iTestContext, String key, JSONObject data) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        JSONArray stepDatas = data.getJSONArray("stepDatas");
        String caseName = key;
        String projectNotes = "";
        Map<String, Object> paramsMap = initParam(projectNotes);
        for (int i = 0; i < stepDatas.length(); i++) {
            JSONObject step = stepDatas.getJSONObject(i);
            String actionStr = replaceParam(step.getString("action"), paramsMap);
            JSONObject action = new JSONObject(actionStr);
            String url = "";
            String method = action.getString("method");
            if  (StringUtils.isNotEmpty(action.getString("url")) && action.getString("url").startsWith("http")) {
                url = action.getString("url");
            } else {
                url = paramsMap.get("url") + action.getString("url");
            }
            JSONObject req = action.optJSONObject("request");

            Map<String, String> headers = jsonToMap(action.optJSONObject("headers"));
            String paramStr = action.optString("params");
            parseParam(paramStr, action.getString("request"), paramsMap);
            String expected = replaceParam(step.getString("expected"), paramsMap);
            JSONObject resp;
            if (StringUtils.isBlank(expected)) {
                resp = new JSONObject();
            } else {
                resp = new JSONObject(expected);
            }
            JSONObject expectedResult = resp.optJSONObject("response");
            if (null == expectedResult) {
                expectedResult = new JSONObject();
            }
            String expectedStr = resp.optString("response");
            CloseableHttpResponse Response = null;
            CustomResponse result = new CustomResponse();
            boolean needExecutor = true;
            Integer executedTimes = 0;
            while (needExecutor) {
                if (method.equalsIgnoreCase("post")) {
                    HttpPost httpPost = new HttpPost(url);
                    // 设置报文和通讯格式
                    StringEntity stringEntity = new StringEntity(req.toString(), "UTF-8");
                    stringEntity.setContentEncoding("utf-8");
                    stringEntity.setContentType("application/json");
                    httpPost.setEntity(stringEntity);
                    if(!headers.isEmpty()){
                        for (Map.Entry<String, String> header : headers.entrySet()) {
                            httpPost.setHeader(header.getKey(),header.getValue());
                        }
                    }

                    log(caseName, "开始调用接口:" + url);
                    log(caseName, "接口期望返回:" + expectedStr);
                    Response = httpClient.execute(httpPost);
                    String resStr = EntityUtils.toString(Response.getEntity(), "utf-8");
                    result.setStatus(Response.getStatusLine().getStatusCode());
                    result.setBody(resStr);
                    log(caseName, "接口实际返回:" + resStr);
                    if(StringUtils.isNotBlank(expectedStr)) {
                        JSONAssert.assertEquals(expectedStr, resStr, false);
                    }
                } else {
                    HttpGet httpGet = new HttpGet(url);
                    if(!headers.isEmpty()){
                        for (Map.Entry<String, String> header : headers.entrySet()) {
                            httpGet.setHeader(header.getKey(),header.getValue());
                        }
                    }
                    log(caseName, "开始调用接口:" + url);
                    log(caseName, "接口期望返回:" + expectedStr);
                    Response = httpClient.execute(httpGet);
                    String resStr = EntityUtils.toString(Response.getEntity(), "utf-8");
                    result.setStatus(Response.getStatusLine().getStatusCode());
                    result.setBody(resStr);
                    Reporter.log("接口实际返回:" + resStr);
                    if(StringUtils.isNotBlank(expectedStr)) {
                        JSONAssert.assertEquals(expectedStr, resStr, false);
                    }
                }
                if (result.getStatus().equals(200)) {
                    paramStr = resp.optString("params");
                    parseParam(paramStr, result.getBody(), paramsMap);
                }
                String script = resp.optString("script");
                System.out.println(script);
                if (StringUtils.isNotBlank(script)) {
                    log(caseName, "开始执行脚本:" + script);
                    log(caseName, "参数信息:" + paramsMap);
                    Object scriptResult = ScriptUtils.handle(script, paramsMap);
                    log(caseName, "脚本执行结果:" + scriptResult);
                    if (scriptResult instanceof Map) {
                        paramsMap.putAll((Map<String, Object>) scriptResult);
                        needExecutor = false;
                    } else {
                        if (scriptResult==null) {
                            needExecutor = false;
                        } else if (scriptResult.toString().equalsIgnoreCase("repeat")) {
                            needExecutor = true;
                        } else if (scriptResult.toString().equalsIgnoreCase("failed")) {
                            Assert.assertEquals("脚本执行failed", "脚本执行success");
                        } else {
                            needExecutor = false;
                        }
                    }
                } else {
                    needExecutor = false;
                }
                executedTimes++;
                if (executedTimes > 2) {
                    needExecutor = false;
                    Assert.assertEquals("脚本执行failed", "重试次数超过最大值");
                }
                if(needExecutor){
                    Thread.sleep(3000);
                }
            }
        }
    }

    private static String replaceParam(String str, Map<String, Object> paramsMap) {
        Pattern paramPattern = Pattern.compile("(?<=\\$\\{).*?(?=\\})");
        Matcher m = paramPattern.matcher(str);
        String pValue = "";
        while (m.find()) {
            String[] p = m.group().split(",");
            String key = p[0].trim();
            if (key.equals("随机数")) {
                if (p.length == 1) {
                    pValue = getRandom(Integer.valueOf(5));
                } else {
                    pValue = getRandom(Integer.valueOf(p[1]));
                }
            } else if (key.equals("当前时间")) {
                Date now = new Date();
                if (p.length == 1) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                    pValue = format.format(now);
                } else {
                    SimpleDateFormat format = new SimpleDateFormat(p[1]);
                    pValue = format.format(now);
                }
            } else if (key.equals("当前时间戳")) {
                pValue = System.currentTimeMillis() + "";
            } else if (key.equalsIgnoreCase("uuid")) {
                pValue = UUID.randomUUID().toString().replaceAll("-", "");
            } else {
                String[] pKeys = m.group().split("\\.");
                if (pKeys.length > 1) {
                    String pKey1 = pKeys[pKeys.length - 1].trim();
                    String Pkey = m.group().substring(0, m.group().length() - pKey1.length() - 1).trim();
                    List valueList = (List) paramsMap.get(Pkey);
                    if (null == valueList) {
                        System.out.println("替换参数异常,参数${" + m.group() + "}的值为空");
                    }
                    if (pKey1.equalsIgnoreCase("size") || pKey1.equalsIgnoreCase("length")) {
                        pValue = valueList.size() + "";
                    } else if (pKey1.equalsIgnoreCase("strip")) {
                        pValue = StringUtils.strip(valueList.toString(), "[]");
                    } else {
                        pValue = valueList.get(Integer.valueOf(pKey1)) + "";
                    }
                } else {
                    Object v = paramsMap.get(m.group().trim());
                    if (null == v) {
                        System.out.println("替换参数异常,参数${" + m.group() + "}的值为空");
                    }
                    pValue = v.toString();
                }
            }
            System.out.println("开始替换参数：" + m.group());
            str = str.replaceAll("\\$\\{" + m.group() + "\\}", pValue);
        }
        //去除格式化后json字符串中的空格，否则转成json时会报错
        str=str.replaceAll(" ","");
        return str;
    }

    public static String getRandom(int length) {
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            val += String.valueOf(random.nextInt(10));
        }
        return val;
    }

    private void parseParam(String paramStr,String json,Map<String,Object> paramsMap) throws JSONException {
        if(paramStr!=null&&!paramStr.equals("")) {
            String[] paramList = paramStr.split(";");
            for(int i=0;i<paramList.length;i++){
                String[] param=paramList[i].split(":");
                if(param.length==2) {
                    paramsMap.put(param[0], JsonPath.read(json,xpath2jsonpath(param[1])).toString());
                }

            }
        }
    }

    private Map<String,Object>  initParam(String projectNotrs) throws JSONException {
        Map<String,Object> paramsMap=new HashMap<String,Object>();
        Properties properties= PropertyUtils.loadProperties("case.properties");
        if(properties!=null){
            Set<String> set = properties.stringPropertyNames();
            set.forEach(key -> {
                paramsMap.put(key,properties.getProperty(key));
            });
        }
        long time=System.currentTimeMillis();
        paramsMap.put("当前时间戳",time+"");
        if(StringUtils.isNotEmpty(projectNotrs)){
            Pattern paramPattern=Pattern.compile("(?<=<p>).*?(?=</p>)");
            Matcher m=paramPattern.matcher(projectNotrs);
            while(m.find()){
                String str=m.group().replaceAll("<.*?>","");
                String[] p= str.split("=");
                if(p.length==2){
                    paramsMap.put(p[0],p[1]);
                }else if(p.length>2){
                    String value="";
                    for(int i=1;i<p.length;i++){
                        value=value+"="+p[i];
                    }
                    value=value.substring(1);
                    paramsMap.put(p[0],value);
                }else{
                    continue;
                }
            }
        }
        return paramsMap;
    }

    /**
     * xpath转换为jsonpath TODO
     * 普通字段名
     */
    private static String xpath2jsonpath(String path) {
        if (!path.startsWith("$")) {
            String p = path.replaceAll("/", ".");
            return p.startsWith(".") ? "$" + p : "$." + p;
        }
        return path;
    }


    private static Map<String,String> jsonToMap(JSONObject json) throws JSONException {
        Map<String,String> map=new HashMap<String, String>();
        if(null==json){
            return map;
        }
        Iterator iterator = json.keys();
        while(iterator.hasNext()) {
            String key = (String)iterator.next();
            String value=json.getString(key);
            map.put(key,value);

        }
        return map;
    }



    public  void log(String caseName,String msg) {
        System.out.println(msg);

        if(testContext.getAttribute("caseLog")==null){
            Map<String,List<String>>caseLog=new HashMap<>();
            List<String> logList=new ArrayList<>();
            caseLog.put(caseName,logList);
            testContext.setAttribute("caseLog",caseLog);
        }else{
            List<String> logList=((Map<String,List<String>>)testContext.getAttribute("caseLog")).get(caseName);
            if(logList==null){
                logList=new ArrayList<>();
                ((Map<String,List<String>>)testContext.getAttribute("caseLog")).put(caseName,logList);
            }
        }
        ((Map<String,List<String>>)testContext.getAttribute("caseLog")).get(caseName).add(msg);
    }

    public static void main(String args[]) {
        Date now=new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        System.out.println(format.format(now));

    }



}

