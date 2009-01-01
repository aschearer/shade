<?php
/** Return a list of top scores. */
// read variables from GET data
$num_scores = @$_GET['num_scores'];

/* Replace this w/ real information. */
$dsn = 'mysql:dbname=[dbname];host=[dbhost]';
$user = '[dbuser]';
$pass = '[dbpass]';

try {
    $dbh = new PDO($dsn, $user, $pass);
} catch (PDOException $e) {
    echo "";
    exit;
}

$q = 'SELECT name, score, clear FROM `scores` ORDER BY `score` DESC';

if (isset($num_scores)) {
	$num_scores = intval($num_scores);
    $q .= ' LIMIT 0, ?';
}

$stmt = $dbh->prepare($q);
$stmt->bindParam(1, $num_scores, PDO::PARAM_INT);
$stmt->execute();

$scores = $stmt->fetchAll(PDO::FETCH_ASSOC);

$out = fopen("php://output", "w");

foreach ($scores as $score) {
	fputcsv($out, $score);
}
?>
