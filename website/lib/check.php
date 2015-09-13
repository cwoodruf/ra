<?php
/*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*/

/**
 * utility functions to check input - not directly related to dbabstracter
 */ 
class Check {
	private static $error;
	public static $emptyok = false;
	public static $rules = array(
		'isvar' => '(letters, numbers or _ only)',
		'isemail' => '(letters, numbers, _ - and . only)',
		'isphone' => '(must have at least 10 digits)',
		'isdate' => '(YYYY-MM-DD)',
		'istime' => '(HH:MM:SS)',
		'isdatetime' => '(YYYY-MM-DD HH:MM:SS)',
		'validtime' => '(hours 0-23 minutes 0-59 seconds 0-59)',
		'digits' => '(numbers 0 - 9 only)',
		'ismd5' => '(32 characters 0-9 and a-f only)',
		'issha1' => '(40 characters 0-9 and a-f only)',
	);

	public static function rule($func) {
		return self::$rules[$func];
	}

	/**
	 * this function allows us to override the $emptyok default with 
	 * the static variables value so we don't have to put "false" in 
	 * every function call
	 */
	private static function emptyok($emptyok) {
		if ($emptyok === null) return self::$emptyok;
		return $emptyok;
	}

	public static function notempty($s) {
		if (empty($s)) return false;
		return true;
	}

	public static function isvar($s,$emptyok=null) {
		if (self::emptyok($emptyok)) return preg_match('#^\w*$#', $s);
		return preg_match('#^\w+$#', $s);
	}
	public static function isemail($s,$emptyok=null) {
		if (self::emptyok($emptyok) and empty($s)) return true;
		$s = trim($s);
		return preg_match('#^\w[\w\.\-]*@\w[\w\.\-]*\.\w+$#', $s);
	}
	public static function isphone($s,$emptyok=null) {
		if (self::emptyok($emptyok) and empty($s)) return true;
		$s = trim($s);
		$num = preg_replace('#\D#','', $s);
		if (strlen($num) < 10) return false;
		return true;
	}
	public static function isdate($s,$emptyok=null) {
		if (self::emptyok($emptyok) and empty($s)) return true;
		if (preg_match('#^(\d{4})-(\d{2})-(\d{2})#', $s, $m)) {
			return checkdate($m[2],$m[3],$m[1]);
		}
		return false;
	}
	public static function istime($s,$emptyok=null) {
		if (self::emptyok($emptyok) and empty($s)) return true;
		if (preg_match('#^(\d{2})\:(\d{2})(?:\:(\d{2})|)$#', $s, $m)) {
			if (self::validtime($m[1],$m[2],$m[3])) return true;
		}
		return false;
	}
	public static function isdatetime($s,$emptyok=null) {
		if (self::emptyok($emptyok) and empty($s)) return true;
		if (!preg_match('#^(\d{4})-(\d{2})-(\d{2}) (\d{2})\:(\d{2})(?:\:(\d{2})|)#', $s, $m)) 
			return false;
		if (!checkdate($m[2],$m[3],$m[1])) return false;
		if (!self::validtime($m[4],$m[5],$m[6])) return false;
		return true;
	}
	public static function validtime($hour,$minute,$second) {
		if ($hour < 0 or $hour > 23) return false;
		if ($minute < 0 or $minute > 59) return false;
		if ($second) {
			if ($second < 0 or $second > 59) return false;
		}
		return true;
	}
	public static function order($start,$end) {
		if ($start > $end) return array($end, $start);
		return array($start, $end);
	}
	public static function digits($s,$emptyok=null) {
		if (self::emptyok($emptyok)) return preg_match('#^\d*$#', $s);
		return preg_match('#^\d+$#', $s);
	}
	public static function checkhash($hash,$len) {
		return preg_match("#^[a-f0-9]{$len}$#",$hash);
	}
	public static function ismd5($hash) {
		return self::checkhash($hash,32);
	}
	public static function issha1($hash) {
		return preg_match("#^[a-f0-9]{40}$#",$hash);
		return self::checkhash($hash,40);
	}
	public static function validpassword($pw) {
		$rules = "passwords should be 6 or more characters and not contain spaces";
		if (strlen($pw) < 6 or preg_match('#\s#', $pw)) {
			self::err($rules);
			return false;
		}
		return true;
	}
	public static function err($msg=null) {
		if (!empty($msg)) self::$error = $msg;
		return self::$error;
	}
}

