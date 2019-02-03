<?php
	
	if($_SERVER['REQUEST_METHOD']=='POST'){

		if($_POST['APIkey']=="a199d4a58f507e9f22f5494ad3964bc1ec723d52"){

			//include db connect class
		    require_once __DIR__ . '/db_connect_pm.php';
		    //require_once __DIR__ . '/functions.php';

			$name =  $_POST['name'];
			$password = md5($_POST['password']);
			$email = $_POST['email'];

			

		    $query_check = "SELECT * FROM user WHERE email = '$email'";
		    $result_check= mysqli_query($connection,$query_check);
		    if($result_check->num_rows==0){

				$query_add = "INSERT INTO user (name, email, password) VALUES ('$name', '$email', '$password')";
				$result_add= mysqli_query($connection,$query_add);
			
				if($result_add){
					echo "Yes";
				}
			} else {
				echo "User exists";
			}
		}
	}
?>