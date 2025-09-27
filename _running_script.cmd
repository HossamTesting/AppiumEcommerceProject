@echo off
echo Start running test

:: To run tests sequentially, use: RunSequentialRegression.xml
:: To run tests parallel,     use: RunParallelRegression.xml

:: To run on cloud "BrowserStack", mvn clean test -DrunOn=browserstack -DsuiteXmlFile=RunSequentialRegression.xml
:: To run on local, mvn clean test -DsuiteXmlFile=RunSequentialRegression.xml


mvn clean test -D suiteXmlFile=RunSequentialRegression.xml

echo end test


