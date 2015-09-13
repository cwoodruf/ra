var SALT = '';

function encode_pw(pw,login) 
{
	// da39a3ee5e6b4b0d3255bfef95601890afd80709 is $.sha1('')
	return $.sha1(pw+login+'da39a3ee5e6b4b0d3255bfef95601890afd80709');
}

function encode_nonce(encoded,nonce)
{
	return $.sha1(nonce+encoded);
}

