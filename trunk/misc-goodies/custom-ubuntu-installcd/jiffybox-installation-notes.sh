
###############################################################################
############### installation notes for jiffybox server: #######################
###############################################################################

# more info:
# https://help.ubuntu.com/community/ApacheMySQLPHP

# starting from a fresh ubuntu installation (profile: openssh-server)
# you may start with the "custom installation cd" (see: create-automatic-ubuntu-cd.txt)

# credentials used for test installations # {{{
# if you want to setup a machine for testing purposes only (!), you may want to use
# those for convenience: (they are also listed in the corresponding sections below)
#-------------------------------------------------
# login             username        password
#-------------------------------------------------
# ubuntu            testuser        testpassword
# mysql             root            newpwd
# mysql             user1           password1
# joomla            bigboss         biggie2
#-------------------------------------------------
# 
# }}}


###############################################################################
############### finalize ubuntu installation: #################################
###############################################################################


# handy apps, nice to have:
apt-get install vim zsh tree zip unzip gzip lzma unrar rsync

# Enable secure automatic ssh login {{{:
# On CLIENT MACHINE, create private/public key pair. (If not already present)
ssh-keygen -t rsa -f ~/.ssh/andre # creates files: ~/.ssh/andre ~/.ssh/andre.pub
# XXX if you do not have a ~/.ssh/config on your client yet, execute:
{ echo 'Host *'; echo 'IdentityFile ~/.ssh/andre'; } > ~/.ssh/config
#
# Install public key to REMOTE MACHINE:
cat ~/.ssh/andre.pub | ssh testuser@192.168.0.101 'cat >> ~/.ssh/authorized_keys2'
# }}}

# Customize zsh settings : {{{
wget -O /etc/zsh/zshrc http://git.grml.org/f/grml-etc-core/etc/zsh/zshrc # grml kicks ass!!!
#
# XXX add these lines to /etc/zsh/zshrc to bind some keys in zsh:
########### start
# bind keys ctrl+left and ctrl+right to jump wordwise like vim:
# escape sequence for ctrl+left:   ^[[1;5D
# escape sequence for ctrl+right:  ^[[1;5C
bindkey "^[[1;5D" backward-word
bindkey "^[[1;5C" forward-word
bindkey "^H" backward-kill-word
########### end
#
cp /etc/zsh/zshrc /root/.zshrc # apply for root
# }}}

# setup vim : {{{
{ echo ':colorscheme desert'; echo ':syn on'; echo ':set foldmethod=marker' } >> /etc/vim/vimrc
#
# }}}

# limit sudo usage to usergroup: {{{
groupadd chucknorris
# Allow members of group chucknorris to execute any command after they have
# provided their password
# XXX: add this line to /etc/sudoers:
%chucknorris ALL=(ALL) ALL
usermod -a -G chucknorris testuser
#
# }}}

###############################################################################
############### setup the service software: ###################################
###############################################################################

# 1.) INSTALL THE LAMPP STACK {{{
###############################################################################
#
apt-get install tasksel
tasksel install lamp-server
# mysql root user password: 'newpwd'
#
# }}}

# 2.) INSTALLATION OF APACHE WEBSERVER {{{
###############################################################################
#
# config directory: /etc/apache2
#              see: /etc/apache2/sites-available/default 
#
apt-get install apache2
/etc/init.d/apache2 restart # or 'service apache2 restart'
#
# test apache installation:
wget http://localhost -O - 2>/dev/null


######### how to bind a subdomain to a specific directory: #########
vim /etc/apache2/sites-available/default

# Hostname            Typ    Priorität        Ziel    
# 
# ragg.ws             A                       141.0.20.92    
# andre.ragg.ws       A                       141.0.20.92  

