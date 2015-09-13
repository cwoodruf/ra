<?php
/**
 * Controller to handle user related tasks 
 */
class Profile extends Controller {
	public function execute() {

		$this->me = Login::check();
		if (!is_array($this->me)) return;

		$this->doable = array(
			'getkey' => 'getkey',
			'resetkey' => 'resetkey',
		);
		View::addJS('survey.php');
		$this->doaction();
	}

	function getkey() {
		header("content-type: text/plain");
		print $this->me['sigkey'];
		exit;
	}

	function resetkey() {
		View::assign('pwfield',PWFIELD);

		if (!isset($_REQUEST[PWFIELD])) {
			View::wrap('resetkey.tpl');
			return;
		}

		$u = new UsersModel;
		$ldata = $u->get_login($this->me['userid']);
		$encoded = $u->encode_pw($_REQUEST[PWFIELD]);

		if ($encoded == $ldata['password']) {

			$newkey = $u->gen_sigkey($ldata['sigkey']);
			$r = $u->upd($ldata['userid'],array('sigkey'=>$newkey));

			if (!$r) {
				View::assign('response',
					"Error saving new signature key $newkey:<br>".
						$u->err()."<br>".$u->query());
			} else {
				// at this point our session data will be out of sync
				$this->me['sigkey'] = $newkey;
				$ldata = $u->get_login($this->me['userid']);

				$pw = $_REQUEST[PWFIELD];
				$newpw = $_REQUEST['newpw'];
				if ($newpw) {
					if ($u->valid_pw($newpw)) {
						if ($newpw == $_REQUEST['newpw2']) {
							$pw = $newpw;
						} else {
							$npwmsg = "Error: new passwords don't match.";
						}
					} else {
						$npwmsg = "Error: invalid new pw.";
					}
				}

				$reencoded = $u->encode_pw($pw);
				$r = $u->upd($ldata['userid'],array('password'=>$reencoded));
				if (!$r) {
					View::assign(
						'response', 
						"$npwmsg Error saving new pw for new signature key $newkey: ".
						$u->err()
					);
				} else {
					View::assign('response', "$npwmsg New signature key: $newkey");
				}
			}
		} else {
			View::assign('response',"Error: invalid password");
		}
		View::wrap('resetkey.tpl');
	}
}

