<?php
class Test extends SectionController
{
	function execute()
	{
		$this->me = Login::check();
		if (!$this->me) {
			View::wrap('login.tpl');
			exit;
		}
		View::addJS('jquery-ui-1.10.3.custom/js/jquery-ui.js');
		View::addJS('jquery.json-2.4.min.js');
		View::addJS('jstorage.js');
		View::addJS('data.js');
		View::addJS('show.js');
		View::addJS("survey.php?survey_section={$this->actions[2]}");
		View::addCSS('surveyphp.css');
		View::addCSS('showjs.css');
		View::addCSS('jquery-ui-1.10.3.custom/css/smoothness/jquery-ui.css');
		View::addCSS('jquery-ui-1.10.3.custom/css/smoothness/jquery-ui-1.10.3.custom.css');
		$this->doable(array(
			'section' => 'showphp',
			'php' => 'showphp',
			'js' => 'showjs',
		));
		$this->doaction();
	}

	function showphp()
	{
		View::assign('sectionid',$this->actions[2]);
		require_once('survey/sectionparser.php');
		require_once('survey/htmltree.php');
		View::addCSS('showphp.css');
		try {
			$sp = $this->_parse_section();
			$ht = new HtmlTree($sp->questions);
			# View::assign('context',$sp->read);
			View::assign('questions',$sp->questions);
			View::assign('htmltree',$ht->toString());
		} catch (Exception $e) {
			View::assign('response','Error: '.$e->getMessage());
		}
		View::wrap('showphp.tpl');
	}

	function showjs()
	{
		View::assign('sectionid',$this->actions[2]);
		View::addJS('answerparser.js');
		View::addJS('showquestion.js');
		View::wrap('showjs.tpl');
	}

}
