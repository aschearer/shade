<?php
/** Read in a CSV file and save the rows. */
// read CSV data from POST.
$scores = @$_POST['scores'];

/* Replace this w/ real information. */
$dsn = 'mysql:dbname=[dbname];host=[dbhost]';
$user = '[dbuser]';
$pass = '[dbpass]';

if (!isset($scores)) {
	echo "failure";
	exit;
}

try {
    $dbh = new PDO($dsn, $user, $pass);
} catch (PDOException $e) {
	echo "failure";
    exit;
}

// TODO start transaction
$q = 'INSERT INTO `scores` (clear, name, score) VALUES (?, ?, ?)';
$stmt = $dbh->prepare($q);

// Get a file pointer to the score data.
$fp = fopen('php://memory', 'r+');
fputs($fp, $scores);
rewind($fp);

try {
	$dbh->beginTransaction();
	while (($score = fgetcsv($fp, 1000, ',')) !== FALSE) {
    	$stmt->bindParam(1, $score[2], PDO::PARAM_INT);
	    $stmt->bindParam(2, $score[0], PDO::PARAM_STR);
    	$stmt->bindParam(3, $score[1], PDO::PARAM_INT);
	    if (!$stmt->execute()) {
    	    // TODO rollback transaction
			echo "failure";
			exit;
    	}
	}
	$dbh->commit();
} catch (Exception $e) {
	$dbh->rollBack();
	echo "failure"; // failed somewhere along the way
	exit;
}

echo "success";
?>