<?php
class Data extends SectionController
{
	function execute() 
	{
		header('content-type: text/plain');
		$this->me = Login::check();

		if (!is_array($this->me)) {
			print "ERROR bad signature or login ".self::r('sig');
			exit;
		}
		$this->doable = array(
			'surveys' => 'surveys',
			'survey' => 'sections',
			'surveysections' => 'sections',
			'sections' => 'sections',
			'section' => 'section',
		);
		$this->doaction();
	}

	function surveys() 
	{
		$s = new MysurveysModel;
		$surveys = $s->getall(
				array("where hide = 0 and userid='%s'",$this->me['userid']));
		print json_encode($surveys);
		exit;
	}

	function sections()
	{
		$s = new MysurveysectionsModel;
		$surveysections = $s->getall(array(
			"where visible = 1 and userid='%s' and surveyid='%u'", 
				$this->me['userid'], $this->actions[2]));
		print json_encode($surveysections);
		exit;
	}
	
	function section()
	{
		try {
			$sp = $this->_parse_section();
			print $sp->json();
		} catch (Exception $e) {
			if (!QUIET) die($e->getMessage());
		}
		exit;
	}
}
