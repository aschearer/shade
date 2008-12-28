<?php
/** Read in a username and score and save them to a file. */
// read variables from POST data.
$username = @$_POST['name'];
$score = @$_POST['score'];
$cleared = @$_POST['clear'];

/* Replace this w/ real information. */
$dsn = 'mysql:dbname=[dbname];host=[dbhost]';
$user = '[dbuser]';
$pass = '[dbpass]';

try {
    $dbh = new PDO($dsn, $user, $pass);
} catch (PDOException $e) {
	echo "failure";
    exit;
}

// verify the username is set and not empty
if (!isset($username) || $username == "") {
	echo "failure";
    exit;
}

// verify the score is set and a number
if (!isset($score) || !is_numeric($score)) {
	echo "failure";
    exit;
}

if (!isset($cleared) || !($cleared == 1 || $cleared == 0)) {
	echo "failure";
	exit;
}

$q = 'INSERT INTO `scores` (clear, name, score) VALUES (?, ?, ?)';

$stmt = $dbh->prepare($q);
$stmt->bindParam(1, $cleared, PDO::PARAM_INT);
$stmt->bindParam(2, $username, PDO::PARAM_STR);
$stmt->bindParam(3, $score, PDO::PARAM_INT);

if (!$stmt->execute()) {
	echo "failure";
	exit;
}

echo "success";
?>
