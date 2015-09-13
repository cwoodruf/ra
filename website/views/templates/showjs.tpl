<a href="/ra/edit/section/{$sectionid}">Edit section {$sectionid}</a>
<h3 id="section-title">Section {$sectionid} mobile view</h3>

<div class="mobiletestcontrols" style="margin-bottom: 10px;">
<span class="viewport">
Viewport: 
<a href="javascript: void(0);" 
   onclick="$('#question').removeClass('landscape7').removeClass('portrait7');"
>none</a> - 
<a href="javascript: void(0);" 
   onclick="$('#question').removeClass('landscape7').addClass('portrait7');"
>portrait7</a> - 
<a href="javascript: void(0);" 
   onclick="$('#question').removeClass('portrait7').addClass('landscape7');"
>landscape7</a>  
</span>

<span class="saverdata" style="margin-left: 10px;">
Entered data:
<a href="javascript: void(0);" onclick="Data.clear(); location.reload();">delete</a> - 
<a href="javascript: void(0);" onclick="show_data(this,'saverdata');">show</a>
</span>

<div id="saverdata" style="display: none; margin-bottom: 10px;">
</div>
</div>

<div id="question">
</div>
