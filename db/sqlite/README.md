## First install sqlite.
+	Download source file of sqlite from address https://www.sqlite.org/download.html
'''bash
	$tar xvfz sqlite-autoconf-xxx.tar.gz
	$cd sqlite-autoconf-xxx
	$./configure --prefix=/usr/local
	$make
	$make install
'''

+	Compile command:
'''bash
	$gcc sqlite.c -l sqlite3 -o sqlite
	$./sqlite
'''

+	Sqlite introduction
	[sqlite][ref]: http://www.runoob.com/sqlite/sqlite-commands.html