<VirtualHost *:80>
# default page:
        ServerName ragg.ws
        ServerAlias www.ragg.ws
        ServerAdmin webmaster@localhost
        DocumentRoot /data/www-data/toplevel
        <Directory />
                Options FollowSymLinks
                AllowOverride None
        </Directory>
        <Directory /data/www-data/toplevel>
                Options Indexes FollowSymLinks MultiViews
                AllowOverride None
                Order allow,deny
                allow from all
        </Directory>

        ScriptAlias /cgi-bin/ /usr/lib/cgi-bin/
        <Directory "/usr/lib/cgi-bin">
                AllowOverride None
                Options +ExecCGI -MultiViews +SymLinksIfOwnerMatch
                Order allow,deny
                Allow from all
        </Directory>

        ErrorLog /var/log/apache2/error.log

        # Possible values: debug,info,notice,warn,error,crit,alert,emerg
        LogLevel warn
        CustomLog /var/log/apache2/access.log combined
</VirtualHost>

<VirtualHost *:80>
# andres section
  ServerName andre.ragg.ws
  DocumentRoot /data/www-data/subdomain-andre
</VirtualHost>
#
# }}}

# 3.) INSTALLATION OF PHP5 {{{
###############################################################################
#
# config file: /etc/php5/apache2/php.ini
#
apt-get install libapache2-mod-php5
#
# deny deprecated "<? " tags (allow only "<?php", because off possible collisions with "<?xml")
vim /etc/php5/apache2/php.ini
# set 'short_open_tag = Off'
#
# test php installation:
echo '<?php phpinfo(); ?>' > /var/www/test.php
wget http://localhost/test.php -O - 2>/dev/null | grep -iEo "php version 5[^<>]*"
rm /var/www/test.php
#
# }}}

# 4.) INSTALLATION OF MYSQL {{{
###############################################################################
#
# config file: /etc/mysql/my.cnf
# root password: newpwd
# test-user: user1, password1
#
apt-get install mysql-server libapache2-mod-auth-mysql php5-mysql
#
# XXX if we need to access our databases from other hosts, 
# comment 'bind-address = localhost' (or bind to ip, e.g.: '192.168.1.20')
#
# test mysql installation:
mysql -u root -p # (exit prompt with '\q' )
#
# change root password
mysqladmin -u root -p password s3cret # note: password may be stored in your shell history file
#
# create a database and a user:
mysql -u root -p
# on mysql prompt:
# mysql> UPDATE mysql.user SET Password = PASSWORD('newpwd') WHERE User = 'root';
# mysql> FLUSH PRIVILEGES;
# mysql> CREATE DATABASE database1;
# mysql> GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, INDEX, ALTER, CREATE TEMPORARY TABLES, LOCK TABLES ON database1.* TO 'user1'@'localhost' IDENTIFIED BY 'password1';
#
#
# XXX if you want to move your db data to another place:
service apache2 stop; service mysql stop
vim /etc/mysql/my.cnf
# change the datadir variable to your preferred location,
# and move the data to the target.
service mysql start; service apache2 start
#
#
# enable accessing data from hosts outside:
/sbin/iptables -A INPUT -i eth0 -p tcp --destination-port 3306 -j ACCEPT
# XXX goto phpmyadmin -> privileges and change the host of the user to "any host"
#
# }}}

# 5.) INSTALLATION OF PHPMYADMIN {{{
###############################################################################
#
# config dir: /etc/phpmyadmin/ (config-db.cfg does not need to be edited)
# (note: use the mysql-users to login)
#
apt-get install phpmyadmin
#
# include the phpmyadmin module in the apache configuration:
# XXX append the line 'Include /etc/phpmyadmin/apache.conf' into file '/etc/apache2/apache2.conf'
# (if not present)
#
# }}}

# 6.) FINISH LAMPP INSTALLATION {{{
###############################################################################
#
# test installation:
/usr/sbin/apache2ctl stop
/usr/sbin/apache2ctl configtest
/usr/sbin/apache2ctl start
# browse to http://{localhost|serverip}/phpmyadmin and try to log in as root and as user1
#
# If you just want to run your Apache install
# as a development server and want to prevent
# it from listening for incoming connection attempts:
vim /etc/apache2/ports.conf
# set 'Listen 127.0.0.1:80'
#
# Do some hardening on the operating system:
apt-get install perl-tk bastille
bastille
#
# see also: http://ubuntuforums.org/showthread.php?t=1002167
# (Securing Ubuntu/Ubuntu Hardening Guide)
#
# }}}

