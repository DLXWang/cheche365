// Register Status Listener
statusListener OnConsoleStatusListener


// Settings
def port = System.getProperty 'server.port'

def otherAppenders  = ['console', 'otherDailyRollingFile']
def appAppenders    = ['console', 'appDailyRollingFile']
def statsAppenders  = ['consoleStats', 'statsDailyRollingFile']

def commonAppenderNames = [
    'other',
    'app'
]
def individualAppenderNames = [
    'baoxian',
    'chinalife',
    'cic',
    'cpic',
    'dc',
    'picc',
    'piccuk',
    'pingan',
    'pinganuk',
    'bihu',
    'sinosig',
    'sinosafe',
    'answern',
    'zhongan',
    'botpy',
    'rest',
    'wechat',
    'unionpay',
    'sms',
    'decaptcha',
    'cpicuk',
    'taikang',
    'huanong',
    'aibao'
]

def externalCategories = [
    'org.springframework',
    'org.hibernate',
    'org.apache.http'
]

def patternLayout = '[%X{mobile}][%X{deviceType}][%X{sessionId}][%X{ip}][%X{channelCode}][%X{flowId}][%d{YYYY-MM-dd HH:mm:ss.SSSZ}][%thread][%level][%logger{60}][%X{appMetaInfo}][%msg]%n'
def patternLayoutStats = '[%d{YYYY-MM-dd HH:mm:ss.SSSZ}][%X{statsMetaInfo}]%n'

// To prevent the ID displaying complication error
def forcefullyDisableProductionEnv      = Boolean.valueOf System.getProperty('logback.forcefully.disable.production.env', Boolean.FALSE.toString())

def productionEnv           = forcefullyDisableProductionEnv ? false : ('production' == System.getProperty('spring.profiles.active'))
def identify = { hostname ->
    productionEnv ?
        "/data/nfs0/logs/web/$hostname/$port"               // nfs0
        : (('/data/nfs2' as File).exists() ?
            "/data/nfs2/logs/web/$hostname/$port"           // nfs2
            : 'logs')                                       // local
}
def logFilePathPrefix       = productionEnv ?
                                // production
                                identify(hostname)
                                // non-production
                                : ('build' as File).exists() ?
                                    'build/logs'        // ide
                                    // other profiles
                                    : identify(hostname)

def maxLogArchiveHistoryDays = 60


// Appenders
appender('console', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = patternLayout
    }
}

appender('consoleStats', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = patternLayoutStats
    }
}

appender('statsDailyRollingFile', RollingFileAppender) {
    file = "$logFilePathPrefix/stats/stats.log"
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "$logFilePathPrefix/stats.%d{yyyy-MM-dd}.gz"
        maxHistory = maxLogArchiveHistoryDays
    }
    encoder(PatternLayoutEncoder) {
        pattern = patternLayoutStats
    }
}

(commonAppenderNames + individualAppenderNames).each { name ->
    createAppender name, logFilePathPrefix, maxLogArchiveHistoryDays, patternLayout
}


/**
 * Loggers
 * ROOT：other.log文件，每天归档，在产品环境下不会向console输出
 * 其他：写入app.log文件，每天归档，cheche包下的全用DEBUG级别，其他同上
 */
root productionEnv ? WARN : INFO, productionEnv ? otherAppenders - 'console' : otherAppenders

// internal
logger 'com.cheche365.cheche', DEBUG, productionEnv ? appAppenders - 'console' : appAppenders


individualAppenderNames.each { name ->
    logger "com.cheche365.cheche.$name", DEBUG, ["${name}DailyRollingFile"]
}
logger('org.springframework.data.mongodb', INFO)
logger('org.springframework.integration', ('dev' == System.getProperty('spring.profiles.active')) ? DEBUG : INFO)
logger('statistics', INFO, statsAppenders, false)
//logger 'com.cheche365.cheche.common', productionEnv ? WARN : INFO
logger 'com.cheche365.cheche.web.version', WARN

// external
externalCategories.each { category ->
    logger category, WARN, productionEnv ? appAppenders - 'console' : appAppenders, false
}




private void createAppender(name, logFilePathPrefix, maxLogArchiveHistoryDays, patternLayout) {
    appender("${name}DailyRollingFile", RollingFileAppender) {
        file = "$logFilePathPrefix/${name}.log"
        rollingPolicy(TimeBasedRollingPolicy) {
            fileNamePattern = "$logFilePathPrefix/${name}.%d{yyyy-MM-dd}.gz"
            maxHistory = maxLogArchiveHistoryDays
        }
        encoder(PatternLayoutEncoder) {
            pattern = patternLayout
        }
    }
}
