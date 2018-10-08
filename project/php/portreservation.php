<?php

	header('Content-Type: application/json');

	$db_host = "localhost";
	$db_user = "kebin1104";
	$db_password = "dbsksl04";
	$db_name = "kebin1104";


	$member_id = $_POST['member_id'];
	$device_mac = $_POST['device_mac'];
	$device_name = $_POST['device_name'];

	$device_id = $_POST['device_id'];
	$port_id = $_POST['port_id'];
	$year = $_POST['year'];
	$month = $_POST['month'];
	$day = $_POST['day'];
	$hour = $_POST['hour'];
	$min = $_POST['min'];

	$json_result = array();

	$conn = mysqli_connect($db_host, $db_user, $db_password, $db_name);
	if(mysqli_connect_errno($conn)){
		echo "데이터 베이스 연결 실패 : " . mysqli_connect_error();
	}
	else{

		mysqli_query($conn, "SET NAMES UTF8");

		$sql = "INSERT INTO multi_port_reservation (device_id, port_id, year, month, day, hour, min) VALUES ('$device_id', '$port_id', '$year', '$month', '$day', '$hour', '$min')";

		mysqli_query($conn, $sql);

		$json_result['result'] = 'ok';

		$json_result = json_encode($json_result);
		
		mysqli_close($conn);
	}

	echo $json_result;
?>