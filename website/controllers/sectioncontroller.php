<?php
class SectionController extends Controller
{
	protected function _get_section($sectionid=null)
	{
		if (!isset($this->me)) 
			throw new Exception("not logged in in _parse_section!");

		if (!isset($sectionid)) $sectionid = $this->actions[2];
		if ((int)$sectionid > 0) {
			$s = new MysectionsModel;
			$this->sdata = $s->getone(array(
				'userid' => $this->me['userid'],
				'sectionid' => $sectionid,
			));
		}
	}

	protected function _parse_section($sectionid=null) 
	{
		$this->_get_section($sectionid);
		if (is_array($this->sdata)) {
			require_once('survey/sectionparser.php');
			$sp = new SectionParser($this->sdata['raw']);
			$sp->parse();
			return $sp;
		}
	}

	protected function _update_section($sectionid) 
	{
		$sectionid = (int)$sectionid;
		if ($sectionid <= 0) return false;

		$ms = new MysectionsModel;
		$section = $ms->getone(array(
			'userid' => $this->me['userid'],
			'sectionid' => $sectionid
		));
		if (!$section) return false;

		$s = new SectionModel;

		require_once('survey/sectionparser.php');
		$sp = new SectionParser;
		$sp->parse($section['raw']);

		# in general defer to what is in the section text except if name is missing
		if (!$sp->questions['sectionname']) {
			$sp->questions['sectionname'] = $section['name'];
			$section['raw'] = "s. {$section['name']}\n\n".$section['raw'];

			$r = $s->upd($sectionid, array('raw' => $section['raw']));
			if (!$r) die("update of raw data for section $sectionid failed: ".$s->err());
		}

		$php = var_export($sp->questions,true);
		$json = json_encode($sp->questions);


		$r = $s->upd($sectionid, array('name' => $sp->questions['sectionname']));
		if (!$r) die("php update of section name for $sectionid failed: ".$s->err());

		$r = $s->upd($sectionid, array('php' => $php));
		if (!$r) die("php update of section $sectionid failed: ".$s->err());

		$r = $s->upd($sectionid, array('json' => $json));
		if (!$r) die("json update of section $sectionid failed: ".$s->err());
		
		
/*
		# as these can be too big for a regular insert we use files
		$phpfile = TMPDIR."/section$sectionid.php";
		$jsonfile = TMPDIR."/section$sectionid.json";
		file_put_contents($phpfile,$php);
		file_put_contents($jsonfile,$json);
*/

	}
}

