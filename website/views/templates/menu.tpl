{if $this->me}
<div class="menu">
<div class="menuitem menubold nohilite">Research Assistant:</div>
<div class="menuitem"><a href="/ra">home</a></div>
<div class="menuitem nohilite">logged in as: {$this->me.userid}</a></div>
<div class="menuitem"><a href="/ra/home/logout">logout</a></div>
<div class="menuitem nohilite">signature key:</div>
<div class="menuitem">
     <a id="sigkeyshow" href="javascript: void(0);" onclick="show_sigkey();">show</a>
     <span id="sigkey"></span></div>
</div>
<div class="menuitem"><a href="/ra/profile/resetkey">reset credentials</a></div>
</div>
<div class="menuend"></div>
{/if}

