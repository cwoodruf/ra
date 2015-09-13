<?php
class Loginform extends Controller {
	public function execute () 
	{
		$this->doable = array(
			'ajaxcheck' => 'ajaxcheck',
			'logout' => 'logout',
			'default' => 'logout',
		);
		$this->doaction();
		$ldata = Login::check();

		if (is_array($ldata)) {
			$_REQUEST['action'] = '';
			require('index.php');
			return;
		}

		$this->flag('login',true);
		View::wrap('tools/login.tpl');
	}

	# ajaxcheck is the quick check done in the background from the login form
	function ajaxcheck() 
	{
		$ldata = Login::check();
		if (is_array($ldata)) {
			print 'OK';
			exit;
		} else {
			if (!QUIET) print ' Not found / bad password. '; 
			exit;
		}
	}

	function logout() 
	{
		Login::logout();
	}
}

