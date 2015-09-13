<?php
# routines to parse answers
# see surveylanguage.txt for details on formatting
class AnswerParser
{
	private $ansreplace = array(
		'textbox' => array( 'pat' => '#(\[____+\])#', 'handler' => '_txtbox_handler' ),
		'number' => array( 'pat' => '#(__(?: __)+)#', 'handler' => '_num_handler' ),
		'text' => array( 'pat' => '#(___+)#', 'handler' => '_txt_handler' ),
		'timestamp' => array( 'pat' => '#\b(timestamp)\b#', 'handler' => '_ts_handler' ),
		'radio' => array( 'pat' => '/(^[\[\(]([\w# ]+)[\]\)])/', 
				'handler' => '_rad_handler', 'wrapper' => '_wrap_label' ),
		'checkbox' => array( 'pat' => '/(^\{([\w# ]+)\})/', 
				'handler' => '_chk_handler', 'wrapper' => '_wrap_label' ),
		'datepicker' => array( 'pat' => '/\b(datepicker)\b/', 'handler' => '_datepicker' ),
		'date' => array( 'pat' => '#\b(y[/-]m[/-]d|d[/-]m[/-]y|m[/-]d[/-]y|m[/-]y|y[/-]m|m[/-]d|d[/-]m)\b#', 
				 'handler' => '_date_handler' ),
		# these selects are different from the original language spec
		'select' => array( 'pat' => '#(\|([\w ]+\|)+)#s', 'handler' => '_sel_handler' ),
		'numselect' => array( 'pat' => '#((\d+)~(\d+))#', 'handler' => '_numsel_handler' ),
	);
	private $months = array('Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec');
	private $monthslong = array('January','February','March','April','May','June',
					'July','August','September','October','November','December');

	function __construct($ord,$starthtml='',$endhtml='',$alreadyrendered='') {
		$this->ord = $ord;
		$this->starthtml = $starthtml;
		$this->endhtml = $endhtml;
		$this->count = 0;
		$this->monthstyle = 'short';
		$this->firstyear = date('Y') - 5;
		$this->lastyear = date('Y') + 5;
		$this->textrows = 5;
		$this->renderedanswer = $alreadyrendered;
	}

	function init($a, $ord = null) {
		if ($a != null) {
			if (isset($ord)) $this->ord = $ord;
			$this->qnum = $a['qnum'];
			$this->qid = $a['id'];
			$this->rawans = $a['answer'];
			$this->ans = $a['answer'];
			unset($this->widget);
		}
	}

	function clear() {
		$this->renderedanswer = '';
	}

	function is_select() {
		if ($this->widget == 'select' or $this->widget == 'numselect') {
			return true;
		}
		return false;
	}

	function tostring() {
		return $this->renderedanswer;
	}

	function parse($a, $ord = null) {
		$this->init($a, $ord);
		$this->_answer_replace();
		$this->renderedanswer .= 
			$this->starthtml."\n".
			$this->ans."\n".
			$this->endhtml."\n";
		$this->renderedanswer = self::ins_goto($this->ans);
		# $this->renderedanswer .= $this->_add_aunset();
	}

	public static function ins_goto($q) {
		return preg_replace(
			'/\[\s*goto\s+(\w+)\s*\]/i',
			"<a class=\"goto\" href=\"#$1\">Goto $1</a>",
			$q
		);
	}

	function _add_aunset() {
		if ($this->qnum and $this->count > 0) return $this->_aunset();
	}

	function _aunset() {
		return <<<HTML
&nbsp; <a href="javascript:void(0);" onclick="aunset('{$this->qnum}');" class="unset">unset</a>

HTML;
	}

	private function _answer_replace()
	{
		$this->count = 0;
		$wrapper = null;
		do {
			$found = false;
			foreach ($this->ansreplace as $widget => $a) {
				if (preg_match($a['pat'], $this->ans, $m)) {
					if (!isset($this->widget)) $this->widget = $widget;
					$this->value = $this->_extract_val($m[2]);
					$handler = $a['handler'];
					$wrapper = $a['wrapper'];
					$this->$handler($m);
					$this->count++;
					$found = true;
				}
			}
		} while ($found and strstr('select',$this->widget));
		if (isset($wrapper)) {
			$this->$wrapper();
		}
	}

	function _set_setter() {
		$this->setter = 
			"survey_set('{$this->qnum}','{$this->ord}','{$this->count}',".
				"$(this).val(),this.checked,'{$this->widget}');";
		return $this->setter;
	}

	function _set_id($count=null) {
		if (!isset($count)) $count = $this->count;
		$val = str_replace(' ','_',$this->value);
		$this->id = "{$this->qnum}-a{$this->ord}_A{$count}";
		return $this->id;
	}
	
	function _set_class() {
		$this->class = "answer {$this->qnum} {$this->qnum}-a{$this->ord}";
		return $this->class;
	}

	function _set_name() {
		$this->name = "survey[{$this->qnum}]";
		return $this->name;
	}

	function _wrap_label() {
		$this->ans = "<label for=\"{$this->_set_id(0)}\">{$this->ans}</label>";
	}

	function _ins_field($pat,$field) {
		$this->ans = str_replace($pat,$field,$this->ans);
	}

	function _extract_val($raw) {
		if ($raw == '#') {
			return $this->ord;
		}
		return $raw;
	}

