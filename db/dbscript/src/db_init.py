#!/usr/bin/python
# -*- coding: UTF-8 -*-
import os
import common
from db_logger import logger


def execute(db_util, directory):
    logger.info('start importing db schema ...')
    cmd_template = ''.join(['mysql -h', db_util.host,
                            ' -P', db_util.port,
                            ' -u', db_util.user,
                            ' -p', db_util.password,
                            ' --default-character-set=utf8',
                            ' ', db_util.database,
                            ' < {}'])
    baseline_home = os.sep.join([directory,'baseline'])
    cmd = cmd_template.format(os.sep.join([baseline_home, 'schema.sql']))
    if not common.execute_cmd(cmd):
        logger.warn('found error, abort ... ')
        return

    logger.info('start importing dictionary data ...')

    with open(os.sep.join([baseline_home, 'dictionary', 'dictionary.txt']), 'r') as dictionary_list:
        for dictionary in dictionary_list.read().splitlines():
            if common.is_skipped_line(dictionary):
                continue
            cmd = cmd_template.format(os.sep.join([baseline_home, 'dictionary', dictionary + '.sql']))
            logger.info('importing dictionary data [%s]', dictionary)
            if not common.execute_cmd(cmd):
                logger.info('found error, abort ... ')
                return

    logger.info(common.adjust_message('import dictionary data completely'))

    # TODO importing test data
