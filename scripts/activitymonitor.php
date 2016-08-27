<?php         

   if(isset($_POST["DateAndTime"]) && isset($_POST["SenderToken"]) && isset($_POST["RecipientFirstname"]) && isset($_POST["RecipientLastname"]) && isset($_POST["Filename"])){ 
        	
      echo "variables set successfully";

      $dateAndTime = $_POST["DateAndTime"];
      
   	$sendertoken = $_POST["SenderToken"];

      $recipientfirstname = $_POST["RecipientFirstname"];

      $recipientlastname = $_POST["RecipientLastname"];   	   

      $filename = $_POST["Filename"];         

      $conn = mysqli_connect("localhost","root","","songmojo") or die("Error connecting");

      echo "Connected to database";
         
      $query = "INSERT INTO activity(DateAndTime,SenderToken,RecipientFirstname,RecipientLastname,Filename) VALUES ('$dateAndTime', $sendertoken','$recipientfirstname','$recipientlastname','$filename')";      

   	mysqli_query($conn,$query);

      echo "Queried database";

   	mysqli_close($conn);
   }   
   else{

      echo "variables not set";
   }
?>