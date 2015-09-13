<?php
# implements PW interface - using the keyword causing unexplained errors
class UsersModel extends UsersEntity
{
	function valid_login($login)
	{
		if (!$login) return false;
		return ($this->get_login($login) === false ? false : true);
	}

	function get_login($login)
	{
		$this->logintest = $login;
		return ($this->logindata = $this->getone($this->logintest));
	}

	function valid_pw($password)
	{
		return (strlen($password) > 0 ? true: false);
	}

	function encode_pw($password) 
	{
		$this->pwtest = $password;
		if ($this->logindata) {
			$this->pwencoded = sha1(
				$this->pwtest.
				$this->logindata['userid'].
				$this->logindata['sigkey']
			);
			return $this->pwencoded;
		}
	}

	function check_signature($fields,$sig)
	{
		if (!preg_match('#^\w+$#',$fields['userid'])) return false;

		if (!preg_match('#^[a-fA-F0-9]+$#',$fields['nonce'])) return false;

		$me = $this->getone($fields['userid']);
		if (!$me) return false;

		$this->signature = $this->_sig_string($me, $fields['nonce']);
		if ($this->signature != $sig) return false;

		unset($me['password']);
		return $me;
	}

	# this has to be replicated in the android app
	private function _sig_string($me,$nonce) {
		return sha1($nonce.$me['sigkey'].$me['password'].$me['userid']);
	}

	# generate some random signature for internal testing
	function gen_sig() 
	{
		$me = Login::check();
		if (!$me) return;
		$sig['userid'] = $me['userid'];
		$sig['nonce'] = sha1(mt_rand());
		$sig['sig'] = $this->_sig_string($me,$sig['nonce']);
		return $sig;
	}

	# rebuild signature key - updating this field means reencoding the pw
	function gen_sigkey($oldkey=null)
	{
		$newkey = $oldkey;
		while ($oldkey and $oldkey == $newkey) {
			$newkey = sha1(mt_rand().mt_rand());
		}
		return $newkey;
	}
}

