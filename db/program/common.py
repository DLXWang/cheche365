#!/usr/bin/python
# -*- coding: UTF-8 -*-
import ConfigParser
import subprocess
import logging
import logging.config

logging.config.fileConfig("logger.conf")
ccLogger = logging.getLogger("example01")

def GetConfParser(fileName):
  cf = ConfigParser.ConfigParser()
  cf.read(fileName)
  return cf

def getAllSqlPath():
  commonConf = GetConfParser("common.conf")

def executeCMD(cmd):
  try:
    print cmd
    p = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    stdoutput, erroutput = p.communicate()
    ccLogger.info("start run cmd:"+ cmd)
    if erroutput != "":
      ccLogger.info("success:"+erroutput.decode('gbk'))
      return False
    else:
      ccLogger.info("fail:"+stdoutput.decode('gbk'))
      return True
  except:
    raise

