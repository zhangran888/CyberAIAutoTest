package com.cyberai.util;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import org.springframework.util.CollectionUtils;
import org.testng.*;
import org.testng.xml.XmlSuite;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ExtentTestNGIReporterListener implements IReporter {

    private static final String OUTPUT_FOLDER = "test-output/";
    private static final String FILE_NAME =PropertyUtils.getProperty("testAppName")+"-TestReport-"+System.currentTimeMillis()+".html";

    private static final String FULL_FILE_NAME = System.getProperty("user.dir")+"/"+OUTPUT_FOLDER+FILE_NAME;

    private ExtentReports extent;

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        init();

        for (ISuite suite : suites) {
            Map<String,ISuiteResult> result = suite.getResults();

            for (ISuiteResult r : result.values()) {
                ITestContext context = r.getTestContext();

                buildTestNodes(context.getFailedTests(), Status.FAIL);
                buildTestNodes(context.getSkippedTests(), Status.SKIP);
                buildTestNodes(context.getPassedTests(), Status.PASS);

            }
        }


        for (String s : Reporter.getOutput()) {
            extent.setTestRunnerOutput(s);
        }

        extent.flush();

        String msg=PropertyUtils.getProperty("testAppName")+"测试完成,报告地址:"+FILE_NAME + "报告文件" + FULL_FILE_NAME;
        try {
//            DingDingUtil.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(OUTPUT_FOLDER + FILE_NAME);
        htmlReporter.config().setDocumentTitle(PropertyUtils.getProperty("testAppName")+"自动化测试报告");
        htmlReporter.config().setReportName(PropertyUtils.getProperty("testAppName")+"自动化测试报告");
        htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP); //图表位置
        htmlReporter.config().setResourceCDN(ResourceCDN.EXTENTREPORTS);
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        extent.setReportUsesManualConfiguration(true);
    }

    private void buildTestNodes(IResultMap tests, Status status) {
        ExtentTest test;

        if (tests.size() > 0) {
            for (ITestResult result : tests.getAllResults()) {
                test = extent.createTest(result.getMethod().getMethodName()); //显示方法名称
                //test.createNode("子案例");  //创建子案例

                for (String group : result.getMethod().getGroups())
                    test.assignCategory(group); //根据group


                if (result.getThrowable() != null) {
                    test.log(status, result.getThrowable()); //异常案例，显示log到报告
                }
                else {
                    test.log(status, "Test " + status.toString().toLowerCase() + "ed");
                }

                test.getModel().setStartTime(getTime(result.getStartMillis()));
                test.getModel().setEndTime(getTime(result.getEndMillis()));
            }
        }
    }

    private Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }
}