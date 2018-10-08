<?php

	header('Content-Type: application/json');

	$db_host = "localhost";
	$db_user = "kebin1104";
	$db_password = "dbsksl04";
	$db_name = "kebin1104";

	$db_table_multi_member = "multi_member";

	$member_id = $_POST['member_id'];
	$member_password = $_POST['member_password'];

	$json_result = array();

	$conn = mysqli_connect($db_host, $db_user, $db_password, $db_name);
	if(mysqli_connect_errno($conn)){
		echo "데이터 베이스 연결 실패 : " . mysqli_connect_error();
	}
	else{

		mysqli_query($conn, "SET NAMES UTF8");

		$sql = "SELECT * FROM ".$db_table_multi_member." WHERE name = '".$member_id."'";

		$result = mysqli_query($conn, $sql);

		$total_record = mysqli_num_rows($result);

		if($total_record != 0){
			$json_result['result'] = 'overlap';
		}
		else{
			$json_result['result'] = 'ok';

			$sql = "INSERT INTO ".$db_table_multi_member." (name, password) VALUES ('$member_id', '$member_password')";

			$result = mysqli_query($conn, $sql);
		}

		$output = json_encode($json_result);
		
		mysqli_close($conn);
	}

	echo $output;
?>