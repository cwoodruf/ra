<?php
class MysectionsModel extends MysectionsRelation
{
	function __construct()
	{
		parent::__construct();
		$this->primary = array(
			'sectionid' => '',
			'userid' => '',
		);
	}
	
}
