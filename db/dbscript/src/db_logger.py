#!/usr/bin/python
# -*- coding: UTF-8 -*-

import logging
import logging.config
import os


def __init__logger():
    # initialize logger
    print('initialize logging configuration')
    logging.config.fileConfig(os.path.join(
        os.path.dirname(__file__), 'conf/logging.conf'))
    return logging.getLogger("dbscript")

logger = __init__logger()
