<?php
# automatically generated by makeclasses.php

class UsersEntity extends Entity {
	function __construct() {
		parent::__construct(
			RaDB::$db, 
			# $this->schema: for building forms among other things
			array (
			  'userid' => 
			  array (
			    'type' => 'varchar',
			    'size' => '64',
			    'key' => true,
			    'null' => false,
			    'default' => NULL,
			    'name' => 'userid',
			    'extra' => '',
			  ),
			  'email' => 
			  array (
			    'type' => 'varchar',
			    'size' => '128',
			    'null' => true,
			    'default' => NULL,
			    'name' => 'email',
			    'extra' => '',
			  ),
			  'password' => 
			  array (
			    'type' => 'varchar',
			    'size' => '64',
			    'null' => true,
			    'default' => NULL,
			    'name' => 'password',
			    'extra' => '',
			  ),
			  'sigkey' => 
			  array (
			    'type' => 'varchar',
			    'size' => '64',
			    'null' => true,
			    'default' => NULL,
			    'name' => 'sigkey',
			    'extra' => '',
			  ),
			),
			'users'
		);
	}

}
