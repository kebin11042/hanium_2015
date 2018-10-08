<?php

	header('Content-Type: application/json');

	$db_host = "localhost";
	$db_user = "kebin1104";
	$db_password = "dbsksl04";
	$db_name = "kebin1104";

	$db_table_multi_member = "multi_member";

	$member_id = $_POST['member_id'];
	$member_password = $_POST['member_password'];

	// $member_id = 'kebin1104';
	// $member_password = '1234';

	$json_result = array();

	$conn = mysqli_connect($db_host, $db_user, $db_password, $db_name);
	if(mysqli_connect_errno($conn)){
		echo "데이터 베이스 연결 실패 : " . mysqli_connect_error();
	}
	else{

		mysqli_query($conn, "SET NAMES UTF8");

		$sql = "SELECT * FROM ".$db_table_multi_member." WHERE name = '".$member_id."' AND password = '".$member_password."'";

		$result = mysqli_query($conn, $sql);

		$total_record = mysqli_num_rows($result);

		//로그인 성공
		if($total_record == 1){
			$json_result['result'] = 'ok';

			$row = mysqli_fetch_array($result);
			$member_id = $row[id];

			$json_result['member_id'] = $member_id;

			$sql = "SELECT * FROM multi_member_device WHERE member_id = ".$member_id;

			$result = mysqli_query($conn, $sql);
			$total_record2 = mysqli_num_rows($result);
			$device_list = array();
			for($i=0;$i<$total_record2;$i++){
				mysqli_data_seek($result, $i);
				$device_info = array();

				$row = mysqli_fetch_array($result);
				$multi_member_device_id = $row[id];
				$device_id = $row[device_id];
				$name = $row[name];
				
				$device_info['id'] = $multi_member_device_id;
				$device_info['device_id'] = $device_id;
				$device_info['name'] = $name;

				$device_list[] = $device_info;
			}

			$json_result['device_list'] = $device_list;
		}
		//실패
		else{
			$json_result['result'] = 'fail';
		}

		$json_result = json_encode($json_result);
		
		mysqli_close($conn);
	}

	echo $json_result;
?>