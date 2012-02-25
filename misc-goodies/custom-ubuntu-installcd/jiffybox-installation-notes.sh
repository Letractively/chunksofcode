
############### installation notes for lamp stack in ubuntu: ##############
# https://help.ubuntu.com/community/ApacheMySQLPHP


# AT FIRST, INCREASE CONVENIENCE: ###############

# enable ssh with automated login using public key:
# on CLIENT MACHINE, create private/public key pair (if not already present)
ssh-keygen -t rsa -f ~/.ssh/andre # creates files: ~/.ssh/andre ~/.ssh/andre.pub
# if you do not have a ~/.ssh/config on your client,
# create it with: echo 'Host *' > ~/.ssh/config (if not already present)
echo 'IdentityFile ~/.ssh/andre' >> ~/.ssh/config
# Install public key to REMOTE MACHINE:
cat ~/.ssh/andre.pub | ssh testuser@192.168.56.101 'cat >> ~/.ssh/authorized_keys2'


# INSTALL THE LAMPP STACK: ##############

apt-get install tasksel
tasksel install lamp-server
# mysql root user password: 'semmel3'


# INSTALLATION OF APACHE: ##############

# config directory: /etc/apache2
#              see: /etc/apache2/sites-available/default 

apt-get install apache2
/etc/init.d/apache2 restart # or 'service apache2 restart'

# test apache installation:
wget http://localhost -O - 2>/dev/null



# INSTALLATION OF PHP5: ##############

# config file: /etc/php5/apache2/php.ini

apt-get install libapache2-mod-php5

# deny deprecated "<? " tags (allow only "<?php", because off possible collisions with "<?xml")
vim /etc/php5/apache2/php.ini

# test php installation:
echo '<?php phpinfo(); ?>' > /var/www/test.php
wget http://localhost/test.php -O - 2>/dev/null | grep -iEo "php version 5[^<>]*"
rm /var/www/test.php



# INSTALLATION OF MYSQL: ##############

# config file: /etc/mysql/my.cnf
# root password: newpwd
# test-user: user1, password1

apt-get install mysql-server libapache2-mod-auth-mysql php5-mysql

# if we need to access our databases from other hosts, 
# comment 'bind-address = localhost' (or bind to ip, e.g.: '192.168.1.20')

# test mysql installation:
mysql -u root -p # (exit prompt with '\q' )

# change root password
mysqladmin -u root -p password s3cret # note that password may be stored in your shell history file

# create a database and a user:
mysql -u root -p
# mysql> UPDATE mysql.user SET Password = PASSWORD('newpwd') WHERE User = 'root';
# mysql> FLUSH PRIVILEGES;
# mysql> CREATE DATABASE database1;
# mysql> GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, INDEX, ALTER, CREATE TEMPORARY TABLES, LOCK TABLES ON database1.* TO 'user1'@'localhost' IDENTIFIED BY 'password1';



# INSTALLATION OF PHPMYADMIN: ##############

# config dir: /etc/phpmyadmin/ (config-db.cfg does not need to be edited)

apt-get install phpmyadmin

# include the phpmyadmin module in the apache configuration:
# put the line 'Include /etc/phpmyadmin/apache.conf' into file '/etc/apache2/apache2.conf'



# FINISH LAMPP INSTALLATION ##############

# test installation:
# browse to http://{localhost|serverip}/phpmyadmin and try to log in as root and as user1
/usr/sbin/apache2ctl stop
/usr/sbin/apache2ctl configtest
/usr/sbin/apache2ctl start

# If you just want to run your Apache install
# as a development server and want to prevent
# it from listening for incoming connection attempts:
vim /etc/apache2/ports.conf
# set 'Listen 127.0.0.1:80'

# Do some hardening on the operating system:
apt-get install perl-tk bastille
bastille

# see also: http://ubuntuforums.org/showthread.php?t=1002167
# (Securing Ubuntu/Ubuntu Hardening Guide)



# SETUP JOOMLA ##############

# config file: JOOMLA/installation/models/configuration.php
# admin-benutzername: bigboss, passwort: biggie2

wget http://joomlacode.org/gf/download/frsrelease/16512/72038/Joomla_*.zip
mkdir /var/www/joomla
unzip Joomla*.zip -d /var/www/joomla
chown www-data:www-data -R /var/www/
chmod og-rwx -R /var/www/

# browse to http://localhost/joomla and follow the instructions.



# SETUP SFTP SERVER ##############

addgroup sftponly
useradd -d /home/sftpuser -s /usr/lib/sftp-server -M -N -g sftponly sftpuser
mkdir -p /home/sftpuser/uploads /home/sftpuser/.ssh
chown sftpuser:sftponly /home/sftpuser/uploads /home/sftpuser/.ssh
echo '/usr/lib/sftp-server' >> /etc/shells # register users shell

# enable automated ssh login: copy public key from testuser account:
# (see section: "AT FIRST, INCREASE CONVENIENCE")
cp ~testuser/.ssh/authorized_keys2 /home/sftpuser/.ssh/
chown sftpuser:sftponly /home/sftpuser/.ssh/authorized_keys2
chmod 600 /home/sftpuser/.ssh/authorized_keys2

# change the "Subsystem sftp" line in /etc/ssh/sshd_config to:
Subsystem sftp internal-sftp
# append these lines to /etc/ssh/sshd_config ()
Match group sftponly
    ChrootDirectory %h
    X11Forwarding no
    AllowTcpForwarding no
    ForceCommand internal-sftp
/etc/init.d/ssh restart #apply changes

