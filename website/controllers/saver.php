<?php
# controller that takes care of saving input from interviews
# could also be used to send back the previously saved state
# of an interview
class Saver extends Controller {
	public function execute() 
	{
		$this->ss = new SaverstateModel;
		$this->doable = array(
			'save' => 'save_section_input',
			'fetch' => 'get_section_input',
		);
		$this->doaction();
	}

	public function save_section_input() 
	{
		if ($this->ss->ins($_POST)) {
			print "OK";
		} else {
			print "ERROR: ".$ss->err();
		}
		exit;
	}
	
	public function get_section_input() 
	{
		$answers = $this->ss->getone($_POST);
		if ($answers) {
			print json_encode($answers['state']);
		}
		exit;
	}
}

