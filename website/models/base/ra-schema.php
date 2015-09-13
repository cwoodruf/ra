<?php
$dbhost = 'localhost';
$dbname = 'ra';
$schema = array (
  'access' => 
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
    'userid' => 
    array (
      'type' => 'varchar',
      'size' => '64',
      'key' => true,
      'null' => false,
      'default' => '',
      'name' => 'userid',
      'extra' => '',
    ),
    'role' => 
    array (
      'type' => 'varchar',
      'size' => '64',
      'null' => true,
      'default' => NULL,
      'name' => 'role',
      'extra' => '',
    ),
    'PRIMARY KEY' => 
    array (
      'surveyid' => '',
      'userid' => '',
    ),
  ),
  'mysections' => 
  array (
    'sectionid' => 
    array (
      'type' => 'int',
      'size' => '11',
      'null' => false,
      'default' => '0',
      'name' => 'sectionid',
      'extra' => '',
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
    'userid' => 
    array (
      'type' => 'varchar',
      'size' => '64',
      'null' => false,
      'default' => '',
      'name' => 'userid',
      'extra' => '',
    ),
    'role' => 
    array (
      'type' => 'varchar',
      'size' => '64',
      'null' => true,
      'default' => NULL,
      'name' => 'role',
      'extra' => '',
    ),
    'surveyhide' => 
    array (
      'type' => 'int',
      'size' => '11',
      'null' => false,
      'default' => '0',
      'name' => 'surveyhide',
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
    'ord' => 
    array (
      'type' => 'int',
      'size' => '11',
      'null' => false,
      'default' => '0',
      'name' => 'ord',
      'extra' => '',
    ),
    'PRIMARY KEY' => 
    array (
    ),
  ),
  'mysurveys' => 
  array (
    'surveyid' => 
    array (
      'type' => 'int',
      'size' => '11',
      'null' => false,
      'default' => '0',
      'name' => 'surveyid',
      'extra' => '',
    ),
    'title' => 
    array (
      'type' => 'varchar',
      'size' => '255',
      'null' => true,
      'default' => NULL,
      'name' => 'title',
      'extra' => '',
    ),
    'userid' => 
    array (
      'type' => 'varchar',
      'size' => '64',
      'null' => false,
      'default' => '',
      'name' => 'userid',
      'extra' => '',
    ),
    'role' => 
    array (
      'type' => 'varchar',
      'size' => '64',
      'null' => true,
      'default' => NULL,
      'name' => 'role',
      'extra' => '',
    ),
    'hide' => 
    array (
      'type' => 'int',
      'size' => '11',
      'null' => false,
      'default' => '0',
      'name' => 'hide',
      'extra' => '',
    ),
    'PRIMARY KEY' => 
    array (
    ),
  ),
  'mysurveysections' => 
  array (
    'surveyid' => 
    array (
      'type' => 'int',
      'size' => '11',
      'null' => false,
      'default' => '0',
      'name' => 'surveyid',
      'extra' => '',
    ),
    'title' => 
    array (
      'type' => 'varchar',
      'size' => '255',
      'null' => true,
      'default' => NULL,
      'name' => 'title',
      'extra' => '',
    ),
    'sectionid' => 
    array (
      'type' => 'int',
      'size' => '11',
      'null' => true,
      'default' => '0',
      'name' => 'sectionid',
      'extra' => '',
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
    'userid' => 
    array (
      'type' => 'varchar',
      'size' => '64',
      'null' => false,
      'default' => '',
      'name' => 'userid',
      'extra' => '',
    ),
    'role' => 
    array (
      'type' => 'varchar',
      'size' => '64',
      'null' => true,
      'default' => NULL,
      'name' => 'role',
      'extra' => '',
    ),
    'visible' => 
    array (
      'type' => 'int',
      'size' => '11',
      'null' => true,
      'default' => '0',
      'name' => 'visible',
      'extra' => '',
    ),
    'ord' => 
    array (
      'type' => 'int',
      'size' => '11',
      'null' => true,
      'default' => '0',
      'name' => 'ord',
      'extra' => '',
    ),
    'hide' => 
    array (
      'type' => 'int',
      'size' => '11',
      'null' => false,
      'default' => '0',
      'name' => 'hide',
      'extra' => '',
    ),
    'PRIMARY KEY' => 
    array (
    ),
  ),
  'saverstate' => 
  array (
    'partid' => 
    array (
      'type' => 'varchar',
      'size' => '128',
      'key' => true,
      'null' => false,
      'default' => NULL,
      'name' => 'partid',
      'extra' => '',
    ),
    'survey' => 
    array (
      'type' => 'int',
      'size' => '11',
      'key' => true,
      'null' => false,
      'default' => NULL,
      'name' => 'survey',
      'extra' => '',
    ),
    'section' => 
    array (
      'type' => 'int',
      'size' => '11',
      'key' => true,
      'null' => false,
      'default' => NULL,
      'name' => 'section',
      'extra' => '',
    ),
    'lastq' => 
    array (
      'type' => 'text',
      'null' => false,
      'default' => NULL,
      'name' => 'lastq',
      'extra' => '',
    ),
    'state' => 
    array (
      'type' => 'text',
      'null' => false,
      'default' => NULL,
      'name' => 'state',
      'extra' => '',
    ),
    'modified' => 
    array (
      'type' => 'datetime',
      'null' => true,
      'default' => NULL,
      'name' => 'modified',
      'extra' => '',
    ),
    'sent' => 
    array (
      'type' => 'datetime',
      'null' => true,
      'default' => NULL,
      'name' => 'sent',
      'extra' => '',
    ),
    'PRIMARY KEY' => 
    array (
      'partid' => '',
      'survey' => '',
      'section' => '',
    ),
  ),
  'saverstate_backup' => 
  array (
    'partid' => 
    array (
      'type' => 'varchar',
      'size' => '128',
      'key' => true,
      'null' => false,
      'default' => NULL,
      'name' => 'partid',
      'extra' => '',
    ),
    'survey' => 
    array (
      'type' => 'int',
      'size' => '11',
      'key' => true,
      'null' => false,
      'default' => NULL,
      'name' => 'survey',
      'extra' => '',
    ),
    'section' => 
    array (
      'type' => 'int',
      'size' => '11',
      'key' => true,
      'null' => false,
      'default' => NULL,
      'name' => 'section',
      'extra' => '',
    ),
    'lastq' => 
    array (
      'type' => 'text',
      'null' => false,
      'default' => NULL,
      'name' => 'lastq',
      'extra' => '',
    ),
    'state' => 
    array (
      'type' => 'text',
      'null' => false,
      'default' => NULL,
      'name' => 'state',
      'extra' => '',
    ),
    'modified' => 
    array (
      'type' => 'datetime',
      'null' => true,
      'default' => NULL,
      'name' => 'modified',
      'extra' => '',
    ),
    'sent' => 
    array (
      'type' => 'datetime',
      'null' => true,
      'default' => NULL,
      'name' => 'sent',
      'extra' => '',
    ),
    'PRIMARY KEY' => 
    array (
      'partid' => '',
      'survey' => '',
      'section' => '',
    ),
  ),
  'section' => 
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
  'survey' => 
  array (
    'surveyid' => 
    array (
      'type' => 'int',
      'size' => '11',
      'auto' => true,
      'key' => true,
      'null' => false,
      'default' => NULL,
      'name' => 'surveyid',
      'extra' => 'auto_increment',
    ),
    'title' => 
    array (
      'type' => 'varchar',
      'size' => '255',
      'null' => true,
      'default' => NULL,
      'name' => 'title',
      'extra' => '',
    ),
    'hide' => 
    array (
      'type' => 'int',
      'size' => '11',
      'null' => false,
      'default' => '0',
      'name' => 'hide',
      'extra' => '',
    ),
  ),
  'surveysection' => 
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
  'users' => 
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
);