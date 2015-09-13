<html>
<head>
<title>test</title>
<script type="text/javascript" src="views/js/jquery-1.8.3.min.js"></script>
<script type=text/javascript src="views/js/jquery.json-2.4.js"></script>
<script type=text/javascript src="views/js/jquery.sha1.js"></script>
<script type=text/javascript src="views/js/jstorage.js"></script>
<script>var survey = new Object();</script>
<!-- script type=text/javascript src="surveydata.js">// static survey data</script -->
<script type="text/javascript" src="views/js/survey.js"></script>
<script type="text/javascript">
function aunsetall() {
	if (confirm("unset all answers?")) {
		aunset('answer');
	}
}

function aunset(qid) {
	$('.'+qid).each(function () { 
		this.checked = false; 
		this.selectedIndex = 0;
		if ($(this).is(':text') || $(this).is('textarea')) {
			$(this).val(""); 
		}
	});
	aclear(qid);
}

function aclear(qid) {
	$('#'+qid+' > .answers > .answer > .questioncontext').hide();
	$('#'+qid+' > .answers > .answer > .questioncontext > .answers').hide();
}

function survey_set(qid,answer,index,value,checked) {
	aid = qid+'-a'+answer;
	if (checked != null && checked == false) {
		$('#'+qid+' > .answers > #'+aid+' > .questioncontext').hide();
		$('#'+qid+' > .answers > #'+aid+' > .questioncontext > .answers').hide();
	} else {
		$('#'+qid+' > .answers > #'+aid+' > .questioncontext').show();
		$('#'+qid+' > .answers > #'+aid+' > .questioncontext > .answers').show();
	}
	if (value != null && value != "") {
		$('.'+aid).each(function () { this.checked = true; });
	}
}
</script>
<link rel="stylesheet" type="text/css" href="views/css/survey.css"/>
<style type="text/css">
.question .question {
	/* display: none; */
	display: block;
}
.expandall, .hideall,
.section a.expand, .section a.hide,
.question a.expand, .question a.hide {
	margin-left: 20px;
	text-decoration: none;
	color: #AAA;
	font-variant: italic;
}
.answers, .questioncontext, .answercontext {
	display: none;
}
div.questioncontext,
div.question {
	padding-top: 10px;
	padding-left: 50px;
	padding-bottom: 10px;
}
div.answers {
}
div {
}
</style>

</head>
<body>
<?php require('sectionhtmltree.php'); ?>
</body>
</head>
