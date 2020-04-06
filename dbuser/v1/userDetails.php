<?php

    require_once '../includes/DbOperValid.php';
    $response = array();

      if($_SERVER['REQUEST_METHOD'] == 'POST') {
          if(isset($_POST['id']) and isset($_POST['username']) and isset($_POST['email'])) {
              $db = new DbOperValid();

              if($db->userId($_POST['id'])) {
                $user = $db->getUserById($_POST['username'], $_POST['email'], $_POST['id']);
                $response['error'] = false;
                $response['id'] = $user['id'];
                $response['email'] = $user['email'];
                $response['username'] = $user['username'];
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
