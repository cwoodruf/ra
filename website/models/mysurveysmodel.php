<?php
class MysurveysModel extends MysurveysRelation
{
	function __construct()
	{
		parent::__construct();
		$this->primary = array(
			'surveyid' => '',
			'userid' => '',
		);
		$this->selector = null;
	}

	function selector($userid,$surveyid) 
	{
		$this->selector = array(
			"where userid='%s' and surveyid='%u'", 
			$userid, 
			$surveyid
		);
		return $this->selector;
	}
}
