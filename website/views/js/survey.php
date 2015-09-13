<?php
chdir("../..");
require_once('init.php');
$u = new UsersModel;
$sig = $u->gen_sig();
?>
// emulate android interface
Saver.survey_section = function() 
{
	return parseInt('<?php print $_GET['survey_section']; ?>');
}

function participant() 
{
	return 'test';
}

function Saver() {}

Saver.store = {};

Saver.inflate = function() {
	Saver.key = 'participant_'+participant()+'_survey_section_'+Saver.survey_section();

	var thawed = $.jStorage.get(Saver.key);
	if (thawed) Saver.store = thawed;

	thawed = $.jStorage.get(Saver.key+'_lastq');
	if (thawed) Saver.lastq = thawed;
	else Saver.lastq = 1;

	return $.toJSON(Saver.store);
}

Saver.setLastq = function(currquestion) {
	if (currquestion != Saver.lastq) {
		Saver.lastq = currquestion;
		Saver.sync();
	}
}

Saver.getLastq = function() {
	return Saver.lastq;
}

Saver.del = function(key,idx) {
	if (Saver.store[key] != null) {
		if (Saver.store[key][idx] != null) 
			delete(Saver.store[key][idx]);
		exists = false;
		for (var test in Saver.store[key]) {
			exists = true;
		}
		if (!exists) delete(Saver.store[key]);
		Saver.sync();
	}
}

Saver.delary = function(key) {
	if (Saver.store[key] != null) {
		delete(Saver.store[key]);
		Saver.sync();
	}
}

Saver.clear = function () {
	Saver.store = {};
	Saver.sync();
}

Saver.sync = function() {
	$.jStorage.set(Saver.key+'_lastq',Saver.lastq);
	$.jStorage.set(Saver.key,Saver.store);
}

Saver.save = function(key,idx,val) {
	Saver.store[key] ? true: (Saver.store[key] = {});
	Saver.store[key][idx] = val;
	Saver.sync();
}

Saver.toString = function () {
	return $.toJSON(Saver.store);
}

function show_data(a,id) {
	$('#'+id).html('<pre>'+Saver.toString()+'</pre>').toggle();
	if ($('#'+id).is(':visible')) $(a).html('hide');
	else $(a).html('show');
}
	
function show_sigkey() 
{
	$.get(
		'/ra/profile/getkey',
		{
			'userid': '<?php print $sig['userid']; ?>',
			'nonce': '<?php print $sig['nonce']; ?>',
			'sig': '<?php print $sig['sig']; ?>'
		},
		function (data) {
			if (data.match(/^\w+$/)) {
				$('#sigkey').html(
					data+
					' <a href="javascript: void(0);" '+
					'onclick="$(this).parent().hide(); '+
					"$('#sigkeyshow').show();\">hide</a>"
				).show();
				$('#sigkeyshow').hide();
			} else {
				alert("error getting sigkey "+data);
			}
		}
	);
}

function now() 
{
	var d = new Date;
	return sprintf(
		'%04d-%02d-%02d %02d:%02d', 
		d.getFullYear(),
		d.getMonth()+1,
		d.getDate(),
		d.getHours(),
		d.getMinutes()
	);
}


$(document).ready(function () {

	if ("ShowQuestion" in window && Saver.survey_section() > 0) {

		Data.boot();
		var ss = ShowQuestion.reboot(Saver.survey_section(),Saver.getLastq());
		ss.sel_width = 30;

		$.getJSON(
			'/ra/data/section/'+ss.section,
			{
				'userid': '<?php print $sig['userid']; ?>',
				'nonce': '<?php print $sig['nonce']; ?>',
				'sig': '<?php print $sig['sig']; ?>'
			},
			function (data) {
				ss.set_question_html(data);
			}
		);
	}
});

