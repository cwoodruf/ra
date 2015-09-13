// functions for handling question visibility 
// also takes care of storing answers
// functions used by answerparser.js mainly

function data_lint() {
//	Data.lint();
	Data.prune();
	if (Data.orphaned.length) {
		alert("errors: "+Data.orphaned);
		Data.show();
	}
}

function aunsetall() {
	if (confirm("unset all answers?")) {
		aunset('answer');
	}
}

function unset_input(el) {
	el.checked = false; 
	el.selectedIndex = 0;
	if ($(el).is(':text') || $(el).is('textarea')) {
		$(el).val(""); 
	}
}

function aunset(qid) {
	if (qid == 'answer') Data.answers = {};
	else delete(Data.answers[qid]);
	data_lint();

	$('.'+qid).each(function () { 
		unset_input(this);
	});
	aclear(qid);
}

function aclear(qid) {
	var blockid = '#'+qid+' > .answers > .answer > .questioncontext';
	$(blockid).hide();
	$(blockid + ' > .answers').hide();
}

function mk_blockid(aid) {
	return '#'+aid+' > .questioncontext';
}

function context_toggle(aid) {
	var blockid = mk_blockid(aid);
	$(blockid).toggle();
	show_related(blockid);
}

var show_context = true;
function context_show(aid) {
	var blockid = mk_blockid(aid);
	if (!show_context) return;
	$(blockid).show();
	$(blockid + ' > .answers > .answer').show();
	$(blockid + ' > .answers').show();
}

function show_related(blockid) {
	// just using toggle doesn't seem to work with these
	if ($(blockid).is(':visible')) {
		$(blockid + ' > .answers > .answer').show();
		$(blockid + ' > .answers').show();
	} else {
		$(blockid + ' > .answers > .answer').hide();
		$(blockid + ' > .answers').hide();
	}
}

function survey_set(qid,answer,index,value,checked,widget) {
	var i = 0;
	if (widget == 'checkbox') {
		i = answer;
	} else {
		i = index;
	}
	Data.unset(qid,i);
	if (checked == true || (widget != 'checkbox' && widget != 'radio')) {
		Data.set(qid,i,value);
	}

	var aid = qid+'-a'+answer;
	var blockid = '#'+aid+' > .questioncontext';
	if (checked != null && checked == false) {
		$(blockid).hide();
		$(blockid + ' > .answers > .answer').hide();
		$(blockid + ' > .answers').hide();
		data_lint();
	} else {
		$(blockid).show();
		$(blockid + ' > .answers > .answer').show();
		$(blockid + ' > .answers').show();
	}
}

function set_timestamp(id, qnum, i) {
	var d = new Date(); 
	var ds = d.toLocaleDateString()+' '+d.toLocaleTimeString();
	$('#'+id).val(ds);
	Data.set(qnum,i,ds);
}

var toggle_parent = true;
function set_parent(parentid) {
	if (toggle_parent && !$('#'+parentid).checked) {
	   var sc = show_context;
	   show_context = true;
	   // yes, doing the attr twice is neccessary
	   $("#"+parentid).attr("checked",true).trigger("click").attr("checked",true);
	   show_context = sc;
	}
}

