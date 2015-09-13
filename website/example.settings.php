<?php
# copy this file to .settings.php to use 
define('DBLOGIN','db user name');
define('DBPW','db password');

# you can override any constant defined in lib/init.php here

# debug output
# define('QUIET',false);

# salt file for the Login class
# see example.salt.php for structure
# if (file_exists('.salt.php')) define('SALTFILE','.salt.php');

#directories
# where the base models are (ie the ones that are automatically made by scripts)
# define('MODELSBASE','models/base');
# where the model subclasses are (hand written)
# define('MODELSDIR','models');
# where the controllers are
# define('CONTROLLERSDIR','controllers');
# where the view logic is
# define('VIEWDIR','views');
# where these libraries are
# define('LIBDIR','lib');
# where the base db libraries are
# define('DBDIR','db');
# where to put temporary files
# define('TMPDIR','/tmp');

#components
# parameter to use to determine controller "action"
# define('ACTION','action');
# default page to show if we don't know what visitor wants to do
# define('DEFCONTROLLER','home');
# object that manages password retrieval
# define('LOGINMODEL','User');
# define('LOGINFIELD','login');
# define('PWFIELD','password');
# define('PWDBFIELD','password');
# define('SIGFIELD','sig');
# object that manages login forms
# define('LOGINCONTROLLER','Loginform');
# key in the $_SESSION array for this login - this should be different for each site
# define('LOGINSESSION',dirname(__FILE__));

# optional wrapper template for views 
# define('VIEWWRAPPER','wrapper.tpl');

