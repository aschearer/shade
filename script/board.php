<?php
/** Return a list of top scores. */
// read variables from GET data
$num_scores = @$_GET['num_scores'];

$dsn = 'mysql:dbname=shadow_play;host=mysql.anotherearlymorning.com';
$user = 'catch22';
$pass = 'divein';

try {
    $dbh = new PDO($dsn, $user, $pass);
} catch (PDOException $e) {
    echo "";
    exit;
}

$q = 'SELECT clear, name, score FROM `scores` ORDER BY `score` DESC';

if (isset($num_scores)) {
	$num_scores = intval($num_scores);
    $q .= ' LIMIT 0, ?';
}

$stmt = $dbh->prepare($q);
$stmt->bindParam(1, $num_scores, PDO::PARAM_INT);
$stmt->execute();

$scores = $stmt->fetchAll(PDO::FETCH_ASSOC);

$out = fopen("php://output", "r");

foreach ($scores as $score) {
	//echo implode(",", $score);
	//echo "\n";
	//echo "$score\n";
	fputcsv($out, $score);
}
?>
