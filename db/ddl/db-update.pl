#!/usr/bin/perl
use DBI;
use Cwd;
use strict;
use warnings;
use Getopt::Long;
use Term::ReadKey;

sub main(){
    my ($help, $host, $database, $user, $script, $password);
    #-- prints usage if no command line parameters are passed or there is an unknown
    #   parameter or help option is passed
    usage() if ( @ARGV < 1 or
        !GetOptions('help|?' => \$help,
                   'host=s' => \$host,
                   'database=s' => \$database,
                   'user=s' => \$user,
                   'script=s' => \$script)
        or defined $help );

    print "please input db password: \n";

    # read passwordx
    ReadMode('noecho');
    ReadMode('raw');

    while (1) {
      my $c;
      1 until defined($c = ReadKey(-1));
      last if $c eq "\n";
      print "*";
      $password .= $c;
    }

    ReadMode('restore');
    # print "\n[$password]\n";

    my $dbh = DBI->connect("DBI:mysql:database=$database;host=$host;user=$user;password=$password",{RaiseError => 1, AutoCommit => 0}) or die $DBI::errstr;
    $dbh->do("SET character_set_client='utf8'");
    $dbh->do("SET character_set_connection='utf8'");
    $dbh->do("SET character_set_results='utf8'");
    my $sth = $dbh->prepare("select * from script_history where version=? and script=?");
    my $usth = $dbh->prepare("insert into script_history(version, script, create_time) values(?, ?,now())");

    my $command = "";
    my $result = "";
    my $currentDirectory = cwd();
    my $index = rindex $currentDirectory, "/";
    my $version = substr $currentDirectory, $index + 1;
    open SCRIPT_FILE, "<", $script or die $!;
    while (my $line = <SCRIPT_FILE> ) {
        $line =~ s/[\r\n]//g;
        chomp($line);
        if (!($line =~ m/^#/)) {
            $command = "mysql --default-character-set=utf8 --host=$host --user=$user --database=$database --password=$password < ". $line;
            #print $command . "\n";
            print "trying to execute script $line\n";
            $sth->execute($version, $line);
            if ($sth->rows == 0) {
                `$command`;
                #print $result . "\n";
                $usth->execute($version, $line);
                sleep 5;
            }else{
                print "$line already executed\n";
            }

        }
    }

    close(SCRIPT_FILE);
    $sth->finish();
    $usth->finish();
    $dbh->disconnect;
}

sub usage
{
    print "usage: \n";
    print "--help         display this help and exit\n";
    print "--host         db host name\n";
    print "--database     database name\n";
    print "--user         db user name\n";
    print "--script       db update script\n";
    exit;
}

main();
