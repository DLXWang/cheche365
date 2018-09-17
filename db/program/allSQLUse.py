#!/usr/bin/python
# -*- coding: UTF-8 -*-
import os
import common

myConf = common.GetConfParser("allSQLUse.conf")
# read by type
db_host = myConf.get("db", "db_host")
db_port = myConf.getint("db", "db_port")
db_user = myConf.get("db", "db_user")
db_pass = myConf.get("db", "db_pass")
db_name = myConf.get("db", "db_name")
fileName = myConf.get("input", "fileName")
#commonConf = common.GetConfParser("config.conf")

#获取上两级目录
directory = os.path.split(os.path.realpath(__file__))[0]
directory = os.path.split(directory)[0] + os.sep + "ddl" + os.sep

# 导入表结构
cmd = "mysql -u" + db_user + " -p" + db_pass + " " + db_name + " < " + directory +  "gccAllSQL_1.sql"
print cmd
try:
  common.executeCMD(cmd)
except:
  raise

# 导入表数据
cmd = "mysql -u" + db_user + " -p" + db_pass + " " + db_name + " < " + directory +  "gccAllData.sql"
print cmd
try:
  common.executeCMD(cmd)
except:
  raise
#mysql -ucheche -pcheche chechetest < F:\javaWork\source\db\ddl\gccAllSQL_1.sql
#mysql -ucheche -pcheche chechetest < F:\javaWork\source\db\ddl\gccAllData.sql
