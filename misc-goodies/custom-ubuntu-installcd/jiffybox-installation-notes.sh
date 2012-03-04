
###############################################################################
############### installation notes for jiffybox server: #######################
###############################################################################
#
# more info:
# https://help.ubuntu.com/community/ApacheMySQLPHP
#
# starting from a fresh ubuntu installation (profile: openssh-server)
# you may start with the "custom installation cd" (see: create-automatic-ubuntu-cd.txt)
#
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
#
#
# handy apps, nice to have:
apt-get install vim zsh tree
#
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
#
#
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

# 4.) INSTALLATION OF MYSQL: {{{
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
# }}}

# 5.) INSTALLATION OF PHPMYADMIN: {{{
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
# browse to http://localhost/joomla and follow the instructions.
#
# }}}

# 8.) INSTALL TOMCAT AND JAVA {{{
###############################################################################
#
# TODO continue here
#
# }}}



# vim:filetype=sh:foldmethod=marker 
