# First install sqlite.

+	Download source file of sqlite from [sqlite]https://www.sqlite.org/download.html

'''shell
	$tar xvfz sqlite-autoconf-xxx.tar.gz
	$cd sqlite-autoconf-xxx
	$./configure --prefix=/usr/local
	$make
	$make install
'''

+	Compile command:

'''shell
	$gcc sqlite.c -l sqlite3 -o sqlite
	$./sqlite
'''

+	Sqlite introduction
	[sqlite documentation]http://www.runoob.com/sqlite/sqlite-commands.html
