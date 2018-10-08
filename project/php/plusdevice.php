<?php

	header('Content-Type: application/json');

	$db_host = "localhost";
	$db_user = "kebin1104";
	$db_password = "dbsksl04";
	$db_name = "kebin1104";


	$member_id = $_POST['member_id'];
	$device_mac = $_POST['device_mac'];
	$device_name = $_POST['device_name'];

	$json_result = array();

	$conn = mysqli_connect($db_host, $db_user, $db_password, $db_name);
	if(mysqli_connect_errno($conn)){
		echo "데이터 베이스 연결 실패 : " . mysqli_connect_error();
	}
	else{

		mysqli_query($conn, "SET NAMES UTF8");

		$sql = "SELECT * FROM multi_device WHERE mac_addr = '".$device_mac."'";
		$result = mysqli_query($conn, $sql);
		$total_record = mysqli_num_rows($result);

		//장비 없음
		if($total_record == 0){
			$json_result['result'] = 'nothing';
		}
		//장비 있음
		else{
			$row = mysqli_fetch_array($result);
			$device_id = $row[id];

			$sql = "SELECT * FROM multi_member_device WHERE member_id = ".$member_id." AND device_id = ".$device_id;
			$result = mysqli_query($conn, $sql);
			$total_record = mysqli_num_rows($result);

			//사용자가 이미 추가함
			if($total_record != 0){
				$json_result['result'] = 'already';
			}
			//할꺼임
			else{
				$json_result['result'] = 'ok';
				
				$json_member_device = array();

				$sql = "INSERT INTO multi_member_device (member_id, device_id, name) VALUES ('$member_id', '$device_id', '$device_name')";
				$result = mysqli_query($conn, $sql);
				$member_device_id = mysqli_insert_id($conn);

				$json_member_device['id'] = $member_device_id;
				$json_member_device['device_id'] = $device_id;
				$json_member_device['name'] = $device_name;

				$json_result['member_device_info'] = $json_member_device;
			}
		}

		$json_result = json_encode($json_result);
		
		mysqli_close($conn);
	}

	echo $json_result;
?>