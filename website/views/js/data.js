// save entered data from a questionnaire section
// it is assumed we are using the qnums to identify answers
// as there is no control over whether a user generated qid 
// is unique
function Data() { }
Data.answers = {};
Data.orphaned = {};

Data.boot = function() {
        Data.answers = $.evalJSON(Saver.inflate());
}

Data.init = function(qnum,i) {
	if (Data.answers[qnum] == null) Data.answers[qnum] = {};
	if (Data.answers[qnum][i] == null) Data.answers[qnum][i] = {};
}

Data.set = function(qnum,i,value) {
	Data.init(qnum,i);
	Data.answers[qnum][i] = value;
	// hook into android storage
	Saver.save(qnum,i,value);
}

Data.unset = function(qnum,i) {
	if (Data.answers == null) return;
	if (Data.answers[qnum] == null) return;
	if (Data.answers[qnum][i] != null) {
		delete(Data.answers[qnum][i]);
		Saver.del(qnum,i);
	}
	var exists = false;
	for (var prop in Data.answers[qnum]) {
		exists = true;
	}
	if (!exists) {
		delete(Data.answers[qnum]);
		Saver.delary(qnum);
	}
}

// remove all related answers to another answer
Data.delrelated = function(qnum) {
	Data.answers[qnum] = {};
	var ql = qnum.length;
	for (var qans in Data.answers) {
		var qs = qnum.substr(0,ql);
		if (qs == qans) {
			delete(Data.answers[qans]);
			Saver.delary(qans);
		}
	}
}

Data.clear = function() {
	Data.answers = {};
	Saver.clear();
}

// check data for inconsistencies
Data.lint = function() {
	if (Data.answers == null) return true;
	var seen = {};
	for (var qnum in Data.answers) {
		seen[qnum] = true;
		for (var anum in Data.answers[qnum]) {
			seen[qnum+'-a'+anum] = true;
		}	
	}
	Data.orphaned = {};
	for (var qnum in Data.answers) {
		var qtest = "";
		var bits = qnum.split("-");
		for (var i in bits) {
			bit = bits[i]; 
			if (qtest != "") qtest += "-";
			qtest += bit;
			if (bit.substr(0,1) == 'a') continue;
			if (seen[qtest] == null) {
				Data.orphaned[qnum] = Data.answers[qnum];
				break;
			}
		}
	}
}

// remove orphaned answers 
Data.prune = function() {
	Data.lint();
	for (var qnum in Data.orphaned) {
		delete(Data.answers[qnum]);
		Saver.delary(qnum);
	}
}

Data.show = function() { alert($.toJSON(Data.answers)); }

Data.getary = function(key) {
	if (Data.answers == null) return false;
	if (Data.answers[key] == null) return false;
	return Data.answers[key];
}

Data.get = function(key,idx) {
	ary = Data.getary(key);
	if (ary) return ary[idx];
}

// ap in these functions is an answerparser object
Data.hasval = function(ap) {
	if (ap == null) return false;
	if (Data.answers == null) return false;
	if (Data.answers[ap.qnum] == null) return false;
	ap.hasval = true;
	return Data.getary(ap.qnum);
}

Data.val = function(ap) {
	var ary = Data.hasval(ap);
	if (ary) return ary[ap.count];
	return '';
}

Data.valat = function(ap,key) {
	var ary = Data.hasval(ap);
	if (ary) return ary[key];
	return;
}

Data.contains = function(ap,val) {
	ary = Data.getary(ap.qnum);
	if (!ary) return false;

	for (var ans in ary) {
		if (ary[ans] == val) {
			ap.hasval = true;
			return true;
		}
	}
	return false;
}

Data.selected = function(ap,val) {
	return (Data.val(ap) == val ? 'selected' : '');
}

/*
$(document).ready(function () {
    Data.boot();
});
*/

