<?php         

   if(isset($_POST["Token"]) && isset($_POST["FirstName"]) && isset($_POST["LastName"]) 
         && isset($_POST["FullName"]) && isset($_POST["Email"]) && isset($_POST["Password"])){   	   

   	   $token = $_POST["Token"];         

         $firstName = $_POST["FirstName"];   	

         $lastName = $_POST["LastName"];

         $fullName = $_POST["FullName"];

         $email = $_POST["Email"];

         $password = $_POST["Password"];

         echo "Set variables ";

         $conn = mysqli_connect("localhost","root","","songmojo") or die("Error connecting");

         echo "Connected to database ";
   	   
   	   $query = "INSERT INTO userregistration(Token,FirstName,LastName,FullName,Email,Password) 
                   VALUES ('$token','$firstName','$lastName','$fullName','$email','$password')";

   	   mysqli_query($conn,$query);

   	   echo "Added data to database";

   	   mysqli_close($conn);
   }   
   else{

      echo "variables not set on client side";
   }
?>