# 7.) SETUP JOOMLA {{{
###############################################################################
#
# config file: JOOMLA/installation/models/configuration.php
# admin-benutzername: bigboss, passwort: biggie2
#
wget http://joomlacode.org/gf/download/frsrelease/16512/72038/Joomla_*.zip
mkdir /var/www/joomla
unzip Joomla*.zip -d /var/www/joomla
chown www-data:www-data -R /var/www/
chmod og-rwx -R /var/www/
#
# browse to http://localhost/joomla and follow the installation instructions.


# how to remove the "index.php" from the joomla url:
#
mv JOOMLA_BASE/htaccess.txt JOOMLA_BASE/.htaccess
#
# XXX browse to joomla/administrator, goto global configuration, 
# make sure the options "Search engine friendly URLs" and 
# "Use URL rewriting" are set to "Yes"
#
a2enmod rewrite # enables the "rewrite" module in apache
service apache2 restart











# OPTIONAL: install advanced syntax highlighting in GeSHi Plugin:

# get GeSHi:
wget "http://sourceforge.net/projects/geshi/files/geshi/GeSHi%201.0.8.10/GeSHi-1.0.8.10.zip/download" -O GeSHi-1.0.8.10.zip











# }}}

# 8.) INSTALL ORACLE JAVA {{{
###############################################################################
#
cd /usr/local/ # where we want to install it
wget http://download.oracle.com/otn-pub/java/jdk/6u31-b04/jre-6u31-linux-x64.bin
/bin/sh jre-6u31-linux-x64.bin # creates dir jre1.6.0_31 and extracts archive
ln -s jre1.6.0_31/ java # create link /usr/local/java -> jre1.6.0_31/
#
# tell ubuntu that java was installed:
update-alternatives --install "/usr/bin/java" "java" "/usr/local/java/bin/java" 1
update-alternatives --set java "/usr/local/java/bin/java"
java -version # tests installation

# add/edit JAVA_HOME in file /etc/environment: 'JAVA_HOME=/usr/local/java'
. /etc/profile # reload environment (note: affects only this terminal session)
# (system restart may be required due to env change)
#
# }}}

# 9.) INSTALL APACHE TOMCAT {{{
###############################################################################
#
# STEP 1) SETUP A SHARED TOMCAT INSTALLATION
#
cd /opt/ # where we want to install tomcat to
wget http://mirror.sti2.at/apache/tomcat/tomcat-7/v7.0.26/bin/apache-tomcat-7.0.26.zip
unzip apache-tomcat-7.0.26.zip
ln -s apache-tomcat-7.0.26 tomcat
# add/edit CATALINA_BASE in file /etc/environment: 'JAVA_HOME=/usr/local/java'
#
# XXX we may want to use multiple tomcat instances, but every instance
# should use the same installation. 
#
# STEP 2) SETUP A TOMCAT INSTANCE FOR A USER:
#
# /opt/tomcat
# /home/andre/bin/tomcat_base
#
# tomcat uses following variables:
# CATALINA_HOME    the expanded (unzipped) tomcat installation
# CATALINA_BASE    the location for a given instance.
#
# setup the instance directory XXX AS USER andre !
mkdir -p /home/andre/bin/tomcat/tomcat_base
cd /home/andre/bin/tomcat/tomcat_base
mkdir {bin,conf,logs,temp,webapps,work}
echo '# do whatever you want change in catalina.sh in this file.' >> bin/setenv.sh
cp /opt/tomcat/conf/{server,web}.xml conf/
#
# create a startup skript for tomcat instance:
vim /etc/init.d/tomcat-andre.sh
#
# insert these lines: (see /opt/tomcat/bin/catalina.sh for brief documentation)
# 1   #!/bin/sh
# 2   CATALINA_HOME=/opt/tomcat
# 3   CATALINA_BASE=/home/andre/bin/tomcat_base
# 4   /bin/sh $CATALINA_HOME/bin/catalina.sh $@
#
# allow andre to start/stop :D
chgrp andre /etc/init.d/tomcat-andre.sh
chmod 754 /etc/init.d/tomcat-andre.sh 
#
# test installation: (XXX as andre)
cp /data/shared/helloworld.war /home/andre/bin/tomcat/webapps/test.war
/etc/init.d/tomcat-andre.sh start
# browse to: http://ragg.ws:8080/test
#
# }}}

