<?php         

   if(isset($_POST["FullName"])){   	   

   	   $fullname = $_POST["FullName"];                     	              

         $conn = mysqli_connect("localhost","root","","songmojo") or die("Error connecting");         
   	   
   	   $query = "SELECT * FROM userregistration WHERE FullName LIKE '$fullname'";                   
   	   
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