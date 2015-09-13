<?php
class JS
{
	# used with .salt.php to make javascript versions of functions
	# probably won't work well with other files 
	static function php2js($file)
	{
		if (!is_file($file)) return;
		$php = file($file);
		foreach ($php as $line) {
			if (preg_match('#<\?php#',$line)) {
				$skip = false;
				continue;
			}
			if (preg_match('#\?>#',$line)) {
				$skip = true;
			}
			if ($skip) continue;

			if (preg_match("#define\('(\w+)',('[^']*')\)#",$line,$m)) {
				$jslines[] = "var {$m[1]} = {$m[2]};\n";
				continue;
			}
			$js = $line;
			$js = preg_replace('#\$#','',$js);
			$js = preg_replace('#\.#','+',$js);
			$js = preg_replace('/#/','//',$js);
			$js = preg_replace('#sha1#','$.sha1',$js);
			$jslines[] = $js;
		}
		return implode("", $jslines);
	}
}
