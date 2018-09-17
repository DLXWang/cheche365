#!/usr/bin/python
# -*- coding: UTF-8 -*-
import os
import common
from db_logger import logger


def compare_version_no(a, b):
    a_list = a.split('.')
    b_list = b.split('.')
    for i in range(len(a_list)):
        if a_list[i] != b_list[i]:
            return int(b_list[i]) - int(a_list[i])
    return 0


def add_version_no(version_no_list, param):
    for i in range(len(version_no_list)):
        if compare_version_no(version_no_list[i], param) <= 0:
            version_no_list.insert(i, param)
            return
    version_no_list.append(param)


def exec_version(script_home, db_util, version_no, db_data=None):
    logger.info('start updating [%s] ...', version_no)
    version_directory = os.sep.join([script_home, version_no])
    script_path = os.sep.join([version_directory, 'script.txt'])
    logger.info(script_path)
    if os.path.exists(script_path):  # no script.txt, skip
        db_list = []
        if db_data and version_no in db_data:
            db_list = db_data[version_no]
        else:
            temp_list = db_util.query(
                'select script from script_history where version = %s',
                [version_no])
            for i in temp_list:
                db_list.append(i[0])

        logger.info('read script.txt in [%s]', script_path)
        line = open(script_path, 'r')
        sql_list = line.read().splitlines()
        logger.info('script.txt has %s sql files', len(sql_list))
        for sql_file_name in sql_list:
            if sql_file_name[0] == '#':
                continue
            sql_file = os.sep.join(
                [version_directory, sql_file_name])
            if sql_file_name in db_list:
                logger.info('The script file [%s] was executed before, just ignore it!', sql_file)
            else:
                logger.info('start running script file [%s] ...', sql_file)
                cmd = 'mysql ' + \
                      '-h' + db_util.host + \
                      ' -P' + db_util.port + \
                      ' -u' + db_util.user + \
                      ' -p' + db_util.password + \
                      ' ' + db_util.database +\
                      ' < ' + sql_file
                if common.execute_cmd(cmd):
                    sql = 'insert into script_history(version, script, create_time) values(%s, %s, now())'
                    db_util.execute_sql(sql, [version_no, sql_file_name])
                else:
                    # todo ask user whether execute other scripts
                    return False

    else:
        logger.info('Did not find script.txt in directory [%s].', script_path)
    return True


def execute(db_util, script_home, version):
    logger.info('start updating ...')
    logger.info('script home: %s', script_home)
    logger.info('dbInfo:host=[%s],dbName=[%s],user=[%s]',
                db_util.host,
                db_util.port,
                db_util.user)

    logger.info('get versions to be updated')
    version_list = []
    if version is None:
        for t_version_no in os.listdir(script_home):
            if not os.path.isdir(os.sep.join([script_home, t_version_no])):
                continue
            add_version_no(version_list, t_version_no)
    else:
        add_version_no(version_list,version)
    logger.info('version to be updated: %s', version_list)

    for version_no in version_list:
        if not exec_version(script_home, db_util, version_no):
            break
    db_util.close()

    logger.info('Database has been updated.')
