<?php
	require_once __DIR__ . '/db_config_pm.php';
	$connection =  mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_NAME);

	// Test for successful connection
	if(mysqli_connect_errno()){
        die("Database connection failed: " . mysqli_connect_error() . " ( " . mysqli_connect_errno . " ) ");
    }
 ?>