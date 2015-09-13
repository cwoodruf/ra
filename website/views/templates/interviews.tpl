<h3>Interviews for survey {$surveyid} section {$sectionid} 
    (<a href="/ra/edit/interviews/{$surveyid}/{$sectionid}">refresh</a>)</h3>
<h4>{$response}</h4>
<h4>SPSS</h4>
<a href="/ra/dump/spss/csv/{$surveyid}/{$sectionid}">CSV data</a> -
<a href="/ra/dump/spss/tab/{$surveyid}/{$sectionid}">TAB data</a> -
<a href="/ra/dump/spss/labels/{$surveyid}/{$sectionid}" target=_blank>VARIABLE LABELS for TAB data</a> -
<a href="/ra/survey/spss.html" target=_blank>SPSS Howto</a>

<h4>Cases</h4>
<table cellpadding="5" cellspacing="0" border="1">
{if $cases}
<tr>
<th>Participant</th>
<th>Interviewed</th>
<th colspan="2">&nbsp;</th>
</tr>
{/if}
{foreach from=$cases key=i item=case}
<tr>
<td>{$case.partid}</td>
<td>{$case.modified}</td>
<td>
<a href="javascript: void(0);" onclick="$('#data{$surveyid}s{$sectionid}p{$case.partid}').toggle();">data</a>
<div id="data{$surveyid}s{$sectionid}p{$case.partid}" 
     style="display: none;">{$case.state|wordwrap:40:"<br>":true}</div>
</td>
<td>
<a href="/ra/edit/interview/delete/{$surveyid}/{$sectionid}/{$case.partid}"
       onclick="confirm('Delete this interview?');">delete</a>
</td>
</tr>
{foreachelse}
<tr><td>No cases found</td></tr>
{/foreach}
</table>

