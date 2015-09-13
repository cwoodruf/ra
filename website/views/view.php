<?php
require_once(VIEWDIR.'/smarty/Smarty.class.php');
if (!defined('VIEWWRAPPER')) define('VIEWWRAPPER','wrapper.tpl');

# base view class
class View {
	const DEFROWS = 5;
	const DEFCOLS = 60;
	const DEFSIZE = 50;
	const MAXSIZE = 60;
	public static $smarty;
	public static $css;
	public static $js;
	public static $css_at_end;
	public static $js_at_end;
	public static $cssfiles;
	public static $jsfiles;
	public static $tplext;

	public static function init($ext='tpl') {
		$smarty = new Smarty();
		$smarty->template_dir = VIEWDIR.'/templates';
		$smarty->compile_dir = VIEWDIR.'/templates_c';
		$smarty->cache_dir = VIEWDIR.'/cache';
		$smarty->plugins_dir[] = VIEWDIR.'/plugins';
		$smarty->plugins_dir[] = dirname(__FILE__).'/smarty/plugins';
		self::$smarty = $smarty;
		self::$tplext = $ext;
	}
	
	public static function assign($name,$value) {
		return self::$smarty->assign($name,$value);
	}

	public static function set($name,$value) {
		return self::$smarty->assign($name,$value);
	}


	public static function wrap($tpl,$wrapper=VIEWWRAPPER) {
		if (self::$smarty->template_exists($wrapper)) {
			self::assign('content',self::fetch($tpl));
			self::display($wrapper);
		} else {
			self::display($tpl);
		}
	}

	public static function display($tpl) {
		self::fixname($tpl);
		return self::$smarty->display($tpl);
	}
	
	public static function fetch($tpl) {
		self::fixname($tpl);
		return self::$smarty->fetch($tpl);
	}

	public static function fixname(&$tpl) {
		if (preg_match("#\.".self::$tplext."#",$tpl)) return;
		$tpl .= ".".self::$tplext;
	}

	public static function addCSS($css, &$cssstr=false) {
		if (preg_match('#(.*)(\?.*)#',$css,$m)) {
			$css = $m[1];
			$querystring = $m[2];
		}
		if (!@filetype($css)) $css = "views/css/$css";
		if (!@filetype($css)) return;
		if (self::$cssfiles[$css]) return;
		self::$cssfiles[$css] = true;
		$sitedir = Controller::sitedir();
		$newcss = "<link rel=\"stylesheet\" type=\"text/css\" ".
			"href=\"$sitedir/$css$querystring\">\n";
		if ($cssstr !== false) $cssstr .= $newcss;
		else self::$css .= $newcss;
	}

	public static function addCSSatEnd($css) {
		if (!isset(self::$css_at_end)) 
			self::$css_at_end = "<!-- added after -->";
		self::addCSSatEnd($css, self::$css_at_end);
	}

	public static function addJS($js, &$jsstr=false) {
		if (preg_match('#(.*)(\?.*)#',$js,$m)) {
			$js = $m[1];
			$querystring = $m[2];
		}
		if (!@filetype($js)) $js = "views/js/$js";
		if (!@filetype($js)) return;
		if (self::$jsfiles[$js]) return;
		self::$jsfiles[$js] = true;
		$sitedir = Controller::sitedir();
		$newjs = "<script type=\"text/javascript\" ".
			"src=\"$sitedir/$js$querystring\" ></script>\n";
		if ($jsstr !== false) $jsstr .= $newjs;
		else self::$js .= $newjs;
	}

	public static function addJSatEnd($js) {
		if (!isset(self::$js_at_end)) 
			self::$js_at_end = "<!-- added after -->";
		self::addJS($js, self::$js_at_end);
	}
}

