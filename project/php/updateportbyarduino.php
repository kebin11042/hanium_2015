<?php
	$db_host = "localhost";
	$db_user = "kebin1104";
	$db_password = "dbsksl04";
	$db_name = "kebin1104";

	$mac_addr = $_GET['mac_addr'];
	$port_number = $_GET['port_number'];
	$status = $_GET['status'];

	$conn = mysqli_connect($db_host, $db_user, $db_password, $db_name);
	if(mysqli_connect_errno($conn)){
		echo "데이터 베이스 연결 실패 : " . mysqli_connect_error();
	}
	else{

		mysqli_query($conn, "SET NAMES UTF8");

		//SELECT * FROM multi_device d, multi_device_port p WHERE d.id = p.device_id AND d.mac_addr = '78-78-78-78-78-78' ORDER BY p.port_number ASC
		$sql = "SELECT * FROM multi_device WHERE mac_addr = '".$mac_addr."'";
		$result = mysqli_query($conn, $sql);
		$row = mysqli_fetch_array($result);

		$device_id = $row[id];

		//$sql = "UPDATE multi_device_port SET status = ".$status." WHERE id = ".$port_id;
		$sql = "UPDATE multi_device_port SET status = ".$status." WHERE device_id = ".$device_id." AND port_number=".$port_number;		
		mysqli_query($conn, $sql);
		
		mysqli_close($conn);
	}
?>