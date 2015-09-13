// routines to parse answers
// see surveylanguage.txt for details on formatting
// this is intended to do the same work as survey/answerparser.php
function isset(thing) 
{
	if (thing == null) return false;
	return true;
}

function AnswerParser(ord)
{
	this.ord = ord;
	this.count = 0;
	this.monthstyle = 'short';
	var d = new Date;
	this.years = 5;
	this.firstyear = d.getFullYear();
	this.lastyear = this.firstyear - this.years;
	this.textrows = 5;

	this.months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
	this.monthslong = ['January','February','March','April','May','June',
					'July','August','September','October','November','December'];
}

AnswerParser.ins_goto = function(q) {
	return q.replace(
		/\[\s*goto\s+(\w+)\s*\]/ig,
		'<a class="goto" href="javascript: void(0);" '+
		"onclick=\"questionid_jump('$1');\">Goto $1</a>"
	);
}

AnswerParser.prototype = {

	// utility methods used by handlers below
	is_select: function() {
		if (this.widget == 'select' || this.widget == 'numselect') {
			return true;
		}
		return false;
	},

	_set_setter: function() {
		this.setter = 
			"set_parent('"+this.parentid+"'); "+
			"survey_set('"+this.qnum+"','"+this.ord+"','"+this.count+
			"',$(this).val(),this.checked,'"+this.widget+"');";
		return this.setter;
	},

	_set_id: function() {
		var count = this.count;
		this.id = ""+this.qnum+"-a"+this.ord+"_A"+count+"";
		return this.id;
	},
	
	_set_class: function() {
		this.class = "answer "+this.qnum+" "+this.qnum+"-a"+this.ord;
		return this.class;
	},

	_set_name: function() {
		this.name = "survey["+this.qnum+"]";
		return this.name;
	},

	_wrap_label: function() {
		this.ans = "<label for=\""+this._set_id()+"\">"+this.ans+"</label>";
	},

	_ins_field: function(pat,field) {
		this.ans = this.ans.replace(pat,field);
	},

	_extract_val: function(raw) {
		var val = raw;
		if (val == '#') {
			val = this.ord;
		}
		this.checked = '';
		if (Data.contains(this,val)) {
			this.checked = 'checked';
		}
		return val;
	},
	
	_sel_start: function() {
		id = this._set_id();
		name = this._set_name();
		return '<select id="'+id+'" class="'+this._set_class()+'" name="'+name+
			'" onchange="'+this._set_setter()+'"> <option value="">select</option> ';
	},

	_add_aunset: function(qnum) {
		if (this.ans == null || this.qnum == null) return;
		this.ans += this._aunset(this.qnum);
	},

	_aunset: function(qnum) {
		return " &nbsp; <a href=\"javascript:void(0);\" onclick=\"aunset('"+
				qnum+"');\" class=\"unset\">unset</a>"
	},

	// handlers used to convert answers to html

	_rad_handler: function(m) {
		val = this._extract_val(m[2]);
		field = '<input id="'+this._set_id()+'" name="'+this._set_name()+'" class="'+
			this._set_class()+'" onclick="'+"aclear('"+this.qnum+"'); "+this._set_setter()+
			'" type="radio" value="'+val+'" '+this.checked+'/> ';
		this._ins_field(m[1],field);
	},

	_chk_handler: function(m) {
		val = this._extract_val(m[2]);
		field = '<input id="'+this._set_id()+'" name="'+this._set_name()+'" class="'+
			this._set_class()+'" onclick="'+this._set_setter()+
			'" type="checkbox" value="'+val+'" '+this.checked+'/> ';
		this._ins_field(m[1],field);
	},

	_numsel_handler: function(m) {
		field = this._sel_start();

		if (m[2] > m[3]) {
			for (i = m[2]; i <= m[3]; i--) {
				var selected = Data.selected(this,i);
				field += "<option "+selected+">"+i+"</option>\n";
			}
		} else {
			for (i = m[2]; i <= m[3]; i++) {
				var selected = Data.selected(this,i);
				field += "<option "+selected+">"+i+"</option>\n";
			}
		}
		field += "</select>";
		this.ans = this.ans.replace(m[1], field);
	},

	_sel_handler: function(m) {
		field = this._sel_start();
		for (var opt in m[1].split('|')) {
			if (!opt) continue;
			var selected = (Data.val(this) == opt ? 'selected': '');
			field += "<option "+selected+">"+opt+"</option>";
		}
		field += "</select>";
		this.ans = this.ans.replace(m[1], field);
	},

	_num_handler: function(m) {
		size = (m[1].length+1)/2;
		field = '<input id="'+this._set_id()+'" name="'+this._set_name()+'" class="'+
			this._set_class()+'" onchange="if (isNaN($(this).val())) '+
			'{ alert('+"'number required'"+'); } else {'+this._set_setter()+'; }" '+
			'size="'+size+'" value="'+Data.val(this)+'" /> ';
		this._ins_field(m[1],field);
	},

	_txt_handler: function(m) {
		size = m[1].length * 10;
		field = '<input id="'+this._set_id()+'" name="'+this._set_name()+'" class="'+
			this._set_class()+
			'" onfocus="set_parent('+"'"+this.parentid+"'); "+
			'" onchange="'+this._set_setter()+
			'" size="'+size+'" value="'+Data.val(this)+'" /> ';
		this._ins_field(m[1],field);
	},

	_txtbox_handler: function(m) {
		cols = m[1].length * 10 - 2;
		field = '<textarea id="'+this._set_id()+'" name="'+this._set_name()+'" class="'+
			this._set_class()+
			'" onfocus="set_parent('+"'"+this.parentid+"'); "+
			'" onchange="'+this._set_setter()+'" rows="'+
			this.textrows+'" cols="'+cols+'" >'+Data.val(this)+'</textarea> ';
		this._ins_field(m[1],field);
	},

	// id made this way to be compatible with set_timestamp function
	_ts_handler: function(m) {
		qnum = this.qnum;
		tsid = this.qnum+this.count;
		field = '<input id="'+tsid+'" name="'+this._set_name()+'" class="'+
			this._set_class()+
			'" onchange="'+this._set_setter()+
			'" onfocus="set_parent('+"'"+this.parentid+"'); "+
			'" size="20" value="'+Data.val(this)+'"/> <a href="javascript:void(0);" onclick="'+
			"set_timestamp('"+tsid+"','"+this.qnum+"','"+this.count+"');"+'">set to now</a> ';
		this._ins_field(m[1],field);
	},

	// jquery ui date picker
	_datepicker: function(m) {
		tsid = this.qnum+this.count;
		field = '<input type="text" class="datepicker '+this.qnum+' '+tsid+'" value="'+
			Data.val(this)+'" onfocus="set_parent('+"'"+this.parentid+"'"+
			'); return false;" onchange="'+
			this._set_setter()+'" size="10" >'+
			' <a href="javascript:void(0);" onclick="'+
			'$(this).prev().trigger('+"'focus'"+');">select date</a> ';
		this._ins_field(m[1],field);
	},

	_date_handler: function(m) {
		var fields = new Object;

		var yopts = '';
		if (this.firstyear < this.lastyear) {
			for (y = this.firstyear; y <= this.lastyear; y++) {
				var selected = Data.selected(this,y);
				yopts += "<option "+selected+">"+y+"</option>\n";
			}
		} else {
			for (y = this.firstyear; y >= this.lastyear; y--) {
				var selected = Data.selected(this,y);
				yopts += "<option "+selected+">"+y+"</option>\n";
			}
		}
		fields['y'] = '<select id="'+this._set_id()+'" name="'+this._set_name()+'" class="'+
			this._set_class()+'" onchange="'+this._set_setter()+'"> <option value="">year</option> '+
			yopts+' </select> ';

		var months = this.months;
		if (this.monthstyle != 'short') months = this.monthslong;
		var mcnt = 1;
		var mopts = '';
		this.count++;
		for (var mon = 0; mon < months.length; mon++) {
			var selected = Data.selected(this,mcnt);
			mopts += "<option value=\""+mcnt+"\" "+selected+">"+months[mon]+"</option>\n";
			mcnt++;
		}
		fields['m'] = '<select id="'+this._set_id()+'" name="'+this._set_name()+'" class="'+
			this._set_class()+'" onchange="'+this._set_setter()+'"> <option value="">month</option> '+
			mopts+' </select>';

		var dopts = '';
		this.count++;
		for (i = 1; i <= 31; i++) {
			var selected = Data.selected(this,i);
			dopts += "<option "+selected+">"+i+"</option>\n";
		}
		fields['d'] = '<select id="'+this._set_id()+'" name="'+this._set_name()+'" class="'+
			this._set_class()+'" onchange="'+this._set_setter()+'"> <option value="">day</option> '+
			dopts+'</select>';

		var ls = m[1].split(/[\-\/]/);
		var field = '';
		for (var l = 0; l < ls.length; l++) {
			field += fields[ls[l]]+" ";
		}
		this._ins_field(m[1],field);
	},

	// methods for managing high level actions
	init: function(a) {
		if (a != null) {
			this.parentid = a['parentid'];
			this.qid = a['qid'];
			this.qnum = a['qnum'];
			this.rawans = a['answer'];
			this.ans = a['answer'];
		}
	},

	clear: function() {
		this.renderedanswer = '';
	},

	tostring: function() {
		return this.renderedanswer;
	},

	// after constructing the object run this then tostring
	parse: function(a) {
		this.init(a);
		this._answer_replace();
		this.renderedanswer = this.ans+"\n";
	},

	// main engine that turns an unformatted answer string into html
	_answer_replace: function()
	{
		// map of widget type to answer handler
		this.ansreplace = {
			'textbox': { 'pat': '(\\[____+\\])', 'handler': '_txtbox_handler' },
			'number': { 'pat': '(__(?: __)+)', 'handler': '_num_handler' },
			'text': { 'pat': '(___+)', 'handler': '_txt_handler' },
			'timestamp': { 'pat': '\\b(timestamp)\\b', 'handler': '_ts_handler' },
			'radio': { 'pat': '^\\s*([\\(\\[]([\\w#]+)[\\]\\)])', 
			         'handler': '_rad_handler', 'wrapper': '_wrap_label' },
			'checkbox': { 'pat': '^\\s*(\\{([\\w#]+)\\})', 
			         'handler': '_chk_handler', 'wrapper': '_wrap_label' },
			'date': { 'pat': '\\b(y[/-]m[/-]d|d[/-]m[/-]y|m[/-]d[/-]y|m[/-]y|y[/-]m|m[/-]d|d[/-]m)\\b', 
					 'handler': '_date_handler' },
			'datepicker': { 'pat': '\\b(datepicker)\\b', 'handler': '_datepicker' },
			// these selects are different from the original language spec
			'select': { 'pat': '(\\|([\w ]+\\|)+)', 'handler': '_sel_handler' },
			'numselect': { 'pat': '((\\d+)~(\\d+))', 'handler': '_numsel_handler' },
		};

		this.count = 0;
		var wrapper;
		do {
			var found = false;
			for (var widget in this.ansreplace) {
			       var a = this.ansreplace[widget];
				m = new RegExp(a['pat']).exec(this.ans)
				if (m) {
					if (!isset(this.widget)) this.widget = widget;
					this.value = m[2];
					if (this.value == '#') this.value = this.ord;
					this[a['handler']](m);
					wrapper = a['wrapper'];
					this.count++;
					found = true;
				}
			}
		} while (found && this.widget.match(/select/));

		// wrap label puts the field into a <label></label> block 
		// only do for simple check/radio answers
		if (wrapper != null) {
			this.count = 0;
			this[wrapper]();
		}
		this.ans = AnswerParser.ins_goto(this.ans);
		// this._add_aunset();
	},
}

