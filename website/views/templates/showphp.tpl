<h4>{$response}</h4>

<div id="{$sectiontag}" class="section">
{if is_array($questions)}
{$questions.sectionname}
<a class="expandall" href="javascript:void(0);" 
   onclick="$('.answers,.answerscontext,.questioncontext').show(); 
            $('.expand').hide(); $('.hide').show();">expand all</a>

<a class="hideall" href="javascript:void(0);" 
   onclick="$('.questioncontext,.answerscontext,.answers').hide(); 
            $('.expand').show(); $('.hide').hide();">hide all</a>

<a class="hideall expandall" href="javascript:void(0);" onclick="aunsetall();">unset all answers</a>
{/if}

<a class="hideall expandall" href="/ra/edit/section/{$sectionid}">edit section</a>

{$htmltree}

</div>
<pre>
{$context}
</pre>

