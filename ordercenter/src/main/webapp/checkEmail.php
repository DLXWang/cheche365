<?php
  /*$conn=mysql_connect('localhost','root','123');
  mysql_select_db('renren-db',$conn);*/
/*  $email=$_POST['email'];
  $sql="SELECT * FROM user WHERE email='".$email."'";
  $rs=mysql_query($sql);
  if(mysql_num_rows($rs)>0){
   echo 'no';
  }else{
   echo 'yes';
  }*/
  $email=$_POST['email'];
  if($email=='lina@sohu.com'){
    echo 'no';
  }else{
    echo 'yes';
  }
?>