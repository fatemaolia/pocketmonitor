<?php
	if($_SERVER['REQUEST_METHOD'] == 'POST'){

		if($_POST['APIkey']=="a199d4a58f507e9f22f5494ad3964bc1ec723d52"){
			
			require_once __DIR__ . '/db_connect_pm.php';

			$email = $_POST['email'];
			$password = md5($_POST['password']);
			
			$query_find = "SELECT name, balance FROM user WHERE email='$email' AND password='$password'";
			$result_find = mysqli_query($connection,$query_find);
			

			if($result_find->num_rows!=0){
				while($row = $result_find->fetch_assoc()) {
					echo $row["balance"];
					echo ",";
					echo $row["name"];
				}
				echo ",Yes";
			} else{

				echo "No,No,No";
			}
		}
	}
?>