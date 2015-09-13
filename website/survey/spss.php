<?php
# build spss syntax output that can be imported directly into the program
class SPSS 
{
	public function __construct($sectionparser) 
	{
		$this->section = $sectionid;
		require_once('survey/answerparser.php');
		$this->ap = new AnswerParser(null);
		# get parsed section data as reference see controllers/sectioncontroller.php
		$this->sp = $sectionparser;
	}

	public function print_cases($cases, $delim) 
	{
		$this->_qlist($this->sp->questions);
		$row = array();
		foreach ($this->ids as $id => $setzero) {
			$row[] = $id;
		}
		print implode($delim,$row)."\r\n";
		foreach ($cases as $i => $row) {
			$rawcase = $row['state'];
			$case = json_decode($rawcase);
			if (!is_object($case)) continue;
			$data = array();
			foreach ($case as $qnum => $responses) {
				if (!is_object($responses)) continue;
				foreach ($responses as $idx => $val) {
					$id = $this->qlist[$qnum][$val]['id'];
					if (isset($id)) {
						$data[$id] = 1;
					} else {
						$id = $this->qlist[$qnum]['id'];
						if (isset($id)) {
							$data[$id] = $val;
						}
					}
				}
			}
			$row = array();
			foreach ($this->ids as $id => $setzero) {
				$datum = $data[$id];
				if (is_numeric($datum)) {
					$row[] = $datum;
				} else if (strlen($datum) == 0) {
					# spss will just drop fields with no data in them
					if ($setzero) $row[] = 0;
					else if (preg_match('#num#', $this->widgets[$id])) $row[] = '0';
					else $row[] = "''";
				} else {
					$datum = preg_replace("#'#","\\'", $datum);
					$datum = preg_replace("#\n#","\\n", $datum);
					$datum = preg_replace("#\r#","\\r", $datum);
					$row[] = "'$datum'";
				}
			}
			print implode($delim,$row)."\r\n";
		}
	}

	private function _qlist($qtree) {
		if (!is_array($qtree['questions'])) return;
		foreach ($qtree['questions'] as $i => $q) {
			if (!is_array($q['answers'])) continue;
			foreach ($q['answers'] as $j => $a) {
				$id = $this->_mk_id($q,$a,$j);
				$val = $this->ap->value;
				if (isset($val)) {
					$this->qlist[$q['qnum']][$val] = array('id'=>$id);
					$this->ids[$id] = true;
				} else {
					$this->qlist[$q['qnum']] = array('id'=>$id);
					$this->ids[$id] = false;
				}
				$this->_qlist($a['context_questions']);
			}
		}
	}

	private function _mk_id($q,$a,$j,&$question=null) 
	{
		$this->ap->parse($a, $j);
		if (preg_match('#^(checkbox|radio)$#', $this->ap->widget)) {
			$val = $this->ap->value;
			$id = $q['id'].'__'.preg_replace('#\s#','_',$val);
			if (isset($question)) 
				$question .= " (".trim($a['answer']).")";
		} else {
			$id = $q['id'];
		}
		$this->widgets[$id] = $this->ap->widget;
		return  $id;
	}

	public function labels() 
	{
		print <<<TXT
GET TRANSLATE FILE="c:/downloaded/TAB/file/path/here"
  /TYPE=TAB
  /FIELDNAMES.

VARIABLE LABELS 
TXT;
		$this->_dump_meta($this->sp->questions);
		print ".\n";
	}

	private function _dump_meta($qtree) 
	{
		if (!is_array($qtree['questions'])) return;
		foreach ($qtree['questions'] as $i => $q) {
			if (!is_array($q['answers'])) continue;
			foreach ($q['answers'] as $j => $a) {
				$question = trim($q['question']);
				$id = $this->_mk_id($q,$a,$j,$question);
				$question = trim(preg_replace('#"#','\"',preg_replace("[\r\n]",'\\\\n',$question)));
				print "  $id \"$question\"\n"; 
				$this->_dump_meta($a['context_questions']);
			}
		}
	}
}

