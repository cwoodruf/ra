// for the navigation tools built in set_nav below
function question_jump(currquestion) 
{
    if (currquestion == null) return;
	ShowQuestion.me().question_jump(parseInt(currquestion));
}

function questionid_jump(qid) 
{
	ShowQuestion.me().questionid_jump(qid);
}

function first_question() 
{
	ShowQuestion.me().show_first_question();
}

function prev_question()
{
	ShowQuestion.me().show_prev_question();
}

function next_question()
{
	ShowQuestion.me().show_next_question();
}

function last_question()
{
	ShowQuestion.me().show_last_question();
}

// given a survey section object from the web interface 
// display a question and navigation tools
function ShowQuestion(se,lastq) 
{
	this.section = ShowQuestion.section = parseInt(se);
	this.currquestion = lastq;
	this.html = '';
	this.expandchar = '+';
	this.sel_width = 40;
}

// static methods
// get an instance 
ShowQuestion.me = function() 
{
	if (ShowQuestion.section == null) return null;
	return ShowQuestion.reboot(ShowQuestion.section);
};

// instantiate a ShowQuestion object
ShowQuestion.reboot = function(section,lastq) 
{
	if (this.instance == null) {
		this.instance = new ShowQuestion(section,lastq);
	}
	return this.instance;
};