	function _rad_handler($m) {
		$val = $this->_extract_val($m[2]);
		$field = <<<HTML
<input id="{$this->_set_id()}" name="{$this->_set_name()}" class="{$this->_set_class()}"
       onclick="aclear('{$this->qnum}'); {$this->_set_setter()}" type="radio" value="$val"/>
HTML;
		$this->_ins_field($m[1],$field);
	}

	function _chk_handler($m) {
		$val = $this->_extract_val($m[2]);
		$field = <<<HTML
<input id="{$this->_set_id()}" name="{$this->_set_name()}" class="{$this->_set_class()}"
       onclick="{$this->_set_setter()}" type="checkbox" value="$val"/>
HTML;
		$this->_ins_field($m[1],$field);
	}

	function _sel_start() {
		$id = $this->_set_id();
		$name = $this->_set_name();
		return <<<HTML
<select id="$id" class="{$this->_set_class()}" name="$name" onselect="{$this->_set_setter()}">
<option value="">select</option>

HTML;
	}

	function _numsel_handler($m) {
		$field = $this->_sel_start();
		if ($m[2] > $m[3]) {
			for ($i = $m[2]; $i >= $m[3]; $i--) {
				$field .= "<option>$i</option>\n";
			}
		} else {
			for ($i = $m[2]; $i <= $m[3]; $i++) {
				$field .= "<option>$i</option>\n";
			}
		}
		$field .= "</select>";
		$this->ans = str_replace($m[1], $field, $this->ans);
	}

	function _sel_handler($m) {
		$field = $this->_sel_start();
		foreach (explode('|', $m[1]) as $opt) {
			if (!$opt) continue;
			$field .= "<option>$opt</option>";
		}
		$field .= "</select>";
		$this->ans = str_replace($m[1], $field, $this->ans);
	}

	function _num_handler($m) {
		$size = (strlen($m[1])+1)/2;
		$field = <<<HTML
<input id="{$this->_set_id()}" name="{$this->_set_name()}" class="{$this->_set_class()}"
       onchange="if (isNaN($(this).val())) { alert('number required'); } 
                 else { {$this->_set_setter()}; }" 
       size="$size" />
HTML;
		$this->_ins_field($m[1],$field);
	}

	function _txt_handler($m) {
		$size = strlen($m[1]) * 10;
		$field = <<<HTML
<input id="{$this->_set_id()}" name="{$this->_set_name()}" class="{$this->_set_class()}"
       onchange="{$this->_set_setter()}" size="$size" />
HTML;
		$this->_ins_field($m[1],$field);
	}

	function _txtbox_handler($m) {
		$cols = strlen($m[1]) * 10 - 2;
		$field = <<<HTML
<textarea id="{$this->_set_id()}" name="{$this->_set_name()}" class="{$this->_set_class()}"
         onchange="{$this->_set_setter()}" 
         rows="{$this->textrows}" cols="{$cols}"
></textarea>
HTML;
		$this->_ins_field($m[1],$field);
	}

	# id made this way to be compatible with set_timestamp function
	function _ts_handler($m) {
		$qnum = $this->qnum;
		$tsid = $qnum.$this->count;
		$field = <<<HTML
<input id="$tsid" name="{$this->_set_name()}" class="{$this->_set_class()}"
       onchange="{$this->_set_setter()}" size="20" />
<a href="javascript:void(0);" onclick="set_timestamp('$qnum',0);">set to now</a>
HTML;
		$this->_ins_field($m[1],$field);
	}

        # jquery ui date picker
        function _datepicker($m) {
                $tsid = $this->qnum.$this->count;
                $field = <<<HTML
<input type="text" class="datepicker {$this->qnum} $tsid" value=""
	onfocus="set_parent('{$this->parentid}'); return false;"
	onchange="{$this->_set_setter()}" size="10" >
<a href="javascript:void(0);" onclick="$(this).prev().trigger('focus');">select date</a>
HTML;
                $this->_ins_field($m[1],$field);
        }

	function _date_handler($m) {
		for ($i = 1; $i <= 31; $i++) {
			$dopts .= "<option>$i</option>\n";
		}
		if ($this->monthstyle == 'short') $months = $this->months;
		else $months = $this->monthslong;
		$mcnt = 1;
		foreach ($months as $month) {
			$mopts .= "<option value=$mcnt>$month</option>\n";
			$mcnt++;
		}
		for ($y = $this->firstyear; $y <= $this->lastyear; $y++) {
			$yopts .= "<option>$y</option>\n";
		}
		$fields['y'] = <<<HTML
<select id="{$this->_set_id()}" name="{$this->_set_name()}" class="{$this->_set_class()}"
        onselect="{$this->_set_setter()}">
<option value="">year</option>
$yopts
</select>
HTML;
		$this->count++;
		$fields['m'] = <<<HTML
<select id="{$this->_set_id()}" name="{$this->_set_name()}" class="{$this->_set_class()}"
        onselect="{$this->_set_setter()}">
<option value="">month</option>
$mopts
</select>
HTML;
		$this->count++;
		$fields['d'] = <<<HTML
<select id="{$this->_set_id()}" name="{$this->_set_name()}" class="{$this->_set_class()}"
        onselect="{$this->_set_setter()}">
<option value="">day</option>
$dopts
</select>
HTML;
		foreach (preg_split('#[\-/]#',$m[1]) as $l) {
			$field .= "{$fields[$l]} ";
		}
		$this->_ins_field($m[1],$field);
	}
}

