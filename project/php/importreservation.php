<?php

	header('Content-Type: application/json');

	$db_host = "localhost";
	$db_user = "kebin1104";
	$db_password = "dbsksl04";
	$db_name = "kebin1104";

	$mac_addr = $_GET['mac_addr'];
	$port_number = $_GET['port_number'];

	$devie_id;

	$conn = mysqli_connect($db_host, $db_user, $db_password, $db_name);
	if(mysqli_connect_errno($conn)){
		echo "데이터 베이스 연결 실패 : " . mysqli_connect_error();
	}
	else{

		mysqli_query($conn, "SET NAMES UTF8");

		$sql = "DELETE FROM multi_member_device WHERE id = ".$member_device_id;

		$sql = "SELECT * FROM multi_device WHERE mac_addr = '".$mac_addr."'";
		$result = mysqli_query($conn, $sql);
		
		$row = mysqli_fetch_array($result);
		$devie_id = $row['id'];

		$sql = "SELECT * FROM multi_device_port WHERE device_id = ".$device_id." AND port_number = ".$port_number;
		$result = mysqli_query($conn, $sql);
		
		$row = mysqli_fetch_array($result);
		$port_id = $row['id'];

		$sql = "SELECT * FROM multi_port_reservation WHERE device_id = ".$device_id." AND port_id = ".$port_id;
		$result = mysqli_query($conn, $sql);
		$total_record = mysqli_num_rows($result);

		if($total_record >= 1){
			$row = mysqli_fetch_array($result);
			echo 
		}

		$json_result['result'] = 'ok';



		$json_result = json_encode($json_result);
		
		mysqli_close($conn);
	}

	echo $json_result;
?>

