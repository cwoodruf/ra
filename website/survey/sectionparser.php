<?php
# read in a blob of text and make a php array of questions and answers out of it
# language spec:
/*
s.   - section name - only one section per blob 
       adding a second s. simply renames the section
q.   - top level question
a.   - answer block (see surveylanguage.txt for description of answer sytax)
       answers are not parsed here but stored as is
cq.  - context question (shows up as part of an answer)
.    - end of a question
=\w+ (\S+)* 
     - macro with optional arguments
=    - end of macro

See the surveylanguage.txt file in this directory for more details.

*/
# assumes function json_encode exists

class SectionParser 
{
	function __construct($blob=null) 
	{
		$this->blob = $blob;
		$this->question = 0;
	}

	# scans text input then processes questions
	function parse($blob=null,$refresh=false) 
	{
		if (isset($blob) and $blob != $this->blob) {
			$this->vars = array();
			$this->lines = array();
			$this->questions = array();
			$this->parsed = false;
			$this->blob = $blob;
		} else {
			$blob = $this->blob;
		}

		if (!$refresh) {
			if ($this->parsed) return;
		}

		# check for mac format input
		if (!preg_match('#\n#', $blob) and preg_match('#\r#', $blob)) {
			$blob = preg_replace('#\r#',"\n", $blob);
		}

		# filter the input - may also want to fiddle with character encoding here
		foreach (explode("\n", $blob) as $line) {
			# remove c++ like comments
			if (preg_match('#//#', $line)) {
				$line = preg_replace('#//.*#', '', $line);
				if (!$line) continue;
			}
			# remove c like comments
			if (preg_match('#/\*.*\*/#',$line)) {
				$line = preg_replace('#/\*.*\*/#','', $line);
				if (!$line) continue;
			}
			else if (preg_match('#/\*#',$line)) {
				$line = preg_replace('#/\*.*#','', $line);
				if ($line) $this->lines[] = $line;
				$ccomment = true;
				if (!$line) continue;
			}
			else if (preg_match('#\*/#',$line)) {
				$line = preg_replace('#.*\*/#','', $line);
				$ccomment = false;
				if (!$line) continue;
			}
			if ($ccomment) continue;
			# note we keep blank lines
			$this->lines[] = $line;
		}

		# scan for variables
		# variables can contain other variables
		# variables are wiped if they are defined later
		# variables can be defined after they are used
		foreach ($this->lines as $line) {
			if (preg_match('/^\s*=(\w+)(.*)/', $line, $m)) {
				$var = $m[1];
				$varstack[] = $var;
				$this->vars[$var] = array();
				$args = trim($m[2]);
				if (preg_match('#\S#',$args)) {
					$this->args[$var] = explode(' ',$args);
				}
				continue;
			} else if (preg_match('#^\s*=\s*$#', $line)) {
				array_pop($varstack);
				$var = $varstack[count($varstack)-1];
				continue;
			}
			if ($var) {
				# no blank lines at the start of a variable
				if (!isset($this->vars[$var]) and empty($line)) 
					continue;

				# avoid variables invoking themselves
				if (preg_match("#^\s*$var\s*$#", $line)) {
					$line = preg_replace(
						"#$var#",
						"RECURSION:$var", 
						$line
					);
				}
				$this->vars[$var][] = $line;
			} else {
				$this->qlines[] = $line;
			}
		}
		# now parse the questions
		try {
			$this->_parse_questions($this->questions);
			$this->parsed = true;
		} catch (Exception $e) {
			die ($e->getMessage()."\n");
		}
	}

