<?php         

   if(isset($_POST["SenderFirstname"]) && isset($_POST["RecipientToken"]) && isset($_POST["Filename"])){ 
         
      echo "variables set successfully";

      // Extract data      
      $senderfirstname = $_POST["SenderFirstname"];      

      $recipienttoken = $_POST["RecipientToken"];          

      $filename = $_POST["Filename"];               

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