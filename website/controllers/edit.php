<?php
class Edit extends SectionController
{
	function execute() 
	{
		$this->me = Login::check();

		if (!is_array($this->me)) {
			print "ERROR bad signature or login ".self::r('sig');
			exit;
		}
		View::addJS('survey.php');
		View::addCSS('survey.css');
		$this->doable = array(
			'survey' => 'survey',
			'surveysave' => 'surveysave',
			'section' => 'section',
			'sectionsave' => 'sectionsave',
			'interview' => 'interview',
			'interviews' => 'interviews',
		);
		$this->doaction();
	}

	function survey($surveyid = null) 
	{
		if ($surveyid == null) $surveyid = $this->actions[2];
		# surveyid can have no digits
		if (Check::digits($surveyid)) {
			$s = new SurveyModel;
			$survey = $s->getone($surveyid);
			View::assign('survey',$survey);

			# "where surveyid = _ and userid = _"
			$selector = MysurveysModel::selector($this->me['userid'], $surveyid);
			$ss = new MysurveysectionsModel;
			$sections = $ss->getall($selector);
			View::assign('sections',$sections);
		} else {
			View::assign('error','Error: invalid surveyid!');
		}
		View::addJS('sectionselect.js');
		View::wrap('editsurvey.tpl');
	}

	function surveysave()
	{
		$surveyid = $this->actions[2];
		$s = new SurveyModel;

		if (!isset($_REQUEST['hide'])) $_REQUEST['hide'] = 0;

		if (Check::digits($surveyid)) {
			if ( !$s->upd( $surveyid, $_REQUEST )) 
				View::assign('response',$s->err());
		} else {
			if (!$s->ins($_REQUEST)) 
				View::assign('response',$s->err());
			else $surveyid = $s->lastid();
		}

		if (!$s->err() and Check::digits($surveyid)) {
			$ss = new SurveysectionModel;
			$ss->reorder($surveyid, $_REQUEST['sections']);
			$a = new AccessModel;
			$r = $a->ins(array(
				'surveyid'=>$surveyid,
				'userid'=>$this->me['userid'],
				'level'=>'any'
			));
			if (!$r) View::assign('response','Error: '.$a->err());
			else View::assign('response',"Success: saved survey $surveyid");
		}
		$this->survey($surveyid);
	}
	
	function section($sectionid=null)
	{
		if (Check::digits($sectionid)) $this->actions[2] = $sectionid;
		else $sectionid = $this->actions[2];
		try {
			$this->_get_section($sectionid);
			$ss = new MysurveysectionsModel;
			if ($sectionid > 0) {
				$surveys = $ss->section2surveys(
					$this->me['userid'],
					$sectionid
				);
				if ($surveys) View::assign('surveys',$surveys);
			}
			View::assign('sdata',$this->sdata);
		} catch (Exception $e) {
			View::assign('response',"Error: ".$e->getMessage());
		}
		if (Check::digits($_REQUEST['surveyid'])) 
			View::assign('surveyid',$_REQUEST['surveyid']);
		View::wrap('editsection.tpl');
	}

	function sectionsave()
	{
		$s = new SectionModel;
		$sectionid = $this->actions[2];
		$surveyid = $_REQUEST['surveyid'];
		$ms = new MysurveysModel;
		if ($surveyid > 0) {
			$survey = $ms->getone(array(
				'userid' => $this->me['userid'],
				'surveyid' => $surveyid
			));
			if (!$survey) die("survey $surveyid does not belong to user {$this->me['userid']}");
		}

		if (empty($_REQUEST['name'])) {
			View::assign('response','Error: need a section name.');
			return $this->section();
		}
		if (Check::digits($sectionid)) {
			if (!$s->upd($sectionid,$_REQUEST)) View::assign('response',$s->err());
		} else {
			if (!$s->ins($_REQUEST)) View::assign('response',$s->err());
			else $sectionid = $s->lastid();
			$ss = new SurveysectionModel;
			if (!$s->err() and $surveyid > 0 and $survey) {
				$r = $ss->ins(array('sectionid'=>$sectionid, 'surveyid'=>(int)$surveyid));
				if (!$r) View::assign('response','Error: '.$ss->err());
			}
		}
		if (!$s->err() and Check::digits($sectionid)) {
			$this->_update_section($sectionid);
			View::assign('response',"Successfully saved section $sectionid");
		}

		$this->section($sectionid);
	}

	public function interview() 
	{
		$actions = $this->actions;
		$controller = array_shift($actions);
		$function = array_shift($actions);
		$action = array_shift($actions);
		$this->surveyid = array_shift($actions);
		$this->sectionid = array_shift($actions);
		$this->partid = implode(' / ',$actions);
		$this->ss = new SaverstateModel;
		if ($this->partid and $action == 'delete') {
			$which = array(
				'partid'=>$this->partid,
				'survey'=>$this->surveyid,
				'section'=>$this->sectionid
			);
			$rstr = "interview for {$this->partid}";
			$case = $this->ss->getone($which);
			if (!$case) {
				View::assign('response',"Error: can't find $rstr");
			} else {
				$ssb = new Saverstate_backupModel;
				$ssb->ins($case);
				$r = $this->ss->del($which);
				if ($r) {
					View::assign('response', "Deleted $rstr");
				} else {
					View::assign('response',"Failed to delete $rstr");
				}
			}
		}
		$this->interviews();
	}

	public function interviews() 
	{
		if (!isset($this->ss)) $this->ss = new SaverstateModel;
		if (!$this->surveyid) $this->surveyid = $this->actions[2];
		if (!$this->sectionid) $this->sectionid = $this->actions[3];
		$cases = $this->ss->get_cases($this->surveyid, $this->sectionid);
		View::assign('cases',$cases);
		View::assign('surveyid',$this->surveyid);
		View::assign('sectionid',$this->sectionid);
		View::wrap('interviews.tpl');
	}
}
