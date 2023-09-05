package com.cyberai.util;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class TestNgListener extends TestListenerAdapter {

    public static WebDriver driver;


    @Override
    public void onTestFailure(ITestResult tr) {
        System.out.println("on test failure");
        try {
            Thread.sleep(300);
//            String picPath=takeScreenShot(tr);
//            tr.setAttribute("picPath",picPath);
//        } catch (IOException e) {
//            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onTestFailure(tr);

    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        System.out.println("on test success");
        super.onTestSuccess(tr);
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        System.out.println("on test skipped");
        super.onTestSkipped(tr);

    }

    @Override
    public void onFinish(ITestContext var1){
        System.out.println("on test finish");
        super.onFinish(var1);

    }


}