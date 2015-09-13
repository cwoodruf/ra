<?php
# make an editable version of a questionnaire using mediawiki-like
# syntax
header('content-type: text/plain');
require_once('sectionparser.php');
// require_once('answerparser.php');

/*
require_once('sectionloader.php');
$questions['sectionname'] = render_section($sections,$sectiontag);
$questions['sectiontag'] = $sectiontag;

// var_export($questions);
$wrap = true;
$sectiontext = "s.{$questions['sectiontag']} {$questions['sectionname']}";
$sectiontext .= print_questions($questions);
print <<<HTML
$sectiontext
HTML;
*/
$section = strtolower($argv[2]);
if (preg_match('#^(?:pio|s)$#', $section)) {
	$sectiontext = file_get_contents("sections/section_$section.txt");

	$sp = new SectionParser;
	$sp->parse($sectiontext);
	$raw = $sectiontext;
	$php = var_export($sp->questions,true);
	$json = json_encode($sp->questions);
	file_put_contents('/tmp/raw',$raw);
	file_put_contents('/tmp/php',$php);
	file_put_contents('/tmp/json',$json);
	require_once('db.php');
	DB::ex("insert into section (`name`) values (:name)",
		array(':name'=>$sp->questions['sectionname']));
#	$rows = DB::query("select last_insert_id()");
#	var_export($rows);
}
/*
$wrap = false;
$sptext = print_questions($sp->questions);
print <<<HTML
s.{$sp->questions['sectiontag']} {$sp->questions['sectionname']}
$sptext
HTML;
*/

function print_questions($questions,$class='',$indent='') {
	$html = '';

	if (!is_array($questions)) return;
	foreach ($questions as $key => $q) {
		if (preg_match('#^section#',$key)) continue;
		$qid = $q['id'];
		$renderedquestion = render_question($q);
		if ($renderedquestion == '') continue;
		$html .= <<<HTML
$indent{$class}q. $renderedquestion

HTML;
		if (is_array($q['answers']) and count($q['answers'])) {
			$html .= "{$indent}a.\n";
			foreach ($q['answers'] as $ord => $a) {
				$renderedanswer = render_answer($a);
				$html .= <<<HTML
{$indent}$renderedanswer

HTML;
				$html .= print_questions($a['context_questions'],'c',$indent."\t");
			}
		}	
		$html .= "$indent.\n\n\n";
	}
	return $html;
}

# may want to print out all - otherwise should turn $sections into a hash
function render_section($sections,$sectiontag) {
	$html = "";
	foreach ($sections as $ord => $s) {
		if ($s['sectiontag'] == $sectiontag) {
			$html .= "{$s['sectionname']}\n";
		}
	}
	return $html;
}

function render_question($q) {
	global $wrap;
	if ($wrap) return wordwrap($q['question']);
	return $q['question'];
}

# make the answer into a form element - or something we can edit?
function render_answer($a) {
	return $a['answer'];
}

