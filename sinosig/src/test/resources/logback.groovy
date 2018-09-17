// Register Status Listener
statusListener OnConsoleStatusListener


// Settings
def appAppenders            = ['appDailyRollingFile']
def commonAppenders         = ['console', 'commonDailyRollingFile']
def sinosigAppenders        = ['console', 'sinosigDailyRollingFile']

def patternLayout = '%d{HH:mm:ss.SSS} [%thread] %-5level %logger{60} - %msg %n'

// To prevent the ID displaying complication error
def forcefullyDisableProductionEnv      = Boolean.valueOf System.getProperty('logback.forcefully.disable.production.env', Boolean.FALSE.toString())

def productionEnv           = forcefullyDisableProductionEnv ? false : ('production' == System.getProperty('spring.profiles.active'))
def logFilePathPrefix       = productionEnv ? 'logs'                            // production
                                : ('build' as File).exists() ? 'build/logs'     // ide
                                : 'logs'                                        // gretty built product

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
        fileNamePattern = "$logFilePathPrefix/rest.%d{yyyy-MM-dd}.gz"
        maxHistory = maxLogArchiveHistoryDays
    }
    encoder(PatternLayoutEncoder) {
        pattern = patternLayout
    }
}

appender('sinosigDailyRollingFile', RollingFileAppender) {
    file = "$logFilePathPrefix/sinosig.log"
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "$logFilePathPrefix/sinosig.%d{yyyy-MM-dd}.gz"
        maxHistory = maxLogArchiveHistoryDays
    }
    encoder(PatternLayoutEncoder) {
        pattern = patternLayout
    }
}



//  Loggers
root(productionEnv ? WARN : INFO, commonAppenders)

logger('com.cheche365.cheche.sinosig', productionEnv ? INFO : DEBUG, sinosigAppenders, false)
logger('com.cheche365.cheche.parser', DEBUG, appAppenders, false)
logger('com.cheche365.cheche.common.flow', DEBUG, appAppenders, false)
logger('com.cheche365.cheche.test.parser', DEBUG, appAppenders, false)
logger('org.springframework', productionEnv ? WARN : WARN, commonAppenders, false)
logger('org.hibernate', productionEnv ? WARN : WARN, commonAppenders, false)
