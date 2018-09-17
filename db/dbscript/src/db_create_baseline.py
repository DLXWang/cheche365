#!/usr/bin/python
# -*- coding: UTF-8 -*-

import os
import common

from db_logger import logger


def check_output_path(directory):
    if not os.path.exists(directory) or os.path.isfile(directory):
        os.makedirs(directory)

    temp = os.sep.join([directory,'dictionary'])
    if not os.path.exists(temp) or os.path.isfile(temp):
        os.makedirs(temp)


def execute(db_util, script_home):
    baseline_home = os.sep.join([script_home, 'baseline'])
    check_output_path(baseline_home)
    logger.info('start dumping database schema ...')
    create_schema_cmd = ''.join([
            'mysqldump --skip-add-drop-table -d -h', db_util.host,
            ' -P', db_util.port,
            ' -u', db_util.user,
            ' -p', db_util.password,
            ' ', db_util.database, ' > ',
            os.sep.join([baseline_home, 'schema.sql'])
        ])
    if not common.execute_cmd(create_schema_cmd):
        logger.info('found error during dumping, abort...')
        return

    logger.info('...........start dumping dictionary data...................')
    cmd_template = ''.join(['mysqldump -t  --extended-insert --complete-insert -h', db_util.host,
                            ' -P', db_util.port,
                            ' -u', db_util.user,
                            ' -p', db_util.password,
                            ' ', db_util.database,
                            ' {} > {}'])
    with open(os.sep.join([baseline_home, 'dictionary', 'dictionary.txt']), 'r') as dictionary_list:
        for dictionary in dictionary_list.read().splitlines():
            if common.is_skipped_line(dictionary):
                continue
            cmd = cmd_template.format(dictionary, os.sep.join([baseline_home, 'dictionary', dictionary + '.sql']))
            logger.info(cmd)
            if not common.execute_cmd(cmd):
                logger.info('found error during dumping, abort...')
                return
    logger.info('...........dump dictionary data finished...........')

