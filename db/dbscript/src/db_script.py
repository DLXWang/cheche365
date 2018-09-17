#!/usr/bin/python
# -*- coding: UTF-8 -*-

import argparse

import db_create_baseline
import db_init
import db_update
import os
import common
import getpass
from db_logger import logger


def main():

    # get param from configFile
    conf = common.load_conf(os.path.join(
        os.path.dirname(__file__), 'conf/db_script.conf'))

    # create arg parser
    arg_parser = argparse.ArgumentParser(
        prog='dbscript',
        description='a set of db scripts to rebase/update db schema')
    # create sub commands
    sub_parsers = arg_parser.add_subparsers(
        dest='command',
        help='sub command help')
    # sub command base
    sub_parser = sub_parsers.add_parser(
        'base', help='create db baseline base on target database')
    sub_parser.add_argument('-o', '--output', help='the output directory')
    # sub command init
    sub_parser = sub_parsers.add_parser(
        'init', help='initialize database according to db baseline')
    sub_parser.add_argument('-i', '--input', help='the input directory')
    # sub command update
    sub_parser = sub_parsers.add_parser(
        'update', help='update database schema')
    sub_parser.add_argument('-v', '--version', help='the db script version to be run', default=None)
    # common args
    arg_parser.add_argument('-H', '--host', help='database host', default=conf.get('db', 'host'))
    arg_parser.add_argument('-P', '--port', help='database port', default=conf.get('db', 'port'))
    arg_parser.add_argument('-u', '--user', help='database user', default=conf.get('db', 'user'))
    arg_parser.add_argument('-d', '--database', help='database schema', default=conf.get('db', 'database'))
    arg_parser.add_argument('-s', '--script_home', help='db script home', default=conf.get('db', 'script_home'))

    args = arg_parser.parse_args()
    logger.debug(vars(args))

    if args.command not in ('base', 'init', 'update'):
        logger.error("unknown command: {0}".format(args.command))
        arg_parser.print_help()
        return

    db_password = getpass.getpass("Please input db password:")
    db_util = common.DBUtil(args.host, args.port, args.user, db_password, args.database)
    db_util.connect()

    script_home = args.script_home
    if args.command == 'base':
        db_create_baseline.execute(db_util, script_home)
    elif args.command == 'init':
        db_init.execute(db_util, script_home)
    elif args.command == 'update':
        db_update.execute(db_util, script_home, args.version)


if __name__ == '__main__':
    main()
