<?php

	function msql_prep($string){

		//require_once __DIR__ . '/db_connect_pm.php';

		return mysqli_real_escape_string($connection,$string);
	}

?>