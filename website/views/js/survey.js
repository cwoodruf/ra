// generic engine for generating survey questions and saving answers
// here as a reference
// superceded by answerparser.js and showquestion.js
var survey = new Object;

// should give some contact information?
function checkid(server) 
{
	if (partinfo_open) {
		$('#partinfo').hide();
		partinfo_open = false;
		return;
	}
	if (!survey_authenticate()) return false;
	$.post(
		server+"/bchcp/partcontact.php",
		survey_key(true),
		function (data) {
			if (data.match(/.*error authenticating.*/i)) password = null;
			$('#partinfo').html(data).show();
			partinfo_open = true;
		}
	);
}

// getting errors when trying to save data from checboxes
function set_survey_ary(save,id,i,data)
{
	if (survey == null) survey = new Object();
	var popclass = '.'+id+'_A'+i+'_popup';
	if (save) {
		$(popclass).show();
		if (survey[id] == null) survey[id] = new Object();
		survey[id][i] = data;
	} else {
		$(popclass).hide();
		if (survey[id] != null) {
			if (survey[id][i] != null) delete survey[id][i];
			if (Object.keys(survey[id]).length === 0) delete survey[id];
		}
	}
}

function qunset(qid)
{
	if (
		qid == null ||
		questions[qid] == null ||
		questions[qid].length == 0 ||
		survey == null ||
		survey[qid] == null
	) {
		return;
	}

	// if (!confirm("Are you sure you want to unset this question?")) return;

	delete survey[qid];
	while (questions[qid].popup == "1") {
		qid = questions[qid].qup;
	}
	if (qid != null && questions[qid] != null)
		qshow('',questions[qid]);
}

