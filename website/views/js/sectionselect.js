function swapsection(idfrom,idto)
{
	lbfrom = document.getElementById(idfrom);
	lbto = document.getElementById(idto);
	if (lbfrom.selectedIndex < 0) return false;
	for (var i=0; i<lbfrom.length; i++) {
		if (!lbfrom[i].selected) continue;
		lbto.add(lbfrom[i]);
	}
	lbfrom.selectedIndex = lbfrom.length-1;
	lbto.selectedIndex = lbto.length-1;
	return false;
}
function setopt(optnew)
{
	var opt = new Option(optnew.text);
	opt.value = optnew.value;
	return opt;
}
function sectionup(id) 
{
	lb = document.getElementById(id);
	var here = lb.selectedIndex;
	if (here <= 0) return false;
	var opt = lb[here];
	lb[here] = setopt(lb[here-1]);
	lb[here-1] = setopt(opt);
	lb.selectedIndex = here - 1;
	return false;
}
function sectiondown(id)
{
	lb = document.getElementById(id);
	var here = lb.selectedIndex;
	if (here < 0) return false;
	if (here >= lb.length - 1) return false;
	var opt = lb[here];
	lb[here] = setopt(lb[here+1]);
	lb[here+1] = setopt(opt);
	lb.selectedIndex = here + 1;
	return false;
}
function selectall(id)
{
	sel = document.getElementById(id);
	if (sel == null) return false;
	var i = 0;
	var selected = false;
	for (i=0; i<sel.length; i++) {
		if (!sel[i].value) continue;
		selected = true;
		sel[i].selected = true;
	}
	return true;
}
function checktitle() 
{
	title = document.getElementById('surveysectiontitle');
	if (title.value == "") {
		alert("Please enter a title for a new survey.");
		return false;
	}
	return true;
}
