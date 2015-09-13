<a href="/ra/edit/survey">Add a new survey</a> - 
<a href="/ra/data/surveys" target=_blank>surveys json data</a>

<table cellpadding="5" cellspacing="0" border="1">
<tr>
<th>Survey</th>
<th>Section</th>
<th>Tools</th>
</tr>

{foreach from=$sections key=i item=s}

{if $s.hide}
{assign var=trcolor value=lightblue}
{else}
{if $s.surveyid != $sid}
{if $trcolor != cornsilk}
{assign var=trcolor value=cornsilk}
{else}
{assign var=trcolor value=white}
{/if}
{/if}
{/if}

<tr bgcolor="{$trcolor}">
<td>{$s.surveyid} {$s.title} {if $s.hide}(hidden){/if}</td>
<td>{$s.sectionid} {$s.name|default:'no sections for this survey'} {if $s and !$s.visible}(hidden){/if}</td>
<td align="right">
{if $s.surveyid != $sid}
<a href="/ra/edit/survey/{$s.surveyid}">edit survey {$s.surveyid}</a> - 
<a href="/ra/edit/section?surveyid={$s.surveyid}">add section</a>
{/if}
&nbsp;
{if $s.sectionid}
Section {$s.sectionid}:
<a href="/ra/test/js/{$s.sectionid}">mobile view</a> -
<a href="/ra/test/php/{$s.sectionid}">web view</a> -
<a href="/ra/data/section/{$s.sectionid}" target=_blank>json</a> -
<a href="/ra/edit/section/{$s.sectionid}">edit section</a> -
<a href="/ra/edit/interviews/{$s.surveyid}/{$s.sectionid}">interviews</a>
{/if}
</td>
</tr>
{assign var=sid value=$s.surveyid}

{foreachelse}

<tr>
<td colspan="4">
No surveys found
</td>
</tr>

{/foreach}

</table>
