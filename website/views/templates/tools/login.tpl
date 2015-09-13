<div>
{literal}
<form id="form_login" action="index.php" method="get"
      onsubmit="
var url='/ra/index.php?action=loginform/ajaxcheck&login='+
	login.value+'&password='+password.value;
$.get(
	url,
	function (data) {
		if (data == 'OK') {
			window.location = 'index.php';
		} else {
			alert('Login failed. '+data);
		}
	}
); return false;"
>
{/literal}
{login_params}
<table cellpadding="3" cellspacing="0" border="0">
<tr><td><b>Login:</b></td>
    <td><input name="login" size="30" maxlength="64" value="{$login}" />
    <script type="text/javascript">document.getElementById("form_login").login.focus()</script></td></tr>
<tr><td><b>Password:</b></td>
    <td><input type="password" name="password" size="30" maxlength="64" /></td></tr>
<tr><td><input type="reset" value='Reset' /></td>
    <td align="right"><input type="submit" value="Log In" /></td></tr>
</table>
</form>
</div>
