<?php
class dump extends Sectioncontroller 
{
	public function execute() 
	{
		$this->me = Login::check();
		if (!is_array($this->me)) exit;

		header("content-type: text/plain");
		$this->doable = array(
			'spss' => 'spss',
		);
		$this->doaction();
	}

	public function spss() 
	{
		$this->action = array_shift($this->actions);
		$this->survey = $this->actions[2];
		$this->section = $this->actions[3];

		require_once('survey/spss.php');
		$this->spss = new SPSS($this->_parse_section($this->section));

		$this->doable = array(
			'csv' => 'csv',
			'tab' => 'tab',
			'labels' => 'labels'
		);
		$this->doaction();
	}

	private function _send_headers($ext) {
		header('Content-Disposition: attachment; filename="survey'.
			$this->survey."section".$this->section.$ext.'"');
		flush(); // this doesn't really matter.
	}

	public function csv() 
	{
		$this->_send_headers('.csv');
		$this->spss->print_cases($this->_cases(), ",");
	}

	public function tab() 
	{
		$this->_send_headers('.dat');
		$this->spss->print_cases($this->_cases(), "\t");
	}

	private function _cases() 
	{
		$ss = new SaverstateModel;
		$this->cases = $ss->get_cases($this->survey,$this->section);
		return $this->cases;
	}

	public function labels() 
	{
		$this->spss->labels();
	}
}
