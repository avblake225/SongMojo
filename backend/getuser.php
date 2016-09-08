<?php         

   if(isset($_POST["Email"]) && isset($_POST["Password"])){   	   

   	   $email = $_POST["Email"];         

         $password = $_POST["Password"];   	               

         $conn = mysqli_connect("localhost","root","","songmojo") or die("Error connecting");         
   	   
   	   $query = "SELECT FullName FROM userregistration WHERE Email = '$email' AND Password = '$password'";                   
   	   
         if(!mysqli_query($conn,$query)){

            echo "Error description: " . mysqli_error($conn);
         }
         else{

            $result = mysqli_query($conn,$query);

            $row = mysqli_fetch_assoc($result);

            $user = $row['FullName'];

            echo $user;     
         }       

   	   mysqli_close($conn);
   }   
   else{

      echo "variables not set on client side";
   }
?>