# 9.1.) CONNECT TOMCAT WITH APACHE SERVER {{{
###############################################################################
#
# talk with tomcat through apache on port 80 (using an AJP/1.3 connection)
# http://tomcat.apache.org/tomcat-3.3-doc/mod_jk-howto.html (still up 2 date!)
#
# for example, 
# you want the app myhost.com:8080/testapp to be available at myhost.com/testapp
#
# STEP 1
# install apache module "mod_jk"
apt-get install libapache2-mod-jk
#
# STEP 2 
# configure your tomcat as a mod_jk worker
vim /etc/apache2/workers.properties
# EXAMPLE: insert following lines:
# 1   # define one single worker in this example:
# 2   worker.list=andretomcat
# 3   # configure worker:
# 4   worker.andretomcat.type=ajp13
# 5   worker.andretomcat.host=localhost
# 6   worker.andretomcat.port=8009
#
# STEP 3
# configure mod_jk in apache conf:
vim /etc/apache2/apache2.conf
# insert following lines:
# 2  JkWorkersFile /etc/apache2/workers.properties
# 5  JkLogFile /var/log/apache2/mod_jk.log
# 4  JkShmFile /var/log/apache2/mod_jk.shm
# 6  JkLogLevel info
# 7  JkLogStampFormat "[%a %b %d %H:%M:%S %Y] "
#
# map requests by url pattern to tomcat instance:
vim /etc/apache2/sites-enabled/000-default
# in the desired <VirtualHost *:80> (or globally),
# place the line "JKMount /tomcat-app-contextroot/* andretomcat" 
# (BELOW the line "DocumentRoot /path/to/documentroot")
#
# STEP 4
# configure tomcat: (disable http, enable ajp)
vim tomcat/conf/server.xml
# uncomment <Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />
# comment <Connector port="8081" protocol="HTTP/1.1" redirectPort="8443" />
# if tomcat and apache run on the same machine, 
# you can set the Connector-Attribut "address" to localhost. 
# Now tomcat is only reachable through localhost
# }}}

# 10.) INSTALL JBOSS APPLICATION SERVER {{{
###############################################################################
# https://docs.jboss.org/author/display/AS71/Getting+Started+Guide
useradd -d /home/jboss -m jboss
usermod -a -G jboss andre
chsh jboss


# XXX as jboss user:
cd /home/jboss
wget http://download.jboss.org/jbossas/7.1/jboss-as-7.1.1.Final/jboss-as-7.1.1.Final.tar.gz
tar -xvf jboss-as-7.1.1.Final.tar.gz 
ln -s jboss-as-7.1.1.Final jboss
chown jboss:jboss -R .
chmod o-rwx -R .
chmod g-w -R .
# set: JBOSS_HOME=/home/jboss/jboss in /etc/environment
# (system restart may be required due to env change)

# test installation
cd /home/jboss/jboss/bin/
sh standalone.sh &
wget localhost:9990 -O -

# convenience:
vim ~/.zshrc  
# PATH="$PATH:/home/jboss/jboss/bin"
# alias killjboss='ps auxwww | grep -E "(org\.jboss|8080)" | awk '"'"'{print $2}'"'"' | xargs kill -9'
# alias startjboss='~/jboss/bin/standalone.sh > ~/jboss-standalone.out 2> ~/jboss-standaone.err & ; tail -F jboss-standalone.{out,err}'

