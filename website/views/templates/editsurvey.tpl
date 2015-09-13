{if is_array($survey)}
<h3>Survey {$survey.surveyid}: {$survey.title} 
    (<a href="/ra/edit/survey/{$survey.surveyid}">refresh</a>)</h3>
{else}
<h3>New Survey</h3>
{/if}
<h4>{$response}</h4>

<form action="/ra/edit/surveysave/{$survey.surveyid}" method="post"
{if is_array($survey)}
 onsubmit="return selectall('sections');" 
{/if}
>
<input type="reset" value="reset form" />
Title: <input name="title" value="{$survey.title}" size="40"/>
<input type="checkbox" name="hide" value="1" {if $survey.hide}checked{/if}/> hide
<input type="submit" value="save survey" name="" />
&nbsp; <a href="/ra/data/survey/{$survey.surveyid}" target=_blank>json data</a>

{if is_array($survey)}
<h4>Sections:</h4>
<a href="/ra/edit/section?surveyid={$survey.surveyid}">Add a new section</a>
<table>
<tr>
<td colspan="2">
Not visible:
</td>
<td colspan="2">
Visible:
</td>
</tr>
<tr>
<td>

<select name="sectionsavail[]" id="sectionsavail" size="10" style="width: 400px;" multiple>
{foreach from=$sections id=i item=s}
{if $s.visible == 0}
<option value="{$s.sectionid}" title="{$s.sectionid} {$s.name}">
({$s.sectionid}) {$s.name}
</option>
{/if}
{/foreach}
</select>

<script type="text/javascript">
var avail = document.getElementById('sectionsavail');
avail.selectedIndex = 0;
</script>
</td>
<td>
<input type="button" onclick="swapsection('sectionsavail','sections');" value=">"/>
<br>
<input type="button" onclick="swapsection('sections','sectionsavail');" value="<"/>
</td>
<td>

<select name="sections[]" id="sections" size="10" style="width: 400px;" multiple>
{foreach from=$sections id=i item=s}
{if $s.visible == 1}
<option value="{$s.sectionid}" title="{$s.sectionid} {$s.name}">
({$s.sectionid}) {$s.name}
</option>
{/if}
{/foreach}
</select>

</td>
<td>
<input type="button" onclick="sectionup('sections');" value="up"/>
<br>
<input type="button" onclick="sectiondown('sections');" value="dn"/>
</td>
</tr>
</table>
{/if}

</form>
