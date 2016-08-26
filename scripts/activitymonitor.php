<?php         

   if(isset($_POST["SenderToken"]) && isset($_POST["RecipientFirstname"]) && isset($_POST["RecipientLastname"]) && isset($_POST["Filename"])){ 
        	
      echo "variables set successfully";
      
   	$sendertoken = $_POST["SenderToken"];

      $recipientfirstname = $_POST["RecipientFirstname"];

      $recipientlastname = $_POST["RecipientLastname"];   	   

      $filename = $_POST["Filename"];         

      $conn = mysqli_connect("localhost","root","","songmojo") or die("Error connecting");

      echo "Connected to database";
         
      $query = "INSERT INTO activity(SenderToken,RecipientFirstname,RecipientLastname,Filename) VALUES ('$sendertoken','$recipientfirstname','$recipientlastname','$filename')";      

   	mysqli_query($conn,$query);

      echo "Queried database";

   	mysqli_close($conn);
   }   
   else{

      echo "variables not set";
   }
?>