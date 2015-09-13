<?php
class SurveysectionModel extends SurveysectionRelation
{
	function hidesections($surveyid) 
	{
		try {
			$this->run(
				"update {$this->table} set visible=0 where surveyid='%u'",
				$surveyid
			);
			return true;
		} catch (Exception $e) {
			if (!QUIET) die($this->err());
			return false;
		}
	}

	function reorder($surveyid,$sections) 
	{
		if (Check::digits($surveyid) and is_array($sections)) {
			$this->hidesections($surveyid);
			foreach ($sections as $ord => $sectionid) {
				$this->ins(array(
					'surveyid' => $surveyid,
					'sectionid' => $sectionid,
					'visible' => 1,
					'ord' => $ord
				));
			}
		}
	}
}

