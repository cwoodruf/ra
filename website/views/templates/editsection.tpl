<h3>Survey {$surveyid} 
{if is_array($sdata)}
Section {$sdata.sectionid} {$sdata.name}
(<a href="/ra/edit/section/{$sdata.sectionid}">refresh</a>)</h3>
{else}
New Section</h3>
{/if}

<h4>{$response}</h4>
<div class="editsectionlinks">

{foreach from=$surveys key=i item=survey}
<a href="/ra/edit/survey/{$survey.surveyid}">edit survey {$survey.surveyid}</a>
{if $survey.hide}(survey hidden){/if} &nbsp;
{/foreach}

{if is_array($sdata)}
view section:  
<a href="/ra/test/js/{$sdata.sectionid}">mobile</a> / 
<a href="/ra/test/php/{$sdata.sectionid}">web</a> / 
<a href="/ra/data/section/{$sdata.sectionid}" target=_blank>json</a>
{/if}
</div>

<form action="/ra/edit/sectionsave/{$sdata.sectionid}" method="post">
<input type="hidden" name="surveyid" value="{$surveyid}"/>
<input type="reset" value="reset form">  
Name: <input name="name" value="{$sdata.name}" size="40"/>
{if $sdata.visible}visible (ordinal: {$sdata.ord}){else}not visible{/if} 
<input type="submit" name="" value="save section">
<a href="/ra/survey/surveylanguage.txt" target=_blank>language reference</a>
<br>
<textarea name="raw" rows="50" cols="120">{$sdata.raw}</textarea>
</form>
