package com.cyberai.util;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class CyberDataProvider {
    public CyberDataProvider() {
    }



    public static Object[][]  createData() throws Exception {
        Enumeration enums = ClassLoader.getSystemResources("");
        List<String> caseNameList=Arrays.asList(PropertyUtils.getProperty("debugCaseName").split(";"));
        Map<String, JSONObject> datas = buildCase(((URL)enums.nextElement()).getPath() + "data/",caseNameList);
        int row = datas.size();
        Object[][] data1 = new Object[row][2];
        Object[] keys = datas.keySet().toArray();
        for(int i = 0; i < row; ++i) {
            data1[i][0] = keys[i];
            data1[i][1] = datas.get(keys[i]);
        }
        return data1;
    }

    public static Map<String, JSONObject> buildCase(String filename,List<String> caseNameList) throws IOException, BiffException, JSONException {
        Set<String> filePaths=getAllFiles(filename);
        Map<String, JSONObject> caseInfos= new TreeMap();

        for (String filePath:filePaths){
            JSONObject data = new JSONObject();
            File caseFile=new File(filePath);
            String caseName=caseFile.getName().split("\\.")[0];
            if(!caseFile.getName().endsWith("xls")){
                continue;
            }
            if(caseNameList!=null&&!caseNameList.contains(caseName)){
                continue;
            }
            data.put("caseName",caseName);
            FileInputStream fileInputStream = new FileInputStream(caseFile);
            Workbook workbook = Workbook.getWorkbook(fileInputStream);
            Sheet readfirst = workbook.getSheet(0);
            int rows = readfirst.getRows();
            JSONArray stepDatas = new JSONArray();
            for(int i =1;i<rows;i++) {
                JSONObject stepData = new JSONObject();
                stepData.put("stepId",i-1);
                Cell cell = readfirst.getCell(0, i);
                System.out.println("action:"+cell.getContents());
                stepData.put("action",cell.getContents());

                cell = readfirst.getCell(1, i);
                System.out.println("expected:"+cell.getContents());
                stepData.put("expected",cell.getContents());
                stepDatas.put(stepData);
            }
            data.put("stepDatas",stepDatas);
            caseInfos.put(caseName,data);

        }
        return caseInfos;
    }


    public static Set<String> getAllFiles(String filePath) throws IOException {
        Set<String> filePaths=new HashSet<>();
        File file = new File(filePath);
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for (File file1 : files) {
                if(file1.isDirectory()){
                    filePaths.addAll(getAllFiles(file1.getAbsolutePath()));
                }else{
                    filePaths.add(file1.getAbsolutePath());
                }
            }
        }else{
            filePaths.add(file.getAbsolutePath());
        }
        return filePaths;
    }


    @DataProvider(name = "dataSource")
    public static Object[][] dataSource(Method method, ITestContext iTestContext) throws Exception {
        if(PropertyUtils.getBooleanProperty("debug")){
            return createData();
        }
        return createData();
    }


}
