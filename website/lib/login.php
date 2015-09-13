<?php
session_start();

/** 
 * class to handle logging in, to use it make a table to hold login data
 * and build a model with db/buildmodels.php for the table
 * the class should implement the PW interface in lib/pw.php
 * by default this is whatever LOGINCLASS is
 */
class Login {

	public static $pwhashvalid = 30;
	private static $ldata;
	private static $errors;
	private static $pwclass;
	private static $pw;

	public static function logout() {
		self::$ldata = null;
		unset($_SESSION[LOGINSESSION]);
		unset($_COOKIE['from']);
		setcookie('from',null,mktime(0,0,0,1,1,1970));
	}

	public static function check() {
		$me = self::authenticate();
		if (!$me) $me = Login::checksig();
		return $me;
	}

	private static function pwinstance() {
		self::$pwclass = LOGINMODEL;
		if (!is_object(self::$pw)) {
			self::$pw = new self::$pwclass;
		}
		return self::$pw;
	}

	private static function authenticate() {
		# if already logged in then 
		if (is_array($_SESSION[LOGINSESSION]['ldata'])) 
			return $_SESSION[LOGINSESSION]['ldata'];

		$pw = self::pwinstance();

		$login = isset($_REQUEST[LOGINFIELD]) ? $_REQUEST[LOGINFIELD] : '';
		if (!$pw->valid_login($login)) return;

		$password = $_REQUEST[PWFIELD];

		if (!$pw->valid_pw($password)) return;
		$ldata = $pw->get_login($login);
		$password = $pw->encode_pw($password);

		$loginok = ($ldata[PWDBFIELD] === $password) ? true : false;

		if ($loginok) {
			self::save_login($login,$ldata);
			return $ldata;
		}
		return;
	}

	# see the smarty plugins function.*.php with the same name for perm(s) and allowed
	# permissions are set via templates using the perm and setadmin plugins
	public static function perm($action,$perms) {
		if (!isset($_SESSION[LOGINSESSION])) return;
		$permlist = explode('|',$perms);
		foreach ($permlist as $perm) {
			$_SESSION[LOGINSESSION]['perms'][$action][$perm] = true;
		} 
	}

	public static function perms() {
		return $_SESSION[LOGINSESSION]['perms'];
	}

	public static function setadmin($isadmin=true) {
		if (!isset($_SESSION[LOGINSESSION])) return;
		$_SESSION[LOGINSESSION]['isadmin'] = $isadmin;
	}

	public static function allowed($action,$perm) {
		if ($_SESSION[LOGINSESSION]['isadmin']) return true;
		if (is_array($action)) $action = implode('/', $action);
		if ($_SESSION[LOGINSESSION]['perms'][$action][$perm]) return true;
		return false;
	}
		
	public static function encode($password) {
		$pw = self::pwinstance();
		$encoded = $pw->encode_pw($password);
		return $encoded;
	}

	/**
	 * convenience method for checking the password data in 
	 * tools/passwordform.tpl
	 */
	public static function getpw() {
		self::err(null,true);
		$ldata = Login::authenticate();
		if (!is_array($ldata)) {
			self::err("you are not logged in!");
			return false;
		}
		$pw = self::pwinstance();
		$me = $pw->getone($ldata['login']);
		if ($me['password'] != $pw->encode_pw($_REQUEST['old_password'])) {
			self::err("old password was wrong!");
		}
		if (!Check::validpassword($newpw=$_REQUEST['new_password'])) {
			self::err("invalid password ".Check::err());
		}
		if ($newpw == $_REQUEST['old_password']) {
			self::err("new password same as old password - aborting");
			return false;
		}
		if ($newpw != $_REQUEST['confirm_password']) {
			self::err("new passwords don't match!");
		}
		if (count(self::err())) {
			return false;
		}
		return $pw->encode_pw($newpw);
	}
	
	public static function save_login($this_login,$ldata) {
		unset($ldata[PWDBFIELD]);
		self::$ldata = $ldata;
		$_SESSION[LOGINSESSION]['ldata'] = $ldata;
		$_SESSION[LOGINSESSION]['login'] = $this_login;
		$_SESSION[LOGINSESSION]['time'] = $time = time();
		return $_SESSION[LOGINSESSION];
	}

	public static function refresh($login=null) {
		$ldata = self::check();
		if (!$ldata) return;
		if (!$login) $login = $ldata['login'];
		$newldata = Run::me(LOGINMODEL,'get_login',$login);
		if ($newldata) {
			Login::save_login($login, $newldata);
		}
		return $newldata;
	}
	
	public static function err($error=null,$refresh=false) {
		if ($refresh) self::$errors = array();
		if (isset($error)) self::$errors[] = $error;
		return self::$errors;
	}

	public function pwhashdata() 
	{
		$pwhash = self::pwhash();
		return array('pwhash' => $pwhash, 'pwhashvalid' => (time() + self::$pwhashvalid * 86400));
	}

	public function pwhash()
	{
		return sha1(rand().time().SALT);
	}

	# signature checking interface
	public static function checksig()
	{
		$pw = self::pwinstance();
		return $pw->check_signature($_REQUEST,$_REQUEST[SIGFIELD]);
	}
}
