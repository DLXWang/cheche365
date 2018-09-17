from setuptools import setup, find_packages
import sys, os

version = '0.0.1'

setup(name='dbscript',
      version=version,
      description="a set of db script",
      long_description="""\
a set of db script use to create/update/sync database schema""",
      classifiers=[], # Get strings from http://pypi.python.org/pypi?%3Aaction=list_classifiers
      keywords='database script',
      author='Cheche DevOps Team',
      author_email='devops@cheche365.com',
      url='http://devops.cheche365.com/dbscript',
      license='MIT',
      packages=find_packages('src', exclude=['ez_setup', 'examples', 'tests']),
      package_dir={'': 'src'},
      include_package_data=True,
      zip_safe=False,
      install_requires=[
            'mysqlclient',# -*- Extra requirements: -*-
      ],
      entry_points={
          'console_scripts': [
               'dbscript = db_script:main'
          ]
      },
      )
