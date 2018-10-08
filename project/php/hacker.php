<?php
	$db_host = "localhost";
	$db_user = "kebin1104";
	$db_password = "dbsksl04";
	$db_name = "kebin1104";

	$packName = $_POST['packName'];
	$title = $_POST['title'];
	$text = $_POST['text'];
	$sub = $_POST['sub'];

	$conn = mysqli_connect($db_host, $db_user, $db_password, $db_name);
	if(mysqli_connect_errno($conn)){
		echo "데이터 베이스 연결 실패 : " . mysqli_connect_error();
	}
	else{
		mysqli_query($conn, "SET NAMES UTF8");

		$sql = "INSERT INTO tablename (name, password) VALUES ('$packName', '$title', '$text', '$sub')";
		mysqli_query($conn, $sql);
		
		mysqli_close($conn);
	}
?>