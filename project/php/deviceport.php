<?php

	header('Content-Type: application/json');

	$db_host = "localhost";
	$db_user = "kebin1104";
	$db_password = "dbsksl04";
	$db_name = "kebin1104";


	$device_id = $_POST['device_id'];
	//$device_id = 1;

	$json_result = array();

	$conn = mysqli_connect($db_host, $db_user, $db_password, $db_name);
	if(mysqli_connect_errno($conn)){
		echo "데이터 베이스 연결 실패 : " . mysqli_connect_error();
	}
	else{

		mysqli_query($conn, "SET NAMES UTF8");

		$sql = "SELECT * FROM multi_device_port WHERE device_id = ".$device_id." ORDER BY port_number ASC";
		
		$result = mysqli_query($conn, $sql);
		
		$total_record = mysqli_num_rows($result);

		$port_list = array();
		for($i=0;$i<$total_record;$i++) {
			mysqli_data_seek($result, $i);
			$port_info = array();

			$row = mysqli_fetch_array($result);
			
			$port_id = $row[id];
			$port_number = $row[port_number];
			$status = $row[status];
			
			$port_info['id'] = $port_id;
			$port_info['port_number'] = $port_number;
			$port_info['status'] = $status;

			$port_list[] = $port_info;
		}

		$json_result['result'] = 'ok';
		$json_result['port_list'] = $port_list;


		$json_result = json_encode($json_result);
		
		mysqli_close($conn);
	}

	echo $json_result;
?>