<?php
	if($_SERVER['REQUEST_METHOD']=='POST'){
		if($_POST['APIkey']=="a199d4a58f507e9f22f5494ad3964bc1ec723d52"){

			require_once __DIR__ . '/db_connect_pm.php';

			$user_fk = $_POST['email'];

			$query = "SELECT t_date, type, info, amount FROM transactions WHERE user_fk='$user_fk'";
			$result = mysqli_query($connection,$query);

			$resultArray = array();

			if($result){
				while($row = $result->fetch_assoc()){
					$resultArray[] = $row;
				}

				echo json_encode($resultArray);

			}
		}
	}
?>