	# build up an array of arrays of questions and answers
	# remember answers can have context questions related to them
	# _set_answer calls _parse_questions to find context questions 
	# for an answer
	private function _parse_questions(&$qtree, $contextual=false, $baseid='') 
	{
		$firstline = true;
		while (count($this->qlines)) {
			$this->lcount++;
			$line = array_shift($this->qlines);
			$this->read .= "{$this->lcount} {$this->_dump_qstack()} $line\n";

			# look for predefined variables
			if ($this->_ins_var($line)) {
				continue;
			}

			# if we have been called recursively the first thing we 
			# should see is the first context question
			if ($firstline) {
				if ($contextual) {
					$this->_trim_blanks();
					if (!$this->_is_cq($line)) {
						array_unshift($this->qlines, $line);
						return;
					}
				}
				$firstline = false;
			}

			# section title
			if (preg_match('#^\s*s\.(\w*)(.*)#i', $line, $m)) {
				# bounce out if we are back to the top level
				if ($contextual) {
					array_unshift($this->qlines, $line);
					return;
				}
				$qtree['sectiontag'] = $m[1];
				$qtree['sectionname'] = trim($m[2]);
			}

			# top level question q. or context question cq.
			if (preg_match('#^\s*(c?q)\.(\w*)(.*)#i', $line, $m)) {
				$qtype = strtolower($m[1]);
				if ($qtype == 'q') $baseid = '';
				# bounce out if we are back to the top level
				if ($contextual and $qtype == 'q') {
					array_unshift($this->qlines, $line);
					return;
				}
				$qtree['qcount']++;
				$qcount = $qtree['qcount'];
				# replace current question on the stack of questions
				$this->qstack[count($this->qstack)] = "q$qcount";
				$this->_append_question($qtree['questions'][$qcount]['question'], $m[3]);
				$qid = $qtree['questions'][$qcount]['id'] = 
					empty($m[2]) ? $this->_dump_qstack($baseid): ($baseid=$m[2]);

				if ($this->seen[$qid]) {
					throw new Exception(
						"Error at $line: Question ID $qid seen before"
					);
				}

				$this->seen[$qid] = true;
				$qtree['questions'][$qcount]['qnum'] = $this->_dump_qstack();
				continue;
			}

			# end of a question block for q. or cq. with a line starting with a .
			# ignore anything after the .
			if (preg_match('#^\s*\.#', $line)) {
				if (!isset($qcount)) {
					// throw new Exception("{$this->context()}\nERROR: No qcount at "."\n");
					continue;
				}
				if (!isset($qtree['questions'][$qcount]['question'])) {
					// throw new Exception("{$this->context()}\nERROR: No question at "."\n");
					continue;
				}
				array_pop($this->qstack);
				if ($contextual) {
					$this->_trim_blanks();
					if (!$this->_is_cq($this->qlines[0])) {
						return;
					}
				} else {
					$baseid = '';
				}
				$qcount = str_replace('q','',$this->qstack[count($this->qstack)-1]);
				continue;
			}

			# answer block a.
			if (preg_match('#^\s*a\.(.*)#', $line, $m)) {
				if (!isset($qtree['questions'][$qcount])) {
					throw new Exception(
						"\n{$this->context()}\n".
						"ERROR: ".$this->_dump_qstack().
						" qcount $qcount: Answer without a question!\n"
					);
				}
				# make a new set of answers only if the array really doesn't exist
				# ie ignore extra a. in answers
				if (!isset($qtree['questions'][$qcount]['answers'])) {
					$qtree['questions'][$qcount]['acount'] = 0;
					$qtree['questions'][$qcount]['answers'] = array();
				}
				if (!empty($m[1])) {
					$this->_set_answer($qtree, $qcount, $m[1], $baseid);
				}
				continue;
			}

			# add an answer or more lines to a question
			# answers are assumed to be on a single line
			if (isset($qcount)) {
				if (isset($qtree['questions'][$qcount]['answers'])) {
					$this->_set_answer($qtree, $qcount, $line, $baseid);
				} else if (isset($qtree['questions'][$qcount]['question'])) {
					$this->_append_question($qtree['questions'][$qcount]['question'], $line);
				}
			}
		}
	}
	
	# show debug info
	function context() 
	{
		return substr($this->read, -1024);
	}

	# patch in a variable
	private function _ins_var($line) 
	{
		if (preg_match('#^\s*(\w+)(.*)#', $line, $m)) {
			$var = $m[1];
			$paramstr = trim($m[2]);
			if ($paramstr) {
				# php 5.3+ can handle "" quoted args
				$params = str_getcsv($paramstr, ' ');
			}
			$args = $this->args[$var];
			if (isset($this->vars[$var])) {
				if ($line == $this->qlines[0]) array_shift($this->qlines);
				foreach (array_reverse($this->vars[$var]) as $v) {
					if (is_array($args) and is_array($params)) {
						$v = str_replace($args, $params, $v);
					}
					array_unshift($this->qlines, $v);
				}
				$this->_trim_blanks();
				return true;
			}
		}
		return false;
	}

	# insert every variable that shows up in the first line - return non-variable first line
	private function _ins_vars($line) 
	{
		$firstline = $line;
		while ($this->_ins_var($firstline)) {
			$varfound = true;
			$firstline = $this->qlines[0];
		}
		if ($varfound) {
			$this->_trim_blanks();
			return $this->qlines[0];
		}
		return $line;
	}

	# see if next line is a context question
	private function _is_cq($line) 
	{
		$line = $this->_ins_vars($line);
		return preg_match('#^\s*cq\.#', $line) ? true : false;
	}

	# see if next line is a top level question
	private function _is_q($line) 
	{
		$line = $this->_ins_vars($line);
		return preg_match('#^\s*q\.#', $line) ? true : false;
	}

	private function _trim_blanks()
	{
		while (count($this->qlines) and preg_match('#^\s*$#', $this->qlines[0])) { 
			array_shift($this->qlines);
		}
	}

	private function _append_question(&$q, $line) 
	{
		if (strlen($q) == 0) $q = ltrim($line);
		else $q .= "\n".ltrim($line);
	}

	private function _set_answer(&$qtree, $qcount, $ans, $baseid)
	{
		if (preg_match('#^\s*$#', $ans)) return;
		$ac = ++$qtree['questions'][$qcount]['acount'];
		# adding details about answer question here 
		# to be more compatible with previous db design's `answers` table
		$qtree['questions'][$qcount]['answers'][$ac] = array(
			'qnum' => $qtree['questions'][$qcount]['qnum'],
			'id' => $qtree['questions'][$qcount]['id'],
			'answer' => trim($ans),
			'context_questions' => array(),
		);
		$this->qstack[] = "a$ac";
		$this->read .= "RECURSING {$this->_dump_qstack()}\n";
		$this->_parse_questions(
			$qtree['questions'][$qcount]['answers'][$ac]['context_questions'],
			true,
			$baseid
		);
		if (count($qtree['questions'][$qcount]['answers'][$ac]['context_questions']) == 0) {
			unset($qtree['questions'][$qcount]['answers'][$ac]['context_questions']);
		}
		array_pop($this->qstack);
		$this->read .= "FINISHED RECURSING {$this->_dump_qstack()}\n";
	}

	private function _dump_qstack($baseid='')
	{
		if (!is_array($this->qstack) or count($this->qstack) == 0) return;
		$stack = $this->qstack;
		if (preg_match('#^\w+$#',$baseid)) $stack[0] = $baseid;
		return implode('_', $stack);
	}

	function json($blob=null) 
	{
		if (isset($blob)) {
			$this->parse($blob);
		}
		return json_encode($this->questions);
	}
		
}
