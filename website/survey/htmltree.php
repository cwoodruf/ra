<?php
require_once('survey/answerparser.php');

class HtmlTree 
{
	function __construct($qtree=null)
	{
		if (is_array($qtree)) 
			$this->qtree = $qtree;
		else if (isset($qtree)) 
			throw new Exception('HtmlTree: $qtree is not an array in __construct!');
	}

	function toString($qtree=null)
	{
		if (is_array($qtree)) 
			$this->qtree = $qtree;
		if (!is_array($this->qtree)) 
			throw new Exception('No valid questions found');
		$this->htmltree = $this->htmltree($this->qtree);
		return $this->htmltree;
	}

	function _aunset($qnum) {
		return <<<HTML
&nbsp; <a href="javascript:void(0);" onclick="aunset('$qnum');" class="unset">unset</a>

HTML;
	}

	function htmltree($qtree,$class='') {
		if (!is_array($qtree)) return;
		$html = '';
		foreach ($qtree['questions'] as $key => $q) {
			if ($this->is_section($key)) continue;
			$qnum = $q['qnum'];
			$qid = $q['id'];
			$renderedquestion = $this->render_question($q);
			if (!$renderedquestion) continue;
			$html .= <<<HTML
<a name="$qid"></a>
<div id="$qnum" class="question$class" >
<span class="qid" title="canonical id $qnum">$qid</span>:
$renderedquestion

HTML;
			if (is_array($q['answers']) and count($q['answers'])) {
				$html .= $this->_aunset($qnum);
				$selector = "#$qnum > .answers, ".
					"#$qnum .questioncontext, ".
					"#$qnum .questioncontext > .answers";
				if ($class == '') {
					$html .= <<<HTML
<a class="expand" href="javascript:void(0);" 
   onclick="$('$selector').show(); 
            $(this).next().show(); $(this).hide();">expand</a>
<a class="hide" style="display: none;"
   href="javascript:void(0);" 
   onclick="$('$selector').hide(); 
            $(this).prev().show(); $(this).hide();">hide</a>
HTML;
				}
				$html .= <<<HTML
<div class="answers $class">

HTML;
				foreach ($q['answers'] as $ord => $a) {
					$html .= "<div id=\"$qnum-a$ord\" class=\"answer $class\" >";
					$ap = new AnswerParser(
						$ord
					);
					$ap->parse($a);
					$html .= $ap->tostring();
					if (count($a['context_questions'])) {
						$html .= $this->htmltree($a['context_questions'],'context');
					}
					$html .= "</div> <!-- answer$class -->";
				}

				$html .= <<<HTML
</div> <!-- answers$class -->
HTML;
			}	
			$html .= "</div> <!-- question$class -->\n";
		}
		return $html;
	}

	function render_question($q) {
		$question = $q['question'];
		$question = str_replace("\n","<br/>\n",$question);
		$question = AnswerParser::ins_goto($question); 
		return $question;
	}

	function is_section($key) {
		return preg_match('#section#',$key);
	}
}

