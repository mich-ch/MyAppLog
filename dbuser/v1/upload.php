<?php

    require_once '../includes/DbOperValid.php';
    $response = array();

      if($_SERVER['REQUEST_METHOD'] == 'POST') {
          if(isset($_POST['id']) and isset($_POST['photo'])) {
              $db = new DbOperValid();

              $id = $_POST['id'];
              $photo = $_POST['photo'];

              if($db->userId($_POST['id'])) {
                $path = "profile_image/$id.jpeg";
                $finalPath = "http://192.168.1.3/dbuser/v1/".$path;
                $user = $db->getUserByIdPhoto($finalPath, $_POST['id']);
                file_put_contents($path, base64_decode($photo));
                $response['error'] = false;
                $response['id'] = $user['id'];
                $response['email'] = $user['email'];
                $response['username'] = $user['username'];
                $response['photo'] = $user['photo'];
                $response['message'] = "User registered successfully";
              }else{
                $response['error'] = true;
                $response['message'] = "No User registered successfully";
              }
          }else{
              $response['error'] = false;
              $response['message'] = "Required fields are missing";
          }
    }


    echo json_encode($response);

?>
