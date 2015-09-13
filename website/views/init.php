<?php
if (!defined('VIEWDIR')) 
	define('VIEWDIR',dirname(__FILE__));

define('SMARTY_DIR',VIEWDIR.'/smarty/');

require_once(VIEWDIR.'/view.php');

View::init();
View::addCSS('ui-lightness/jquery-ui.css');
View::addCSS('main.css');
View::addJS('jquery.js');
View::addJS('jquery-ui.js');
View::addJS('sprintf.min.js');
View::addJSatEnd('ready.js');

