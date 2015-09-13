var qroot;
var firstquestion = 1;
var currquestion = firstquestion;
var lastquestion;
var html = '';

function section_data(section) 
{
	$.getJSON(
		'/ra/data/section/'+section,
		{
			'userid': 'cal',
			'nonce': 0,
			'sig': 'test'
		},
		function (data) {
			qroot = data;
		}
	);
}

function show_question(qtree, currquestion, qclass) 
{
	if (typeof qtree != 'Object') {
		html = "No valid questions found.";
		return html;
	}
	var question = qtree['questions'][currquestion];
	var qid = question['id'];
	if (lastquestion == null) lastquestion = qtree['qcount'];
	html += '<div id="'+qid+'" class="'+qclass+'">'+"\n"
			+question['question'].replace(/\n/g,"<br>\n");
	if (question['acount'] != null && question['acount'] > 0) {
		html += '<div style="display: block;" class="answers">'+"\n";
		for (var i=1; i<=question['acount']; i++) {
			var ap = new AnswerParser(i);
			ap.parse(question['answers'][i]);
			var aid = qid+'-a'+i;
			html += '<div id="'+aid+'" class="answer ">'+"\n"+ap.tostring();
			var qcache = qtree;
			var currq = currquestion;
			qtree = question['answers'][i]['context_questions'];
			if (qtree != null) {
				for (var j=1; j<=qtree['qcount']; j++) {
					show_question(qtree, j, 'questioncontext');
				}
			}
			qtree = qcache;
			currquestion = currq;
			html += '</div><!-- class="answer" -->'+"\n";
		}
		html += '</div><!-- class="answers" -->'+"\n";
	}
	html += '</div><!-- question -->'+"\n";
	return html;
}

function set_question_html() {
	html = '';
	$('#question').html(show_question(qroot, currquestion, 'question'));
}

$(document).ready(function () {
	qroot = section_data(survey_section());
	// seem to need this delay for very large question trees
	window.setTimeout(set_question_html,1000);
});
