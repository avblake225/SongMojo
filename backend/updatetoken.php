<?php         

   if(isset($_POST["NewToken"]) && isset($_POST["Email"]) && isset($_POST["Password"])){ 
        	
      echo "variables set successfully";

      // Extract data
      $newtoken = $_POST["NewToken"];
      
   	$email = $_POST["Email"];

      $password = $_POST["Password"];             

      // Open database
      $conn = mysqli_connect("localhost","tonyonan_me","me2016","tonyonan_songmojo");

      if(mysqli_connect_errno()){

         echo "Failed to connect to database: " . mysqli_connect_error();
      }
      else{

         echo "Successfully connected to database";
      }           
         
      // Update token
      $query = "UPDATE userregistration SET Token = '$newtoken' WHERE Email = '$email' AND Password = '$password'";

   	if(!mysqli_query($conn,$query)){

         echo "Error description: " . mysqli_error($conn);
      }
      else{

         echo "Successfully queried database";
      }                  
      
      // Close database
   	mysqli_close($conn); 
   }           
?>