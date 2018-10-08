<?php

	header('Content-Type: application/json');

	$db_host = "localhost";
	$db_user = "kebin1104";
	$db_password = "dbsksl04";
	$db_name = "kebin1104";


	$member_device_id = $_POST['member_device_id'];
	//$member_device_id = 7;

	$json_result = array();

	$conn = mysqli_connect($db_host, $db_user, $db_password, $db_name);
	if(mysqli_connect_errno($conn)){
		echo "데이터 베이스 연결 실패 : " . mysqli_connect_error();
	}
	else{

		mysqli_query($conn, "SET NAMES UTF8");

		$sql = "DELETE FROM multi_member_device WHERE id = ".$member_device_id;

		mysqli_query($conn, $sql);

		$json_result['result'] = 'ok';

		$json_result = json_encode($json_result);
		
		mysqli_close($conn);
	}

	echo $json_result;
?>

