<?php
	$db_host = "localhost";
	$db_user = "kebin1104";
	$db_password = "dbsksl04";
	$db_name = "kebin1104";

	$mac_addr = $_GET['mac_addr'];

	$conn = mysqli_connect($db_host, $db_user, $db_password, $db_name);
	if(mysqli_connect_errno($conn)){
		echo "데이터 베이스 연결 실패 : " . mysqli_connect_error();
	}
	else{

		mysqli_query($conn, "SET NAMES UTF8");

		//SELECT * FROM multi_device d, multi_device_port p WHERE d.id = p.device_id AND d.mac_addr = '78-78-78-78-78-78' ORDER BY p.port_number ASC
		$sql = "SELECT * FROM multi_device d, multi_device_port p WHERE d.id = p.device_id AND d.mac_addr = '".$mac_addr."' ORDER BY p.port_number ASC";
		$result = mysqli_query($conn, $sql);

		$total_record = mysqli_num_rows($result);

		echo "start\n";
		for($i=0;$i<$total_record;$i++) {
			mysqli_data_seek($result, $i);

			$row = mysqli_fetch_array($result);
			
			$port_number = $row[port_number];
			$status = $row[status];

			echo $status;
		}
		
		mysqli_close($conn);
	}
?>