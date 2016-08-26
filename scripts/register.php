<?php      

   var_dump(isset($_POST["Token"]));

   var_dump(isset($_POST["FirstName"]));

   var_dump(isset($_POST["LastName"]));

   echo "\n";

   if(isset($_POST["Token"]) && isset($_POST["FirstName"]) && isset($_POST["LastName"])){   	   

   	   $token = $_POST["Token"];

         echo "Received token\n";

         $firstName = $_POST["FirstName"];

   	   echo "Received first name\n";

         $lastName = $_POST["LastName"];

         echo "Received last name\n";

         $conn = mysqli_connect("localhost","root","","songmojo") or die("Error connecting");

         echo "Connected to database\n";
   	   
   	   $query = "INSERT INTO userregistration(Token,FirstName,LastName) VALUES ('$token','$firstName','$lastName')";

   	   mysqli_query($conn,$query);

   	   echo "Added data to database";

   	   mysqli_close($conn);
   }   
   else{

      echo "variables not set";
   }
?>