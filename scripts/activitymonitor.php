<?php         

   if(isset($_POST["DateAndTime"]) && isset($_POST["SenderToken"]) && isset($_POST["SenderFirstname"]) && isset($_POST["SenderLastname"]) 
      && isset($_POST["RecipientFirstname"]) && isset($_POST["RecipientLastname"]) && isset($_POST["Filename"])){ 
        	
      echo "variables set successfully";

      $dateandtime = $_POST["DateAndTime"];
      
   	$sendertoken = $_POST["SenderToken"];

      $senderfirstname = $_POST["SenderFirstname"];

      $senderlastname = $_POST["SenderLastname"];

      $recipientfirstname = $_POST["RecipientFirstname"];

      $recipientlastname = $_POST["RecipientLastname"];   	   

      $filename = $_POST["Filename"];         

      $conn = mysqli_connect("localhost","root","","songmojo") or die("Error connecting");

      if(mysqli_connect_errno()){

         echo "Failed to connect to database: " . mysqli_connect_error();
      }
      else{

         echo "Successfully connected to database";
      }      
         
      $query = "INSERT INTO activity (DateAndTime,SenderToken,SenderFirstname,SenderLastname,RecipientFirstname,RecipientLastname,Filename) 
                VALUES ('$dateandtime', '$sendertoken', '$senderfirstname', '$senderlastname', '$recipientfirstname','$recipientlastname','$filename')";      

   	if(!mysqli_query($conn,$query)){

         echo "Error description: " . mysqli_error($conn);
      }
      else{

         echo "Successfully queried database";
      }            

   	mysqli_close($conn);
   }   
   else{

      echo "variables not set";
   }
?>