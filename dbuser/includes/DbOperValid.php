<?php
  $responseErr = array();
  class DbOperValid{

      private $con;
      private $errUser, $errPass, $errEmail;

      function __construct() {
          require_once dirname(__FILE__).'/DbConnect.php';
          $db = new DbConnect();
          $this->con = $db->connect();
      }

      //register user into SQLiteDatabase
      public function createUser($username, $pass, $email) {
          if($this->isUserExist($username, $email)) {
              return 0;
          }else{
            //control if username password and email are DbOperValid
            //if correct then registration to the sytem else not
            if(($this->validEmail($email)) && ($this->validPassword($pass)) && ($this->validUsername($username))) {
                $password = md5($pass);
                $stmt = $this->con->prepare("INSERT INTO users (id, username, password, email) VALUES (NULL, ?, ?, ?);");
                $stmt->bind_param("sss", $username, $password, $email);
                if($stmt->execute()) {
                  return 1;
                }else{
                  return 2;
                }
            }else{
                return 2;
            }
              /*$password = md5($pass);
              $stmt = $this->con->prepare("INSERT INTO users (id, username, password, email) VALUES (NULL, ?, ?, ?);");
              $stmt->bind_param("sss", $username, $password, $email);
              if($stmt->execute()) {
                return 1;
              }else{
                return 2;
              }*/
          }
        }

        //login in database system
        public function userLogin($username, $pass) {
          $password = md5($pass);
          $stmt = $this->con->prepare("SELECT id FROM users WHERE username = ?
                                          AND password = ?");
           $stmt->bind_param("ss", $username, $password);
           $stmt->execute();
           $stmt->store_result();
           return $stmt->num_rows > 0;
        }

        //return if the id is correct
        public function userId($id) {
           $stmt = $this->con->prepare("SELECT * FROM users WHERE id = ?");
           $stmt->bind_param("i", $id);
           $stmt->execute();
           $stmt->store_result();
           return $stmt->num_rows > 0;
        }

        //system returns users where they change username and email
        public function getUserById($username, $email, $id) {
          $stmt = $this->con->prepare("UPDATE users SET username = ?, email = ? WHERE id= ? ");
          $stmt->bind_param("sss", $username, $email, $id);
          $stmt->execute();
          $result = $this->con->prepare("SELECT * FROM users WHERE id = ?");
          $result->bind_param("s", $id);
          $result->execute();
          return $result->get_result()->fetch_assoc();
        }

        //The system return users where they use username in order acces
        public function getUserByUsername($username) {
          $stmt = $this->con->prepare("SELECT * FROM users WHERE username = ?");
          $stmt->bind_param("s", $username);
          $stmt->execute();
          return $stmt->get_result()->fetch_assoc();
        }

        //Update user photo
        public function getUserByIdPhoto($photo, $id) {
          $stmt = $this->con->prepare("UPDATE users SET photo = ? WHERE id= ? ");
          $stmt->bind_param("ss", $photo, $id);
          $stmt->execute();
          $result = $this->con->prepare("SELECT * FROM users WHERE id = ?");
          $result->bind_param("s", $id);
          $result->execute();
          return $result->get_result()->fetch_assoc();
        }

        //valid or invalid username or password from db
        private function isUserExist($username, $email) {
          $stmt = $this->con->prepare("SELECT id FROM users WHERE username =?
                                      OR email = ?");
          $stmt->bind_param("ss", $username, $email);
          $stmt->execute();
          $stmt->store_result();
          return $stmt->num_rows > 0;
        }

        private function validEmail($email) {
            if (empty($email)) {
                $responseErr['error'] = true;
                $responseErr['mesEmail'] = "Error: Empty email";
                $this->setEmailErr($responseErr['mesEmail']);
                return false;
            }else {
                $emailCheck = $this->test_input($email);
                // check if e-mail address is well-formed
                if (!filter_var($emailCheck, FILTER_VALIDATE_EMAIL)) {
                    $responseErr['error'] = true;
                    $responseErr['mesEmail'] = "Error: Please no validate email";
                    $this->setEmailErr($responseErr['mesEmail']);
                    return false;
                }else {
                    $responseErr['mesEmail'] = "All good!";
                    $this->setEmailErr($responseErr['mesEmail']);
                    return true;
                }
            }
        }

        private function validUsername($username) {
            if (empty($username)) {
                 $responseErr['error'] = true;
                 $responseErr['mesUser'] = "Error: Empty username";
                 $this->setUserErr($responseErr['mesUser']);
                 return false;
            }else {
                 $nameCheck = $this->test_input($username);
                 // check if name only contains letters and whitespace
                 if (!preg_match("/^[a-zA-Z ]*$/",$nameCheck)) {
                    $responseErr['error'] = true;
                    $responseErr['mesUser'] = "Error: Please no validate username a-z A-Z";
                    $this->setUserErr($responseErr['mesUser']);
                    return false;
                 }else {
                   $responseErr['mesUser'] = "All good!";
                   $this->setUserErr($responseErr['mesUser']);
                    return true;
                 }
           }
        }

        private function validPassword($password) {
            if(empty($password)) {
                $responseErr['error'] = true;
                $responseErr['mesPass'] = "Error: Empty password";
                $this->setPassErr($responseErr['mesPass']);
                return false;
            }
            if (strlen($password) <= '8') {
              $responseErr['error'] = true;
              $responseErr['mesPass'] = "Error: Please try again password > 8";
              $this->setPassErr($responseErr['mesPass']);
                return false;
            }
            elseif(!preg_match("#[0-9]+#",$password)) {
              $responseErr['error'] = true;
              $responseErr['mesPass'] = "Error: Please try again password 0-9";
              $this->setPassErr($responseErr['mesPass']);
                return false;
            }
            elseif(!preg_match("#[A-Z]+#",$password)) {
              $responseErr['error'] = true;
              $responseErr['mesPass'] = "Error: Please try again password A-Z";
              $this->setPassErr($responseErr['mesPass']);
                return false;
            }
            elseif(!preg_match("#[a-z]+#",$password)) {
              $responseErr['error'] = true;
              $responseErr['mesPass'] = "Error: Please try again password a-z";
              $this->setPassErr($responseErr['mesPass']);
                return false;
            }else {
              $response['mesPass'] = "All good!";
              $this->setPassErr($responseErr['mesPass']);
                return true;
            }
        }

        function test_input($data) {
            $data = trim($data);
            $data = stripslashes($data);
            $data = htmlspecialchars($data);
            return $data;
        }

        public function setEmailErr($value) {
            $this->errEmail = $value;
        }

        public function getEmailErr() {
            return $this->errEmail;
        }

        public function setPassErr($value) {
            $this->errPass = $value;
        }

        public function getPassErr() {
            return $this->errPass;
        }

        public function setUserErr($value) {
            $this->errUser = $value;
        }

        public function getUserErr() {
            return $this->errUser;
        }

}

?>
