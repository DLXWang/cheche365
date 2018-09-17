// Register Status Listener
statusListener OnConsoleStatusListener


// Settings
def commonAppenders         = ['console', 'commonDailyRollingFile']
def appAppenders            = ['console', 'appDailyRollingFile']
def springAppenders         = ['console', 'springDailyRollingFile']

def patternLayout = '%d{HH:mm:ss.SSS} [%thread] %-5level %logger{30} - %msg %n'

def logFilePathPrefix       = 'build/logs'

def maxLogArchiveHistoryDays = 180



// Appenders
appender('console', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = patternLayout
    }
}

appender('commonDailyRollingFile', RollingFileAppender) {
    file = "$logFilePathPrefix/common.log"
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "$logFilePathPrefix/common.%d{yyyy-MM-dd}.gz"
        maxHistory = maxLogArchiveHistoryDays
    }
    encoder(PatternLayoutEncoder) {
        pattern = patternLayout
    }
}

appender('appDailyRollingFile', RollingFileAppender) {
    file = "$logFilePathPrefix/app.log"
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "$logFilePathPrefix/app.%d{yyyy-MM-dd}.gz"
        maxHistory = maxLogArchiveHistoryDays
    }
    encoder(PatternLayoutEncoder) {
        pattern = patternLayout
    }
}

appender('springDailyRollingFile', RollingFileAppender) {
    file = "$logFilePathPrefix/spring.log"
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "$logFilePathPrefix/spring.%d{yyyy-MM-dd}.gz"
        maxHistory = maxLogArchiveHistoryDays
    }
    encoder(PatternLayoutEncoder) {
        pattern = patternLayout
    }
}




//  Loggers
root(ERROR, commonAppenders)

logger 'com.cheche365.cheche.misc', DEBUG, appAppenders, false
logger 'org.springframework', INFO, springAppenders, false
