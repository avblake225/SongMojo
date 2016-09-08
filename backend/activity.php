<?php         

   if(isset($_POST["DateAndTime"]) && isset($_POST["SenderToken"]) && isset($_POST["SenderFirstname"]) && isset($_POST["SenderLastname"]) 
      && isset($_POST["RecipientFirstname"]) && isset($_POST["RecipientLastname"]) && isset($_POST["FileName"])
      && isset($_POST["FileType"]) && isset($_POST["Duration"])){ 
        	
      echo "variables set successfully";

      // Extract data
      $dateandtime = $_POST["DateAndTime"];
      
   	  $sendertoken = $_POST["SenderToken"];

      $senderfirstname = $_POST["SenderFirstname"];

      $senderlastname = $_POST["SenderLastname"];

      $recipientfirstname = $_POST["RecipientFirstname"];

      $recipientlastname = $_POST["RecipientLastname"];   	   

      $filename = $_POST["FileName"];

      $filetype = $_POST["FileType"];

      $duration = $_POST["Duration"];         

      // Open database
      $conn = mysqli_connect("localhost","root","","songmojo") or die("Error connecting");

      if(mysqli_connect_errno()){

         echo "Failed to connect to database: " . mysqli_connect_error();
      }
      else{

         echo "Successfully connected to database";
      }   

      // Get recipient token
      $tokenQuery = "SELECT Token FROM userregistration WHERE FirstName = '$recipientfirstname' AND LastName = '$recipientlastname'";

      if(!mysqli_query($conn,$tokenQuery)){

         echo "Error description: " . mysqli_error($conn);
      }
      else{

         $result = mysqli_query($conn,$tokenQuery);

         $row = mysqli_fetch_assoc($result);

         $recipienttoken = $row['Token'];

         echo "Recipient token: " . $recipienttoken;     
      }            
         
      // Record activity
      $query = "INSERT INTO activity (DateAndTime,SenderToken,SenderFirstname,SenderLastname,RecipientToken,RecipientFirstname,RecipientLastname,FileName,FileType,Duration) 
                VALUES ('$dateandtime', '$sendertoken', '$senderfirstname', '$senderlastname', '$recipienttoken', '$recipientfirstname','$recipientlastname','$filename','$filetype','$duration')";            

   	  if(!mysqli_query($conn,$query)){

         echo "Error description: " . mysqli_error($conn);
      }
      else{

         echo "Successfully queried database";
      }                  
      
      // Close database
   	  mysqli_close($conn);      

      // Send notification
      $tokens[] = $recipienttoken;

      $message_str = "$senderfirstname just sent you $filename";

      $message = array("message" => $message_str);

      $message_status = send_notification($tokens, $message);

      echo $message_status;
   }   
   else{

      echo "variables not set";
   }

   function send_notification($tokens,$message){

        $url = 'http://fcm.googleapis.com/fcm/send';

        $fields = array(

         'registration_ids' => $tokens,
         'data' => $message
         );

        $headers = array(
         'Authorization:key = AIzaSyDfoty7pijQjhojBaJClEI_-kw4hsKGwqw',
         'Content-Type: application/json'
         );

        // use "curl" library to make network request
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 0);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));

        $result = curl_exec($ch);

        if($result === FALSE){

            die('Curl failed: '. curl_error($ch));
        }

        curl_close($ch);

        return $result;
   }
?>