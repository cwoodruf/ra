<?php
require_once('vendor/autoload.php');

# Call the converter:

$phpToJavascript = new PHPToJavascript\PHPToJavascript(); 
$phpToJavascript->addFromFile($argv[1]); 
$jsOutput = $phpToJavascript->toJavascript();
print $jsOutput;

