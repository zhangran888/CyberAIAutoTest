package com.cyberai.util;

import com.aventstack.extentreports.ExtentTest;

public class MyReporter {
    public static ExtentTest report;
    private static String testName;

    public static String getTestName() {
        return testName;
    }

    public static void setTestName(String testName) {
        MyReporter.testName = testName;
    }

    public static ExtentTest getReport() {
        return report;
    }

    public static void setReport(ExtentTest report) {
        MyReporter.report = report;
    }
}
