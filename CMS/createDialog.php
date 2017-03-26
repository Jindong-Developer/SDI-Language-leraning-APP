<?php
	if(isset($_POST['create'])){

		function checkRequiredValues(){
			$values = $_POST['line'];
			if($values[0]["question"] == null || $values[0]["answer"] == null ||$_POST['category']==null){
				return false;
			}
			return true;
		}

		function sendForm(){
			$values = $_POST['line'];
			$json = json_encode($values);
			$category = $_POST['category'];
			$url = "https://sdi-app-3059e.firebaseio.com/" . $category .".json?auth=km8DBBrMUiyudYPrmEDwL84cW6AeKvK9wAcTJ1UF";                                                                             
			callRestAPI($url, $json);
		}

		function callRestAPI($uri, $json) {
		    $headers = array (
		        "Content-Type: application/json; charset=utf-8",
		        "Content-Length: " .strlen($json) 
		    );
		     
		    $channel = curl_init($uri);
		    curl_setopt($channel, CURLOPT_CUSTOMREQUEST, "PUT");
		    curl_setopt($channel, CURLOPT_HTTPHEADER, $headers);
		    curl_setopt($channel, CURLOPT_POSTFIELDS, $json);
		    curl_setopt($channel, CURLOPT_SSL_VERIFYPEER, false);
		    curl_setopt($channel, CURLOPT_CONNECTTIMEOUT, 10);
		    
		    curl_exec($channel);
		    $statusCode = curl_getInfo($channel, CURLINFO_HTTP_CODE);
		    curl_close($channel);    
		    showResponseDialog($statusCode);
    	}

    	function showResponseDialog($statusCode){
    		if ($statusCode != 200) 
			{
			    echo '<script type="text/javascript">
				    alert("Upload fehlgeschlagen!");
				    history.back();
				 </script>';
			}else{
				echo '<script type="text/javascript">
				    alert("Upload erfolgreich!");
				    history.back();
				 </script>';
			}  
    	}

    	if(checkRequiredValues()){
			sendForm();
		}else{
		echo'<script type="text/javascript">
		    alert("Fehlende Eingaben! Bitte beachten Sie, dass mindestens die erste Zeile, sowie die Kategorie ausgef\u00fcllt werden muss.");
		    history.back();
		 </script>';
		}
	}
?>