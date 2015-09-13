<?php
# copy this file to .salt.php and change the SALT to improve security for the database
# the salt value is used in Login::encode($pw) which provides a basic encoding method for
# passwords - however you can encode them any way you want
define('SALT','some random bunch of characters');

# if these functions are used they should be customized 
# custom password function for this site
function encode_pw($password,$login='') 
{
	return sha1($password.SALT);
}

# make a hash with a nonce
function encode_nonce($encoded,$nonce)
{
	return sha1($encoded.$nonce.SALT);
}
