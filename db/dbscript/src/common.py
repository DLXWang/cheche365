#!/usr/bin/python
# -*- coding: UTF-8 -*-
import configparser
import subprocess
import MySQLdb
from db_logger import logger


def is_skipped_line(line):
    return line is None or line.isspace() or line.lstrip().startswith('#')


def load_conf(conf_file):
    config_parser = configparser.ConfigParser()
    config_parser.read(conf_file)
    return config_parser


def adjust_message(message, padding_char='-', length=80):
    return message.center(length, padding_char)


def execute_cmd(cmd):
    p = subprocess.Popen(cmd, shell=True,
                         stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    std_output, error_output = p.communicate()
    logger.info("start run cmd:" + cmd)
    if has_error(error_output):
        logger.info("error: " + error_output.decode("utf8"))
        return False
    return True


def has_error(error_output):
    for line in error_output.decode("utf8").splitlines():
        if line.startswith("mysql: [Warning]") or line.startswith('mysqldump: [Warning]'):
            continue
        return True
    return False


class DBUtil(object):

    def __init__(self, host, port, user, password, db):
        self.__host = host
        self.__port = port
        self.__user = user
        self.__password = password
        self.__db = db
        self.__connection = None

    @property
    def host(self):
        return self.__host

    @property
    def port(self):
        return self.__port

    @property
    def user(self):
        return self.__user

    @property
    def password(self):
        return self.__password

    @property
    def database(self):
        return self.__db

    def connect(self):
        self.__connection = MySQLdb.connect(
            host=self.__host,
            user=self.__user,
            passwd=self.__password,
            db=self.__db,
            charset='utf8',
        )

    def execute_sql(self, sql, param_list):
        with self.__connection.cursor() as cursor:
            cursor.execute(sql, param_list)
           # logger.info("执行sql[%s]执行成功，影响%i条记录", sql, _exec_count)

    def query(self, sql, param_list):
        with self.__connection.cursor() as cursor:
            _exec_count = cursor.execute(sql, param_list)
           # logger.info("查询sql[%s]执行成功, 返回%i条", sql, _exec_count)
            return cursor.fetchmany(_exec_count)

    def close(self):
        self.__connection.commit()
        self.__connection.close()
