#!/usr/bin/python
# -*- coding: UTF-8 -*-
import os, sys
import MySQLdb
import common
import logging
import logging.config

logging.config.fileConfig("logger.conf")
ccLogger = logging.getLogger("example01")

scriptState = {
  'init': 1,
  'finish': 2,
}

def compareVersionNo(a,b):
  aList = a.split('.')
  bList = b.split('.')
  for i in len(aList):
    if(aList[i]!=bList[i]):
      return bList[i] - aList[i]
  return 0

def updatedInFile(versionNo):
  return os.path.exists(dbFilePath + os.sep + 'ddl' + os.sep + versionNo + os.sep + 'UPDATED')


def execSQL(sql, paramList):
  execCount = cur.execute(sql, paramList)
  ccLogger.info("执行sql[%s]执行成功，影响%s条记录", sql, execCount)


def query(sql, paramList):
  execCount = cur.execute(sql, paramList)
  ccLogger.info("查询sql[%s]执行成功,返回%s条", sql, execCount)
  return cur.fetchmany(execCount)


def execVersion(versionNo, dbData):
  # 读取script.txt文件的列表，并逐一和dbData进行比较
  ccLogger.info("----------------------开始处理版本[%s]-------------------------------", versionNo)
  dbList = []
  if versionNo in dbData:
    dbList = dbData[versionNo]

  # 没有script.txt的目录暂不处理
  script_path = dbFilePath + os.sep + 'ddl' + os.sep + versionNo + os.sep + 'script.txt'
  if os.path.exists(script_path):  # 没有script，则不进行处理
    ccLogger.info("读取script文件...")
    line = open(script_path)
    sqlList = line.readlines()
    ccLogger.info("script文件中总共%s个sql文件", len(sqlList))
    for i in sqlList:
      sqlFile = i.strip('\n')
      sqlAllPath = dbFilePath + os.sep + 'ddl' + os.sep + versionNo + os.sep + sqlFile
      if sqlFile in dbList:
        ccLogger.info('sql文件[%s] 已经执行过。', sqlAllPath)
      else:
        if not execSqlFile(sqlAllPath):
          return False
        else:
          sql = "insert into script_history(version, script, create_time) values(%s, %s, now())"
          execSQL(sql, (versionNo, sqlFile))
  else:
    ccLogger.info("没有script.txt文件.")
  # 全部执行成功，且文件已关闭提交。则变更finish标记。
  if (updatedInFile(versionNo)):
    sql = "update script_history t set t.state = %s where t.version = %s"
    execSQL(sql, (scriptState['finish'], versionNo))
  return True


def execSqlFile(sqlPath):
  ccLogger.info("开始执行sql文件[%s]", sqlPath)
  cmd = "mysql -h" + db_host + " -u" + db_user + " -p" + db_pass + " " + db_name + " < " + sqlPath
  return common.executeCMD(cmd)

'''
1.从数据库的finish标记，获取当前开始检查的版本。如2.2.3
2.从2.2.3开始，把数据库数据和文件数据进行比较。如果script.txt已关闭。而且两边数据一致。则修改所有记录的finish标记。
'''

myConf = common.GetConfParser("incrementSQLUse.conf")
# read by type
db_host = myConf.get("db", "db_host")
db_port = myConf.getint("db", "db_port")
db_user = myConf.get("db", "db_user")
db_pass = myConf.get("db", "db_pass")
db_name = myConf.get("db", "db_name")

conn = MySQLdb.connect(
  host=db_host,
  user=db_user,
  passwd=db_pass,
  db=db_name,
  charset='utf8',
)
cur = conn.cursor()
# TODO 根据实际目录计算
dbFilePath = 'F:\javaWork\source\db'
ccLogger.info("-----------------------开始进行增量脚本更新-------------------------------")
ccLogger.info("源文件夹目录为:[%s]", dbFilePath)
ccLogger.info("目标数据库信息:host=[%s],dbName=[%s],user=[%s],password=[%s]", db_host, db_name, db_user, db_pass)

# 根据数据库数据，保存过滤掉的版本号
ccLogger.info("------------------------从数据库获取已执行过的版本列表-----------------------")
finishListFromDB = query("select DISTINCT t.version from script_history t where t.state = %s", (scriptState['finish']))
finishList = []
for i in finishListFromDB:
  finishList.append(i[0])
ccLogger.info(finishList)

# 根据文件夹的数据，保存需要处理的版本号
ccLogger.info("------------------------根据源文件夹目录，获取需要执行的版本列表-----------------------")
versionList = []
for t_versionNo in os.listdir(dbFilePath + os.sep + 'ddl'):
  if os.path.isdir(dbFilePath + os.sep + 'ddl' + os.sep + t_versionNo) and not t_versionNo in finishList:
    #需要根据顺序插入
    # for i in versionList:
    #   根据compareVersionNo()结果决定插入的位置，以确保versionList的顺序。该逻辑未实现。
    versionList.append(t_versionNo)
ccLogger.info(versionList)

#获取待处理版本中的执行过的sql文件。
ccLogger.info("------------------------从数据库获取待执行的版本的SQL文件执行情况，比较用-----------------------")
sql = "select t.version,t.script,t.create_time,t.state from script_history t where 1=2"
for i in versionList:
  sql = sql + " or t.version = '" + i + "'"
versionListFromDB = query(sql, ())
versionMap = {}
for i in versionListFromDB:
  t_versionNo = i[0]
  t_sqlFile = i[1]
  if not t_versionNo in versionMap:
    versionMap[t_versionNo] = []
  versionMap[t_versionNo].append(t_sqlFile)
ccLogger.info(versionMap)

ccLogger.info("------------------------开始执行各个版本内的脚本文件-----------------------")
for i in versionList:
  execVersion(i, versionMap)

cur.close()
conn.commit()
conn.close()


#代码风格，pep或者google。
#1.设置一个参数跳过某些版本;设置一个参数仅执行目标版本;不依赖于文件夹的完成标记. pep8
#2.insert语句加字段,分多个insert语句
#3.使用包结构，com.cheche.devops.db