# configuration listen to all hosts (not only localhost)
cd /home/jboss/jboss/bin/
# add management user
./add-user.sh

vim jboss/standalone/configuration/standalone.xml
# insert any address to get internet connection
# <interfaces>
#   <interface name="management">
#     <any-address/>
#     </interface>
#   <interface name="public">
#    <any-address/>
#   </interface>
# </interfaces>

# }}}

# 11.) INSTALL POSTGRESQL {{{
###############################################################################

# config dir: /etc/postgresql/8.4/main/

apt-get install postgresql 

# create a user and a database:
su - postgres
createuser andre -P
createdb andretest1 -O andre
andre@j23049 ~ % psql andretest1 # test if it works. :D


# enable remote connections:
su - postgres
vim /etc/postgresql/8.4/main/postgresql.conf
# set property: "listen_addresses = '*'" to make the server 
# listen to hosts outside (generally).
vim /etc/postgresql/8.4/main/postgresql.conf
# now, add this line to grant remote connections to db 'andre1' for user andre
# host andre1 andre 0.0.0.0/0 md5
/etc/init.d/postgresql-8.4 restart


# }}}

# 11.1) OPTIONAL: PHPPGADMIN INSTALLATION # {{{

apt-get install phppgadmin
# 
# XXX installation of phppgadmin automatically links /etc/phppgadmin/apache.conf 
# into /etc/apache2/conf.d/, so therefore no include statement is needed in 
# in /etc/apache2/apache.conf as seen in section 'PHPMYADMIN'
#
# allow access from hosts outside (as in phpmyadmin installation)
vim /etc/phppgadmin/apache.conf
# set 'allow from all' to be able to connect remotely


# }}}

# 12.) SETUP A MAVEN REPOSITORY # {{{


# set up a Private remote internal repository using artifactory
# see: http://www.theserverside.com/news/1364121/Setting-Up-a-Maven-Repository

# i will use a separate tomcat instance and a separate user for artifactory.
#
#
# STEP 1.) setup server environment ###################################
#
useradd -d /home/artifactory -m artifactory
usermod -a -G artifactory artifactory
chsh artifactory -s /bin/zsh
#
# XXX create a tomcat instance as described in chapter 9 for artifactory user.
# (in this example: /home/artifactory/bin/tomcat_base)
#
# configure tomcat {{{
# tomcat/server.xml: add line
#    '<Connector port="8010" protocol="AJP/1.3" redirectPort="8443" address="localhost"/>'
#
# use apache as proxy (described in 9.1)
#
# add a worker to: /etc/apache2/workers.properties
#   worker.list=<EXISTING-WORKERS>,artifactorytomcat
#   worker.artifactorytomcat.type=ajp13
#   worker.artifactorytomcat.host=localhost
#   worker.artifactorytomcat.port=8010
#
# mount the application in: /etc/apache2/sites-available/default
#   <VirtualHost *:80>
#     ServerName andre.ragg.ws
#     [...]
#     # artifactory tomcat instance: (maven repo)
#     JkMount /artifactory* artifactorytomcat
#   </VirtualHost>
#}}}
#
# STEP 2.) setup artifactory installation ###################################
#
wget "http://sourceforge.net/projects/artifactory/files/artifactory/2.5.1.1/artifactory-2.5.1.1.zip/download" -O artifactory-2.5.1.1.zip
unzip artifactory-2.5.1.1.zip
#
# let's work on a link as base dir:
ln -s artifactory-2.5.1.1 artifactory_base
cd artifactory_base
#
# "deploy" the artifactory.war:
ln -vs /home/artifactory/artifactory_base/webapps/artifactory.war /home/artifactory/bin/tomcat_base/webapps/
#
# tell the webappl where is artifactory home:
vim /home/artifactory/bin/tomcat_base/bin/setenv.sh
# append 'export JAVA_OPTS="$JAVA_OPTS -Dartifactory.home=/home/artifactory/artifactory_base"'
#
# start the artifactory server:
/etc/init.d/tomcat-artifactory.sh start
# browse to web console: http://andre.ragg.ws/artifactory
# login as 'admin' with 'password' (and change password)
#
#
# STEP 2.1) OPTIONAL: tell artifactory to use mysql: # {{{
#
# create mysql db and user:
mysql -u root -p
# > create database artifactory character set utf8;
# > GRANT SELECT,INSERT,UPDATE,DELETE,CREATE,DROP,ALTER,INDEX on artifactory.* TO 'artifactory'@'localhost' IDENTIFIED BY 'password1';

