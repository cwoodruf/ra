#!/usr/local/bin/php
<?php
$lib = dirname(dirname(__FILE__));
print "run both mysql2schema.pl and makeclasses.php to build out classes for a database\n";

$db = $argv[1];
if (empty($db)) die("need name of database!\n");

$thistable = null;
if (isset($argv[2])) $thistable = $argv[2];
# this arg can be empty

if (isset($argv[3])) $modeldir = $argv[3];
if (empty($modeldir)) $modeldir = "models/base";

print "usage {$_SERVER['PHP_SELF']} {database '$db'} [table '$thistable'] [model directory: '$modeldir']\n";

if (!file_exists($modeldir)) mkdir($modeldir,0777,true);
$schemafile = "$modeldir/$db-schema.php";
$mysqlfile = "$modeldir/$db.mysql";

print "These user credentials will be saved for db access.\n";

@include_once("$modeldir/{$db}_db.php");
if (($stdin = fopen('php://stdin','r')) !== false) {
	$dbclass = ucfirst($db).'DB';
	$dbok = false;
	if (class_exists($dbclass)) {
		$dbvars = get_class_vars($dbclass);
		$myuser = $dbvars['db']['login'];
		$mypw = $dbvars['db']['pw'];
		$dbhost = $dbvars['db']['host'];
		print "found mysql user: $myuser, host: $dbhost. Ok? [Ynq] ";
		$dbok = fgets($stdin,255);
		if (preg_match('#^[qQ]#', $dbok)) exit;
		$dbok = (preg_match('#^(y(es|)|\s*)$#i',$dbok) ? true : false);
	}
	if (!$dbok) {
		print "mysql user: ";
		$myuser = fgets($stdin,255);
		$myuser = preg_replace('#[\n\r]#','',trim($myuser));
		print "mysql password: ";
		$mypw = fgets($stdin,255);
		$mypw = preg_replace('#[\n\r]#','',trim($mypw));
		print "mysql host: ";
		$dbhost = fgets($stdin,255);
		$dbhost = preg_replace('#[\n\r]#','',trim($dbhost));
	}
	/* # older approach that does not capture views
	shell_exec(
		"mysqldump -u'$myuser' -p'$mypw' -h'$dbhost' --opt --no-data $db ".
		"| /usr/bin/tee $mysqlfile ".
		"| perl $lib/db/mysql2schema.pl > $schemafile"
	);
	*/
	require_once('mysql2schema.php');
	print "wrote table info to $schemafile\n";
	# by default makeclasses.php won't overwrite an existing directory
	$force = true;
	require("$lib/db/makeclasses.php");
} else {
	die("can't get standard input");
}

