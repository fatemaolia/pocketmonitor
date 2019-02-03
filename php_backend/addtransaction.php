<?php
	if($_SERVER['REQUEST_METHOD']=='POST'){
		if($_POST['APIkey']=="a199d4a58f507e9f22f5494ad3964bc1ec723d52"){

			//!!!date format should be yyyy-mm-dd

			//include db connect class
		    require_once __DIR__ . '/db_connect_pm.php';

		    $t_date = $_POST['t_date'];
		    $type = $_POST['type'];
		    $info = $_POST['info'];
		    $amount = $_POST['amount'];
		    $user_fk = $_POST['email'];

		    // get old balance and update 
		    $query_getbal = "SELECT balance FROM user WHERE email='$user_fk'";
		    $result_getbal=mysqli_query($connection,$query_getbal);
		    if($result_getbal->num_rows!=0){
				while($row = $result_getbal->fetch_assoc()) {
					$balance = $row["balance"] + floatval($amount);
					


					// add transaction
					$query_tran = "INSERT INTO transactions (transaction_key,t_date, type, info, amount, user_fk) VALUES ( NULL, '$t_date', '$type', '$info', '$amount', '$user_fk')";
				    $result_tran=mysqli_query($connection,$query_tran);
				    if($result_tran){
				    	
				    	// update balance
				    	$query_putbal = "UPDATE user SET balance='$balance' WHERE email='$user_fk'";
				    	$result_putbal = mysqli_query($connection,$query_putbal);
				    	if($result_putbal){
				    		echo "Success";
				    	} else {
				    		echo "Failure 1";
				    	}
				    } else{
				    	echo "Failure 2";
			    	}
		    	}
			} else {
				echo "Failure 3";
			} 
		    
		}
	}
?>