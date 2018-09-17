__author__ = 'gaochengchun'
import logging
import logging.config

logging.config.fileConfig("logger.conf")
logger = logging.getLogger("example01")

def info(msg):
  logger.info(msg)

def debug(msg):
  logger.debug(msg)
