package com.cyberai.util;

import com.google.common.base.Preconditions;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptUtils {

    private static ExpressRunner runner = new ExpressRunner();

    public static Object handle(String script, Map<String,Object> params) {
        List<String> message = new ArrayList<String>();
        try {
            checkPreCompile(script);
            DefaultContext<String, Object> context = new DefaultContext<String, Object>();
            context.putAll(params);
            Object object = runner.execute(script, context, message, true, true);
            System.out.println("object:"+object);
            return object;
        } catch (Exception ex) {
            System.out.println("脚本执行异常:"+ex.getMessage());
            return "failed";
        }

    }

    private static void checkPreCompile(String script) {
        Preconditions.checkArgument(StringUtils.isNotBlank(script), "can not run with empty script");
    }


    public static void main(String args[]) throws JSONException {
        Map<String,Object> params =new HashMap<String,Object>();
        params.put("loginName","a");
        params.put("loginName1","b");
        String script="if(!loginName.equals(loginName1)){\n" +
                "            return \"repeat\";}";
        System.out.println(ScriptUtils.handle(script,params));
//        Map<String,String> map=new HashMap<>();
//        String taskId="1";
//        String taskId1="2";
//        if(!taskId.equals(taskId1)){
//            return "repeat";
//        }

    }

}
