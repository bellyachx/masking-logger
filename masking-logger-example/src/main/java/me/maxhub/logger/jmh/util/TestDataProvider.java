package me.maxhub.logger.jmh.util;

import me.maxhub.logger.TestData;

import java.util.List;

public class TestDataProvider {

    public static TestData createTestData() {
        var testData = TestData.buildTestData();
        var testData1 = TestData.buildTestData();
        var testData2 = TestData.buildTestData();
        testData1.setTestDataList(List.of(testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2));
        testData.setTestDataList(List.of(testData1, testData1));
        return testData;
    }
}
