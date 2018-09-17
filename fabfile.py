from fabric.api import local, run, sudo, cd


def build_product_cheche(spring_profile='dev'):
    """build product"""
    local('gradlew -Dspring.profiles.active=itg buildProduct')


def update():
    local('ls -al ..')


def build_prod_cheche():
    local('chgrp -R Users *')
