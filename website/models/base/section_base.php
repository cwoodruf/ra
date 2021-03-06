<?php
# automatically generated by makeclasses.php

class SectionEntity extends Entity {
	function __construct() {
		parent::__construct(
			RaDB::$db, 
			# $this->schema: for building forms among other things
			array (
			  'sectionid' => 
			  array (
			    'type' => 'int',
			    'size' => '11',
			    'auto' => true,
			    'key' => true,
			    'null' => false,
			    'default' => NULL,
			    'name' => 'sectionid',
			    'extra' => 'auto_increment',
			  ),
			  'name' => 
			  array (
			    'type' => 'varchar',
			    'size' => '255',
			    'null' => true,
			    'default' => NULL,
			    'name' => 'name',
			    'extra' => '',
			  ),
			  'raw' => 
			  array (
			    'type' => 'longtext',
			    'null' => true,
			    'default' => NULL,
			    'name' => 'raw',
			    'extra' => '',
			  ),
			  'php' => 
			  array (
			    'type' => 'longtext',
			    'null' => true,
			    'default' => NULL,
			    'name' => 'php',
			    'extra' => '',
			  ),
			  'json' => 
			  array (
			    'type' => 'longtext',
			    'null' => true,
			    'default' => NULL,
			    'name' => 'json',
			    'extra' => '',
			  ),
			),
			'section'
		);
	}

}
