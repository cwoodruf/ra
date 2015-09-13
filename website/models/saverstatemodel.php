<?php
class SaverstateModel extends SaverstateRelation {
	public function get_cases($surveyid,$sectionid) 
	{
		$cases = $this->getall(array(
			"where survey=%u and section=%u",
			$surveyid, $sectionid
		));
		return $cases;
	}
}

