<?php
/**
 * default controller - customize it
 *
 * you can use the $this->actions array to select which action you want to do
 * use $this->doable(array( ...map of action to callback funcs...));
 * and $this->doaction($this->actions[1]);
 * to handle sub actions such as home/someaction
 *
 * you can display templates with the default smarty view code 
 * View::assign - to assign template variables
 * View::display - display the template
 * View::wrap - display template in a wrapper 
 */
class Home extends Controller {
	/**
	 * required function for controllers
	 */
	public function execute() {
		$this->me = Login::check();

		if (in_array('logout',$this->actions)) 
			$this->logout();

		if (is_array($this->me)) {
			$s = new MysurveysectionsModel;
			$sections = $s->getall(array(
				"where userid='%s' ".
				"order by hide,surveyid,visible desc,ord,sectionid",
				$this->me['userid']
			));
			View::addJS('survey.php');
			View::assign('sections',$sections);
			View::wrap('home.tpl');
		} else {
			View::wrap('tools/login.tpl');
		}
	}

	function logout() 
	{
		Login::logout();
		unset($this->me);
	}
}

