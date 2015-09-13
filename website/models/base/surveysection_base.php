<?php
# automatically generated by makeclasses.php

class SurveysectionRelation extends Relation {
	function __construct() {
		parent::__construct(
			RaDB::$db, 
			# $this->schema: for building forms among other things
			array (
			  'surveyid' => 
			  array (
			    'type' => 'int',
			    'size' => '11',
			    'key' => true,
			    'null' => false,
			    'default' => '0',
			    'name' => 'surveyid',
			    'extra' => '',
			  ),
			  'sectionid' => 
			  array (
			    'type' => 'int',
			    'size' => '11',
			    'key' => true,
			    'null' => false,
			    'default' => '0',
			    'name' => 'sectionid',
			    'extra' => '',
			  ),
			  'ord' => 
			  array (
			    'type' => 'int',
			    'size' => '11',
			    'null' => false,
			    'default' => '0',
			    'name' => 'ord',
			    'extra' => '',
			  ),
			  'visible' => 
			  array (
			    'type' => 'int',
			    'size' => '11',
			    'null' => false,
			    'default' => '0',
			    'name' => 'visible',
			    'extra' => '',
			  ),
			  'PRIMARY KEY' => 
			  array (
			    'surveyid' => '',
			    'sectionid' => '',
			  ),
			),
			'surveysection'
		);
	}

}
