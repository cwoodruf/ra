<?php
# print a tree version of a questionnaire section
if (empty($sectiontag)) {
	foreach ($sections as $ord => $s) {
		$renderedsection = render_section($sections,$sectiontag,$ord);
		print <<<HTML
<div id="$sectiontag" class="section">
$renderedsection
</div>

HTML;
	}
}

$sectiontag = strtolower($_REQUEST['sectiontag']);
if (!preg_match('#^\w+$#', $sectiontag)) die("invalid sectiontag $sectiontag");
require_once("sections/section_{$sectiontag}.php");
require_once('answerparser.php');

// $renderedsection = render_section($sections,$sectiontag);
$renderedsection = $questions['sectionname'];
$sectiontag = $questions['sectiontag'];

print <<<HTML
<div id="$sectiontag" class="section">
$renderedsection
<a class="expandall" href="javascript:void(0);" 
   onclick="$('.answers').show(); $('.expand').hide(); $('.hide').show();">expand all</a>
<a class="hideall" href="javascript:void(0);" 
   onclick="$('.answers').hide(); $('.expand').show(); $('.hide').hide();">hide all</a>
<a class="hideall expandall" href="javascript:void(0);" onclick="aunsetall();">unset all answers</a>

HTML;

print_questions($questions);

print <<<HTML
</div> <!-- section -->
HTML;

function print_questions($questions,$class='') {
	if (!is_array($questions)) return;
	$html = '';
	foreach ($questions as $key => $q) {
		if (is_section($key)) continue;
		$qid = $q['id'];
		$renderedquestion = render_question($q);
		if (!$renderedquestion) continue;
		print <<<HTML
<div id="$qid" class="question$class" title="$qid">
$renderedquestion

HTML;
		if (is_array($q['answers']) and count($q['answers'])) {
			if ($class == '') {
				print <<<HTML
<a class="expand" href="javascript:void(0);" 
   onclick="$('#$qid > .answers').show(); 
            $(this).next().show(); $(this).hide();">expand</a>
<a class="hide" href="javascript:void(0);" 
   onclick="$('#$qid > .answers').hide(); 
            $(this).prev().show(); $(this).hide();">hide</a>
HTML;
			}
			print <<<HTML
<div class="answers $class">

HTML;
			if (count($q['answers'])) {
				print <<<HTML
<a href="javascript:void(0);" onclick="aunset('$qid');" class="unset">unset</a>

HTML;
			}
			foreach ($q['answers'] as $ord => $a) {
				print "<div id=\"$qid-a$ord\" class=\"answer $class\" title=\"$class $qid\">";
				$ap = new AnswerParser(
					$ord
				);
				$ap->parse($a);
				print $ap->tostring();
				if (count($a['context_questions'])) {
					print_questions($a['context_questions'],'context');
				}
				print "</div> <!-- answer$class -->";
			}

			if (count($q['answers'])) {
				print <<<HTML
<a href="javascript:void(0);" onclick="aunset('$qid');" class="unset">unset</a>

HTML;
			}
			print <<<HTML
</div> <!-- answers$class -->
HTML;
		}	
		print "</div> <!-- question$class -->\n";
	}
}

# may want to print out all - otherwise should turn $sections into a hash
function render_section($sections,$sectiontag,$ord=null) {
	global $survey;
	$html = "";
	if (isset($ord) and isset($sections[$ord])) {
		$s = $sections[$ord];
		$html .= <<<HTML
<a href="?surveyid=$survey&sectiontag={$s['sectiontag']}">
{$s['ordinal']}. {$s['sectionname']} ({$s['sectiontag']})</a>

HTML;
	}
	if (!is_array($sections)) return $html;
	foreach ($sections as $ord => $s) {
		if ($s['sectiontag'] == $sectiontag) {
			$html .= "{$s['ordinal']}. {$s['sectionname']} ({$s['sectiontag']})\n";;
		}
	}
	return $html;
}

function render_question($q) {
	return str_replace("\n","<br/>\n",$q['question']);
}

function is_section($key) {
	return preg_match('#section#',$key);
}

