<?php

/* List of possible pages to display. */
$pages = array();
$pages['normal']  = 'form.html';
$pages['error']   = 'failure.html';
$pages['complete'] = 'success.html';

/* Current page to display. */
/* Should load page's data into $content to display. */
$page = $pages['normal'];

/* Attempt to read in data submitted by the user. */
$rating = @$_POST['rating'];
$comments = @$_POST['comments'];
$contact = @$_POST['contact'];

try {
    if (!empty($_POST)) {
        if (!isset($rating) || $rating < 0 || $rating > 5) {
            throw new Exception("Rating not valid.");
        }

        /* Replace this w/ real information. */
        $dsn = 'mysql:dbname=[dbname];host=[dbhost]';
        $user = '[dbuser]';
        $pass = '[dbpass]';

        $dbh = new PDO($dsn, $user, $pass);

        $q = 'INSERT INTO `feedback` (rating, comments, contact) VALUES (?, ?, ?)';
        $stmt = $dbh->prepare($q);
        $stmt->bindParam(1, $rating, PDO::PARAM_INT);
        $stmt->bindParam(2, $comments, PDO::PARAM_STR);
        $stmt->bindParam(3, $contact, PDO::PARAM_STR);

        if (!$stmt->execute()) {
            throw new Exception("Failed to write to database.");
        }

        $page = $pages['complete'];
    }

} catch (Exception $e) {
    $page = $pages['error'];
}

$content = file_get_contents($page);

?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
    <head>
        <title>Shade Feedback</title>
        <link href="feedback.css" rel="stylesheet" type="text/css">
    </head>
    <body>
        <?php echo $content; ?>
        <div id="footer" />
    </body>
</html>
