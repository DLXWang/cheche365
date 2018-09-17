#!/usr/bin/python
# -*- coding: UTF-8 -*-
import os
import common

myConf = common.GetConfParser("allSQLCreate.conf")
# read by type
db_host = myConf.get("db", "db_host")
db_port = myConf.getint("db", "db_port")
db_user = myConf.get("db", "db_user")
db_pass = myConf.get("db", "db_pass")
db_name = myConf.get("db", "db_name")
fileName = myConf.get("output", "fileName")
#commonConf = common.GetConfParser("config.conf")

#获取上两级目录
directory = os.path.split(os.path.realpath(__file__))[0]
directory = os.path.split(directory)[0] + os.sep + "ddl" + os.sep

# 导出表结构
cmd = "mysqldump --opt -d -u" + db_user + " -p" + db_pass + " " + db_name + " --skip-add-drop-table> " + directory +  "gccAllSQL_1.sql"
print cmd
try:
  common.executeCMD(cmd)
except:
  raise

# 导出表数据
tableList = ['marketing','area_type','area','marketing_area']
if len(tableList)>0:
  cmd = "mysqldump -t -u" + db_user + " -p" + db_pass + " " + db_name
  for temp in tableList:
    cmd = cmd + " " + temp
  cmd = cmd + "> " + directory + "gccAllData.sql"
  print cmd
  try:
    common.executeCMD(cmd)
  except:
    raise