// make question html
function qmake(question) 
{
	var id = question.id;
	var q = question;

	var classes = 'question '+q.class;
	var unsetlink = '<a href="javascript: void(0);" onclick="qunset(\''+id+'\');">unset</a>';
	if (q.popup == "1") {
		classes = 'popup '+q.up+'_popup '+q.qup+'_popup ';
		if (q.answers != null && q.answers[0] != null) {
			classes += 'popup_'+q.answers[0].widget;
		}
	} else {
		atq = question;
	}

	var html = "<div class=\""+classes+"\">"+
		"<span class=question id="+id+"text>"+id+": "+q.question+"</span><br>\n";

	var checked = '';
	if (q.next != null && questions[question.next] != null) next = questions[question.next];

	if (q.answers != null && q.answers.length > 0) {
		var widget = null;
		var selectstarted = false;
		for (var i=0; i< q.answers.length; i++) {
			var nextid = q.answers[i].skipto;
			if (nextid == null) nextid = q.next;
			if (nextid == ':default:') nextid = q.neighbour;
			else if (questions[nextid] == null) nextid = q.next;
			if (widget == null) widget = q.answers[i].widget;
			if (widget == 'selectrange') widget = 'select';
			var popups = q.answers[i].popups;
			var qup = q.answers[i].qup;
			var up = q.answers[i].up;
			var value = q.answers[i].value;
			var ans = q.answers[i].answer;
			ans = ans.replace(/<.*?>/,"&lt;"+value+"&gt;");
			var pophtml = '';
			var popchild = '';

			var checked = '';
			if (survey[id] != null) {
				var found = false;
				if (survey[id] instanceof Object) {
					 if (survey[id][value] == 1) {
						found = true;
					}
				} else if (survey[id] == value) {
					found = true;
				}
				if (found) {
					checked = "checked";
					if (nextid != null && questions[nextid] != null) {
						next = questions[nextid];
					}
					popkids[kidcount++] = '.'+id+'_A'+value+'_popup';
				}
			}

			var htmlid=id+'_A';
			if (value != null) {
				htmlid += value;
				html += '<label for="'+htmlid+'">';
			}
			if (widget == 'checkbox') {
				html += '<input type=checkbox '+
					'name="survey['+id+']['+value+']" '+
					'id="'+htmlid+'" '+
					'onclick="'+"$('#"+q.up+"').attr('checked',true); "+
					          'set_survey_ary(this.checked,'+"'"+id+"'"+",'"+value+"',"+"1); ";

			} else if (widget == 'radio') {
				html += '<input type=radio '+
					'name="survey['+id+']" '+
					'id="'+htmlid+'" '+
					'onclick="'+"$('#"+q.up+"').attr('checked',true); "+
					          "$('."+qup+"_popup').hide(); "+ 
					          'survey.'+id+'='+"'"+value+"'; ";

			} else if (widget == 'text') {
				html += mk_text_widget(q,ans,nextid);

			} else if (widget == 'date') {
				html += mk_date_widget(q,ans,nextid,false);

			} else if (widget == 'timestamp') {
				unsetlink = "";
				html += mk_timestamp(q,nextid);

			} else if (widget == 'select') {
				if (!selectstarted) {
					selectstarted = true;
					html += '<select '+
						'name="survey['+id+']" '+
						'id="'+id+'" '+
						'onchange="'+"$('#"+q.up+"').attr('checked',true); "+
							  "$('."+qup+"_popup').hide(); "+ 
							  'survey.'+id+'=$(this).val();" >'+
							  "\n<option value=\"\">choose...</option>\n";
				} 
				var sel = '';
				if (value == survey[id]) sel = ' selected';
				value = value.replace(/"/,'&quot;');
				html += '<option value="'+value+'"'+sel+'>'+ans+"</option>\n";
			}
			if (q.popup == '0') {
				if (widget == 'radio')
					html += 'next=questions.'+nextid+'; $('+"'#next'"+').show(); ';
				else if (widget == 'checkbox')
					html += 'next=(this.checked ? questions.'+nextid+': questions.'+q.next+'); '+
						'$('+"'#next'"+').show(); ';
			}

			var popid = "";
			var showpop = "";
			if (popups != null) {
				for (var j=0; j<popups.length; j++) {
					if (popid == "") popid = "id=popup_"+popups[j];
					pophtml += qmake(questions[popups[j]]);
					if (widget == 'radio') html += 'popupshow(questions.'+popups[j]+'); ';
				}
			}

			if (widget == 'checkbox' || widget == 'radio') {
				html += '" value="'+value+'" '+checked+'>'+ans;
			}
			if (value != null) html += "</label><br>\n";
			html += pophtml;
		}
		if (selectstarted) html += "</select>\n";
		html += "<div class=\""+classes+" unsetlink\">"+unsetlink+"</div></div>";
		// no navigation for embedded popup questions
		if (question.popup == "1") return html;
	} else {
		if (q.next != null && questions[q.next] != null) next = questions[q.next];
		html += "</div>";
	}

	// if this question is part of a group show the next question
	if (q.shownext == 1) {
		html += qmake(questions[q.next]);
	}
	return html;
}

function mk_date_widget(q,ans,nextid,set)
{
	var months = [null,'Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
	var d = new Date();
	var year = d.getFullYear();
	var endyear = year - 100;
	var thisyear = -1;
	var today = -1;
	var thismonth = -1;
	if (set) {
		var today = d.getDate();
		var thismonth = d.getMonth() + 1;
	}
	var id = q.id;
	var i = 0;
	if (ans == null) return ans;
	ans = ans.replace(
		/([ymdYMD]+)/g,
		function (tag) {
			// create storage if necessary
			if (survey == null) survey = {};
			if (survey[id] == null) survey[id] = [];

			var html = '<select name="survey['+id+']" '+
				'onfocus="'+"$('#"+q.up+"').attr('checked',true);"+'" '+
				'onchange="survey.'+id+'['+i+']=$(this).val();">';
			var sel = '';
			if (tag.match(/[yY]+/)) {
				html += '<option value="">year</option>';
				for (var j=year+1; j>=endyear; j--) {
					if (survey[id][i] == j) sel = ' selected';
					else if (j == thisyear) sel = ' selected';
					else sel = '';
					if (set && sel != '') survey[id][i] = j;
					html += '<option value="'+j+'"'+sel+'>'+j+"</option>\n";
				}
			} else if (tag.match(/[dD]+/)) {
				html += '<option value="">day</option>';
				for (var j=1; j<=31; j++) {
					if (survey[id][i] == j) sel = ' selected';
					else if (j == today) sel = ' selected';
					else sel = '';
					if (set && sel != '') survey[id][i] = j;
					html += '<option value="'+j+'"'+sel+'>'+j+"</option>\n";
				}
			} else if (tag.match(/[mM]+/)) {
				html += '<option value="">month</option>';
				for (var j=1; j<=12; j++) {
					if (survey[id][i] == j) sel = ' selected';
					else if (j == thismonth) sel = ' selected';
					else sel = '';
					if (set && sel != '') survey[id][i] = j;
					html += '<option value="'+j+'"'+sel+'>'+months[j]+"</option>\n";
				}
			}
			html += "</select>\n";
			i++;
			return html;
		}
	);
	questions[id].itemcount = i;
	return ans;
}

function set_timestamp(qid,i) {
	var d = new Date(); 
	if (i == null) i = 0;
	var id = "#"+qid+i;
	$(id).val(d.toLocaleDateString()+' '+d.toLocaleTimeString());
	if (survey == null) survey = {};
	if (survey[qid] == null) survey[qid] = [];
	survey[qid][i] = $(id).val();
}

function mk_timestamp(q,nextid) 
{
	if (survey == null) survey = {};
	if (survey[q.id] == null) {
		survey[q.id] = [];
		var d = new Date();
		survey[q.id][0] = d.toLocaleDateString()+' '+d.toLocaleTimeString();
	}
	var html = '<span class="timestamp">'+
		   mk_text_widget(q,'_______',nextid)+
		   ' <a href="javascript: void(0);" onclick="set_timestamp(\''+q.id+'\',0);" >'+
		   'set to now</a></span>';
	return html;
}

function mk_timesel(q,start,set)
{
	var id = q.id;
	if (survey == null) survey = {};
	if (survey[id] == null) survey[id] = [];

	d = new Date();
	var hour = -1;
	var min = -1;
	if (set) {
		hour = d.getHours();
		min = d.getMinutes();
	}

	var sel = '';
	var i = (start == null ? 1 : start);
	html = '<span class="timestamp"><select name="survey['+id+']" '+
		'onfocus="'+"$('#"+q.up+"').attr('checked',true);"+'" '+
		'onchange="survey.'+id+'['+i+']=$(this).val();">';
	html += '<option value="">hour</option>';
	for (var j = 0; j < 24; j++) {
		if (j == survey[id][i]) sel = ' selected';
		else if (j == hour) sel = ' selected';
		else sel = '';
		if (set && sel != '') survey[id][i] = j;
		html += '<option value="'+j+'"'+sel+'>'+j+'</option>';
	}
	html += '</select>:';

	i++;
	html += '<select name="survey['+id+']" '+
		'onfocus="'+"$('#"+q.up+"').attr('checked',true);"+'" '+
		'onchange="survey.'+id+'['+i+']=$(this).val();">';
	html += '<option value="">minute</option>';
	for (var j = 0; j < 60; j++) {
		if (j == survey[id][i]) sel = ' selected';
		else if (j == min) sel = ' selected';
		else sel = '';
		if (set && sel != '') survey[id][i] = j;
		html += '<option value="'+j+'"'+sel+'>'+j+'</option>';
	}
	html += '</select></span>';
	return html;
}


// make text widget that can handle compound fields
// ____ plain text __ __ number,  [____] textbox
function mk_text_widget(q,ans,nextid) 
{
	var id = q.id;
	var i = 0;
	if (ans == null) return ans;
	ans = ans.replace(
		/(\[_+\]|____+|(__ )+__)/g,
		function (tag) {
			var html = "";
			var checker;
			var textarea = false;
			if (tag.match(/^____+/)) {
				var size = 3 * tag.length;
				html += "<input id=\""+id+i+"\" type=text size="+size+" ";
			} else if (tag.match(/^(__ )+__/)) {
				var size = (tag.length+1)/2;
				html += "<input id=\""+id+i+"\" type=text size="+size+" ";
				checker = 'digits';
			} else if (tag.match(/^\[_+/)) {
				textarea = true;
				html += "<textarea id=\""+id+i+"\" cols=60 rows=6 ";
			} else {
				html += "<input id=\""+id+i+"\" type=text size=40 ";
			}

			// create storage if necessary
			if (survey == null) survey = {};
			if (survey[id] == null) survey[id] = [];

			if (q.up != null) 
				html += 'onfocus="'+"$('#"+q.up+"').attr('checked',true);"+'" ';
			if (checker != null)
				html += "onblur=\"checkfield('"+checker+"',$(this).val());\" ";

			html += 'name="survey['+id+']" '+
				'onchange="survey.'+id+'['+i+']=$(this).val(); ';
			if (q.popup == '0') 
				html += 'next=questions.'+nextid+'; $('+"'#next'"+').show(); ';

			var val = "";
			if (survey[id][i] != null) val = survey[id][i];

			if (textarea) {
				html += '" >'+val+"</textarea>";
			} else {
				val.replace(/"/g,"\\'");
				html += '" value="'+val+'">';
			}
			i++;
			return html;
		}
	);
	// not every text question requires an answer
	// questions[id].itemcount = i;
	return ans;
}

function checkfield(checker,val) 
{
	var rules = { digits: "a number" };
	var ret = false;
	switch (checker) {
	case 'digits': ret = val.match(/^\d*$/); break;
	default: return true;
	}
	if (!ret) {
		alert("Invalid input: need "+rules[checker]);
		return false;e
	}
	return false;
}

function navmake(id)
{
	nav = "\n<div class=nav>\n";
	nav += "<div style=\"font-size: 12pt; float:left; padding-right: 50px; \">\n";
	nav += "Jump to a question: <select name=jumpto onchange=\"qshow(null,questions[$(this).val()]);\">\n";
	var found = false;
	$.each(questions, function(name, value) {
		if (name.match(/[A-Z]\w+/) && questions[name] != null && questions[name].popup == 0) {
			var selected = '';
			if (name == id) {
				selected = "selected";
				found = true;
			}
			var star = '*&nbsp;';
			if (survey[name] == null || 
			    (survey[name] instanceof Array && survey[name].length == 0)) {
				star = '&nbsp;&nbsp;';
			}
			if (questions[name].question != null) {
				nav += "<option value=\""+name+"\" "+selected+">"+star+name+" "+
					questions[name].question.substring(0,40)+"... </option>\n";
			}
		}
	});
	if (!found) nav += "<option value=\"+id+\" selected>"+id+"</option>\n";
	nav += "</select>\n</div>\n";
	if (prev != null && prev.id != null && questions[prev.id] != null) 
		nav += '<div class=navprev id=prev><a href="javascript: void(0);" '+
			'onclick="qshow(p,prev);">prev</a></div>'+"\n";
        if (next != null && next.id != null && questions[next.id] != null && atq.id != last.id) 
		nav += '<div class=navnext id=next ><a href="javascript: void(0);" '+
			     'onclick="qshow(n,next);">next</a></div>'+"\n";
	nav += "</div>\n";
	return nav;
}

// show an embedded question
function popupshow(question)
{
	if (question == null) return;
	$("."+question.up+"_popup").show();
}

// show a non-embedded question
function qshow(direction,question)
{
	save_data();
	if (question == null || question == '') {
		$('#questions').hide();
		$('#start').show();
		return;
	}
	// if we are in a group of questions always start with first in group
	if (question.firstq != null && questions[question.firstq] != null)
		question = questions[question.firstq];

	if (questions[question.id] == null) {
		alert("can't find question "+question.id);
		return;
	}
	if (direction == 'next' && currid != question.id) 
		questions[question.id].prev = currid;
	prev = questions[questions[question.id].prev];
	currid = question.id;

	$('#start').hide();
	popkids = new Array();
	kidcount = 0;
	var html = qmake(question);
	var nav = navmake(question.id);
	$('#questions').html('<div id="qnav">'+nav+'</div><div id="qhtml">'+html+'</div>').show();
	for (i=0; i< popkids.length; i++) {
		$(popkids[i]).show();
	}
}

// make string representation of entered survey data in json format
function survey_string ()
{
	if (!survey_authenticate()) return false;
	// may want to actually do something other than complain?
	survey_valid();
	return $.toJSON(survey);
	return false;
}

function survey_valid()
{
	var ret = true; 
	var errors = '';
	if (survey == null) {
		alert('no survey data!');
		return false;
	}
	// check various things here: note that text fields get checked with onblur
	for (var id in questions) {
		q = questions[id];
		if (survey[id] == null) continue;
		if (q.itemcount != null) {
			if (survey[id].length != q.itemcount) {
				errors += "missing data for question '"+id+": "+
					  q.question.substring(0,20)+"...'\n";
			}
		}
	}
	if (errors.length) {
		alert(errors);
		return false;
	}
	return true;
}

// make sure we have ids for everybody and then generate a key based on the researcher password
// key is required to validate results
function survey_authenticate ()
{
	password = $('#passwordin').val();
/*
	if (password == null || password == "") 
		password = prompt("please enter your password: ","");
*/

	if (password == null || password == "") {
		alert("need a password!");
		return false;
	}
	var part = "";
	var surveyor = "";

	if (survey.key != null) delete(survey.key);

	part = $('#participant').val();
/*
	if ($('#participant').val() == null || $('#participant').val() == "")
		part = prompt("please enter the participant id: ","");
*/

	// may want to do other checks at this point
	if (part == null || part == "") {
		alert("Need a participant ID");
		return false;
	}

	if (survey.partid == null || part != survey.partid) {
		survey.partid = part;
		$('#participant').val(survey.partid);
	}

	surveyor = $('#userid').val();
/*
	if ($('#userid').val() == null || $('#userid').val() == "")
		surveyor = prompt("please enter your userid: ","");
*/

	if (surveyor == null || surveyor == "") {
		alert("please enter your ID");
		return false;
	}

	if (survey.userid == null || surveyor != survey.userid) {
		survey.userid = surveyor;
		$('#userid').val(survey.userid);
	}
	survey.location = $('#location').val();

	survey_key(false);
	return true;
}

function survey_key(wantarray) {
	if (survey.ts == null) survey.ts = ""+(new Date()).getTime();
	if (password == null) password = $('#passwordin').val();
	if (password == null) password = prompt("Enter password:");
	survey.key = $.sha1(survey.surveyid+survey.userid+
			$.sha1('6d0aa35acc7546d4d0f5f261f04f754b5e5165d30bb6c20e41f86c8de847b6bfa458e03e045ec3'+$.sha1(password))+
			survey.partid+survey.ts);
	if (!wantarray) return;
	return {
		surveyid: survey.surveyid,
		userid: survey.userid,
		partid: survey.partid,
		ts: survey.ts,
		key: survey.key
	};
		
}

// print the form data in a way that can be later uploaded
function localsave ()
{
	var surveydata = survey_string();
	if (surveydata === false) return false;
	$('#surveydata').val("survey = "+surveydata+";");
	$('#localsave').toggle();
	// $('#localsaveclose').show();
}

// send collected data to back end via json
function save_data()
{
	$.jStorage.set('survey',survey);
	save_data_to_server(servers['local']);
	save_data_to_server(servers['remote']);
}

function save_data_to_server (server)
{
	if (!survey_authenticate()) return false;
	$.post(
		server+"/bchcp/surveysave.php",
		survey,
		function (data) {
			if (data.match(/.*error authenticating.*/i)) password = null;
			$('#save_data').html(server+":"+data).show().fadeOut(2000);
		}
	);
}

// get json data from the server for this survey/part pair
// use it if you are resuming a session 
function refresh_data (server)
{
	if (!survey_authenticate()) return false;
	skey = survey_key(true);
	if (Object.keys(survey).length > Object.keys(skey).length) {
		cont = confirm("Existing data will be overwritten: continue?");
		if (!cont) return false;
	}
	// var url = "https://bchcp.ca/bchcp/surveyjson.php";
	var data = skey;
	var success = function (data) {
				if (data == null) {
					$('#refresh_data').html("survey restore failed").show().fadeOut(2000);
					password = null;
					return;
				}
				var responsemsg = "survey data restored";
				$.each(data, function (name,value) {
					if (questions[name] != null) survey[name] = value;
					if (name == ':responsemsg') responsemsg = value;
				});
				$('#refresh_data').html(responsemsg).show().fadeOut(2000);
			};
	var error = function (data, e) {
		alert('error '+e);
	}
	var url = server+"/bchcp/surveyjson.php";
	$.getJSON( url, skey, success, error).done(alert("restored from: "+url));
}

$(document).ready(function () {
	thawed = $.jStorage.get('survey');
	if (thawed) survey = thawed;
	if (survey.surveyid != null) $('#surveyid').val(survey.surveyid);
	$('#userid').val(survey.userid);
	$('#participant').val(survey.partid);
	// save when an answer is changed
	// setInterval(save_data,60000);
});