cd /home/artifactory # (as artifactory user)

# install mysql driver to tomcat:
wget http://www.mysql.com/get/Downloads/Connector-J/mysql-connector-java-5.1.19.zip/from/http://gd.tuwien.ac.at/db/mysql/ -O mysql-connector-java-5.1.19.zip
unzip mysql-connector-java-5.1.19.zip
cp mysql-connector-java-5.1.19/mysql-connector-java-5.1.19-bin.jar bin/tomcat_base/lib/


# tell artifactory to use mysql:
vim artifactory_base/etc/artifactory.system.properties
# artifactory.jcr.configDir=repo/filesystem-mysql

# configure datasource in:
vim artifactory_base/etc/repo/filesystem-mysql/repo.xml
#
# }}}
#
#
# STEP 3.) setup maven client ###################################
#
# STORE CREDENTIALS ASSIGNED TO A SERVER USER:
#
# (create a user in the artifactory web console.)
# update your ~/.m2/settings.xml to tell maven the server settings:
# {{{
#      <servers>
#            [...]
#            <!-- define your user's credentials here to deploy
#               your artifacts on the server. (server.id is
#               referenced in poms at <distributionManagement>  section-->
#            <server>
#                  <id>andre.ragg.ws</id>
#                  <username>artifactoryUserName</username>
#                  <password>******</password>
#            </server>
#      </servers>
#
#      <profiles>
#         [...]
#         <!-- add a profile that configures the repositories
#               used to fetch artifacts during build 
#               (overrides default maven repo) -->
#         <profile>
#            <id>artifactory-repository</id>
#            <activation>
#                <activeByDefault>true</activeByDefault>
#            </activation>
#            <repositories>
#                <repository>
#                    <id>central</id>
#                    <url>http://andre.ragg.ws/artifactory/repo</url>
#                    <snapshots>
#                        <enabled>false</enabled>
#                    </snapshots>
#                </repository>
#                <repository>
#                    <id>snapshots</id>
#                    <url>http://andre.ragg.ws/artifactory/repo</url>
#                    <releases>
#                        <enabled>false</enabled>
#                    </releases>
#                </repository>
#            </repositories>
#            <pluginRepositories>
#                <pluginRepository>
#                    <id>central</id>
#                    <url>http://andre.ragg.ws/artifactory/repo</url>
#                    <snapshots>
#                        <enabled>false</enabled>
#                    </snapshots>
#                </pluginRepository>
#                <pluginRepository>
#                    <id>snapshots</id>
#                    <url>http://andre.ragg.ws/artifactory/repo</url>
#                    <releases>
#                        <enabled>false</enabled>
#                    </releases>
#                </pluginRepository>
#            </pluginRepositories>
#        </profile>
#    </profiles>
# }}}
#
# put this to the project's pom.xml's: (to be able to use deploy:deploy)
# {{{
# <project>
#    [...]
#    <distributionManagement>
#        <repository> <id>andre.ragg.ws</id>
#            <url>http://andre.ragg.ws/artifactory/libs-release-local</url>
#        </repository>
#        <snapshotRepository> <id>andre.ragg.ws</id>
#            <url>http://andre.ragg.ws/artifactory/libs-snapshot-local</url>
#        </snapshotRepository>
#    </distributionManagement>
#    [...]
# </project>
# }}}
#
# test: deploy an artifact to server (from client)
mvn install deploy:deploy # used repo depends on: version.endsWith("SNAPSHOT")


# }}}


# vim:filetype=sh:foldmethod=marker

