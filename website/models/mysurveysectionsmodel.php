<?php
class MysurveysectionsModel extends MysurveysectionsRelation
{
	function __construct()
	{
		parent::__construct();
		$this->primary = array(
			'surveyid' => '',
			'sectionid' => '',
			'userid' => '',
		);
	}
	function section2surveys($userid,$sectionid) 
	{
		return $this->getall(array(
			"where userid='%s' and sectionid='%u' order by surveyid",
			$userid,
			$sectionid
		));
	}
}