// object methods
ShowQuestion.prototype = 
{
	// this must be run before questions can be displayed with show_html
	set_question_html: function(data) 
	{
		if (data != null) {
			this.qroot = data;
			this.qselect = null;
			this.show_html();
		} else {
			$('#question').html(
				'<div class="question">No valid questions found.</div>'
			);
		}
	},

	show_html: function()
	{

		this.popshow = [];
		this.answered = {};
		this.showc = 0;
		this.html = '';
        Saver.setLastq(this.currquestion);
        
		this.set_question(this.qroot, this.currquestion, 'question','');
		$('#question').html(this.set_nav()+this.html);

		for (var i=0; i<this.popshow.length; i++) {
			context_show(this.popshow[i]);
		}
		// refresh jquery ui once the html is generated
        $('.datepicker').datepicker();
	},

	// for goto jumps - only makes sense for top level questions
	questionid_jump: function(qid) 
	{
		if (typeof this.qroot != 'object') return;
		for (var i=1; i<=this.qroot['qcount']; i++) {
			if (this.qroot['questions'][i]['id'] == qid) {
				this.question_jump(i);
			}
		}
	},

	question_jump: function(currquestion) 
	{
		if (typeof currquestion != 'number') return;
		if (typeof this.qroot != 'object') return;
		if (currquestion < 1 || currquestion > this.qroot['qcount']) return;
		this.currquestion = currquestion;
		this.show_html();
	},

	show_first_question: function() 
	{
		this.currquestion = 1;
		this.show_html();
	},

	show_prev_question: function() 
	{
		if (this.currquestion > 1) {
			this.currquestion--;
			this.show_html();
		}
	},

	show_next_question: function() 
	{
		if (this.currquestion < this.qroot['qcount']) {
			this.currquestion++;
			this.show_html();
		}
	},

	show_last_question: function() 
	{
		this.currquestion = this.qroot['qcount'];
		this.show_html();
	},

	build_qselect: function()
	{

		if (typeof this.qroot != 'object') return 'not an object';
		if (this.qroot['questions'] == null || 
			this.qroot['questions'].length == 0) return 'no questions';

		var select = '<select id="qselect" class="nav " '+
			'onchange="question_jump($(this).val());">'+"\n";
		var questions = this.qroot['questions'];
		var qtext;
		var selected;
		for (var i=1; i <= this.qroot['qcount']; i++) {
			var qtext = questions[i]['id']+': '+
				questions[i]['question'].substr(0,this.sel_width)+'...';
			if (i == this.currquestion) selected = 'selected';
			else selected = '';

			var star = '&nbsp;&nbsp;';
			if (Data.getary(questions[i]['qnum'])) star = '*';

			select += '<option value="'+i+'" '+selected+'>'+star+qtext+"</option>\n";
		}
		select += "</select>\n";
		return select;
	},

	set_nav: function()
	{
		var navhtml = '';

		if (typeof this.qroot != 'object') return;
		// note we initialize the html property here...
		navhtml = '<div class="nav">'+"\n";
		if (typeof this.currquestion != 'number' || this.currquestion < 1) {
			this.currquestion = 1;
		}
		if (this.currquestion > 1) {
			var firstq = this.qroot['questions'][1];
			var prevq = this.qroot['questions'][this.currquestion-1];
			navhtml += 
				'<a class="nav " href="javascript: void(0);" '+"\n"+
				' title="'+firstq['id']+': '+firstq['question']+'" '+"\n"+
				'onclick="first_question();" >&lt;&lt; start</a>'+"\n"+
				'<a class="nav " href="javascript: void(0);" '+"\n"+
				' title="'+prevq['id']+': '+prevq['question']+'" '+"\n"+
				'onclick="prev_question();" >&lt; prev</a>'+"\n";
		} else {
			navhtml += 
				'<span class="nav " >&lt;&lt; start</span>'+"\n"+
				'<span class="nav " >&lt; prev</span>'+"\n";
		}

		navhtml += this.build_qselect();

		if (this.currquestion < this.qroot['qcount']) {
			var nextq = this.qroot['questions'][this.currquestion+1];
			var lastq = this.qroot['questions'][this.qroot['qcount']];
			navhtml += 
				'<a class="nav " href="javascript: void(0);" '+"\n"+
				' title="'+nextq['id']+': '+nextq['question']+'" '+"\n"+
				'onclick="next_question();" >next &gt;</a>'+"\n"+
				'<a class="nav " href="javascript: void(0);" '+"\n"+
				' title="'+lastq['id']+': '+lastq['question']+'" '+"\n"+
				'onclick="last_question();" >end &gt;&gt;</a>'+"\n";
		} else {
			navhtml += 
				'<span class="nav " >next &gt;</span>'+"\n"+
				'<span class="nav " >end &gt;&gt;</span>'+"\n";
		}
		navhtml += '</div><!-- class="nav" -->'+"\n";
		return navhtml;
	},
	
    // may want to override this function for different 
    // viewer technology
	expander: function(qtree, aid, answer) 
	{
		var expvisible = "";
		if (qtree == null) expvisible = "invisible";
		var exp = answer+
		    "<div class=\"expander "+expvisible+"\">"+
			"<a class=\"expand "+expvisible+"\" href=\"javascript:void(0);\" "+
			"onclick=\"context_toggle('"+aid+"');\""+
			"title=\"show/hide context questions\">"+this.expandchar+"</a>"+
			"</div><div style='clear:both;'></div> ";
		return exp;
	},

	unsetlink: function(acount, qnum) {
		var un = "<div class=\"unsetlink\">";
		if (acount > 0) {
			un += " &nbsp; <a href=\"javascript:void(0);\" onclick=\"aunset('"+
				qnum+"');\" class=\"unset\">unset</a>";
		} 
		un += "</div> ";
		return un;
	},
	
	renderquestion: function(q) {
		var question = q.replace(/\n/g,"<br>\n");
		question = AnswerParser.ins_goto(question);
		return question;
	},

	set_question: function(qtree, currquestion, qclass, parentid) 
	{
		if (qtree == null || typeof qtree != 'object') return;
		if (typeof currquestion != 'number') return;
		if (typeof qclass != 'string') return;

		var question = qtree['questions'][currquestion];
		var qnum = question['qnum'];
		var acount = parseInt(question['acount'] == null ? 0: question['acount']);
		this.html += '<div id="'+qnum+'" class="'+qclass+'">'+"\n";
		this.html += this.renderquestion(question['question']);
		this.html += this.unsetlink(acount, qnum);
		this.html += '<div class="qunder"></div>';

		if (acount > 0) {
			// answers
			this.html += '<div style="display: block;" class="answers">'+"\n";
			for (var i=1; i<=acount; i++) {
				var ap = new AnswerParser(i);
				question['answers'][i]['parentid'] = parentid;
				ap.parse(question['answers'][i]);

				if (ap.hasval) this.answered[qnum] = true;

				var aid = qnum+'-a'+i;
				this.html += '<div id="'+aid+'" class="answer ">'+"\n";

				var qcache = qtree;
				var currq = currquestion;
				qtree = question['answers'][i]['context_questions'];
				
				this.html += this.expander(qtree, aid, ap.tostring());

				if (qtree != null) {
					for (var j=1; j<=qtree['qcount']; j++) {
						this.set_question(qtree, j, 'questioncontext',ap.id);
					}
					// whether or not to reopen a question
					// only applies to checkbox and radio
					var test;
					if (ap.widget != null && ap.widget.match(/radio|checkbox/) && ap.hasval) { 
						this.popshow[this.showc] = aid; 
						this.showc++;
					}
				} 
				qtree = qcache;
				currquestion = currq;
				this.html += '</div><!-- class="answer" -->'+"\n";
			}
			this.html += '</div><!-- class="answers" -->'+"\n";
		}
		this.html += '</div><!-- question -->'+"\n";
	},
}

