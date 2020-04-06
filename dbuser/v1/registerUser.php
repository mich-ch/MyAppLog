<?php

    require_once '../includes/DbOperValid.php';
    $response = array();

    if($_SERVER['REQUEST_METHOD'] == 'POST') {
        if(isset($_POST['username']) and isset($_POST['email']) and isset($_POST['password'])) {
            $db = new DbOperValid();

            $result = $db->createUser($_POST['username'], $_POST['password'], $_POST['email']);
            $response['mesEm'] = $db->getEmailErr();
            $response['mesPas'] = $db->getPassErr();
            $response['mesUser'] = $db->getUserErr();
            if($result == 1)  {
              $response['error'] = false;
              $response['message'] = "User registered successfully";
            }elseif($result == 2){
              $response['error'] = true;
              $response['message'] = "Error: Please try again";
            }elseif($result == 0) {
              $response['error'] = true;
              $response['message'] = "Error: User already registered fot this email  ";
            }
            if($db->userLogin($_POST['username'], $_POST['password'])) {
              $user = $db->getUserByUsername($_POST['username']);
              $response['error'] = false;
              $response['id'] = $user['id'];
              $response['email'] = $user['email'];
              $response['username'] = $user['username'];
            }else{
              $response['error'] = true;
              $response['message'] = "Invalid username or password";
            }
        }else{
            $response['error'] = true;
            $response['message'] = "Error: Required fields are missing";
        }
    }else{
        $response['error'] = true;
        $response['message'] = "Invalid Request";
    }

    echo json_encode($response);
?>
