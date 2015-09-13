$(document).ready(function () {
    show_context = false;
    Data.boot();
	var ss = ShowQuestion.reboot(Saver.survey_section(),Saver.getLastq());
	ss.sel_width = 30;
    ss.set_question_html($.evalJSON(Saver.getSectiondata()));
});

