<?php
ini_set('display_errors','stdout');
error_reporting(E_ALL & ~E_NOTICE);

require_once('dbaccess.php');

$dsn = "mysql:host=$hostname;dbname=$database";
if (isset($port)) $dsn .= ";port=$port";
$db = new PDO($dsn, $user, $password);
$mode = PDO::FETCH_ASSOC;

// used with array_map to make a list of fields to update
function mk_fields($field) 
{
	if (!preg_match('#^\w+$#', $field)) return;
	return "$field=:$field";
}
// make keys to map into field values list in query
function mk_fkey($k) 
{
	if (!preg_match('#^\w+$#', $k)) return;
	return ":$k";
}
// build field map for running a query
function mk_fmap($arr) 
{
	return array_combine(array_map('mk_fkey',array_keys($arr)), array_values($arr));
}
	

class DB 
{
	static $query;

	static function lastquery()
	{
		return self::$query;
	}

	static function query($query,$args=null) 
	{
		global $db,$mode;
		$st = $db->query(self::build_query($query,$args), $mode);
		if (!$st) die("error executing query: ".var_export($db->errorInfo(),true));
		$rows = $st->fetchAll($mode);
		return $rows;
	}
	static function ex($query,$args=null)
	{
		global $db;
		$ret = $db->exec(self::build_query($query,$args));
		if ($ret === false) var_export($db->errorInfo());
		return $ret;
	}
	static function build_query($query,$args)
	{
		global $db;
		// prepare does not seem to work
		if (is_array($args)) {
			foreach ($args as $key => $value) {
				$query = preg_replace("#$key#", $db->quote($value), $query);
			}
		} 
		self::$query = $query;
		return $query;
	}
}

