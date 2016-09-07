<?php         

   if(isset($_POST["NewToken"]) && isset($_POST["FirstName"]) && isset($_POST["LastName"])){ 
        	
      echo "variables set successfully";

      // Extract data
      $newtoken = $_POST["NewToken"];
      
   	$firstname = $_POST["FirstName"];

      $lastname = $_POST["LastName"];             

      // Open database
      $conn = mysqli_connect("localhost","root","","songmojo") or die("Error connecting");

      if(mysqli_connect_errno()){

         echo "Failed to connect to database: " . mysqli_connect_error();
      }
      else{

         echo "Successfully connected to database";
      }           
         
      // Update token
      $query = "UPDATE userregistration SET Token = '$newtoken' WHERE FirstName = '$firstname' AND LastName = '$lastname'";

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