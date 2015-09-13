<h3>Reset signature key for {$this->me.userid}</h3>
<div style="margin-top: -18px; padding-bottom: 10px; font-style: italic;">
The signature key is needed to authenticate network updates from mobile devices.
</div>

<h4>{$response}</h4>
{if !$response}
<form action="/ra/profile/resetkey" method="post">
<table>
<tr>
<td>
<b>Current password (required)</b></td><td><input type="password" name="{$pwfield}" size="30">
</td>
</tr>
<tr>
<td>

<div style="padding-top: 20px; font-style: italic;">Optionally, create new password</div>
</td>
</tr>
<tr>
<td>

New password </td><td><input type="password" name="newpw" size="30">
</td>
</tr>
<tr>
<td>

Repeat new password </td><td><input type="password" name="newpw2" size="30">
</td>
</tr>
<tr>
<td colspan="2" align="right">
<input type="submit" name="" value="update credentials for {$this->me.userid}">
</td>
</tr>
</table>
</form>
{/if}
