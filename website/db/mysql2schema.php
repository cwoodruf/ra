<?php
ini_set('include_path',ini_get('include_path').":./db");
require_once('abstract-mysql.php');
require_once('abstract-common.php');

$m2sdb = new Entity(array(
	'host' => $dbhost,
	'login' => $myuser,
	'pw' => $mypw,
	'db' => $db,
));

$m2sdb->resultformat = 'array';
$m2sdb->run("show tables");
$tables = $m2sdb->resultarray();

foreach ($tables as $row) {
	
	$table = $row[0];

	$m2sdb->run("desc $table");
	$fields = $m2sdb->resultarray();

	$keys = array();
	$tdata = array();
	foreach ($fields as $fdata) {
		$s = array();
		if (preg_match('#(\w+)\((\d+)\)#i', $fdata['Type'],$m)) {
			$s['type']  = strtolower($m[1]);
			$s['size'] = $m[2];
		} else {
			$s['type'] = strtolower($fdata['Type']);
		}
		if ($fdata['Extra'] == 'auto_increment') {
			$s['auto'] = true;
		}
		if ($fdata['Key'] == 'PRI') {
			$s['key'] = true;
			$keys[$fdata['Field']] = '';
		}
		$s['null'] = ($fdata['Null'] == 'YES' ? true: false);
		$s['default'] = $fdata['Default'];
		$s['name'] = $fdata['Field'];
		$s['extra'] = $fdata['Extra'];
		$tdata[$s['name']] = $s;
	}
	if (count(array_keys($keys)) != 1) {
		$tdata['PRIMARY KEY'] = $keys;
	}
	$schema[$table] = $tdata;
}

$schemavar = var_export($schema,true);
$schemaphp = <<<PHP
<?php
\$dbhost = '$dbhost';
\$dbname = '$db';
\$schema = $schemavar;
PHP;

if ($schemafile) {
	$fh = fopen($schemafile,'wb');
	if (!$fh) die("can't open $schemafile");
	$ret = fwrite($fh,$schemaphp);
	fclose($fh);
	if (!$ret) die("error writing to $schemafile");
} else {
	print $schemaphp;
}

