<!DOCTYPE html>

<html>
    <head>
        <title>SDI CMS</title>
       <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min.js"></script>
            <!-- Latest compiled and minified CSS -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">

            <!-- Optional theme -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">

            <!-- Latest compiled and minified JavaScript -->
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
        
        <link rel="stylesheet" type="text/css" href="stylesheets/styles.css">
    </head>
    <body>
        <?php include("createDialog.php"); ?>
        <!--@content-->
        <div class="header-container">
        <img class="image" src="assets/sdimuc_logo_mit-claim_large.png" alt="SDI Logo">
        <div class="background header-box">
            <div id="header" class="page-header background">
                <h1>SDI-APP<small>Content Managment System</small></h1>
            </div>
        </div>
        </div>

        <nav class="navbar navbar-default">
            <div class="container-fluid">

                    <!-- Collect the nav links, forms, and other content for toggling -->
                <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                    <ul class="nav navbar-nav">
                        <li class="active"><a href="">Neuer Dialog<span class="sr-only">(current)</span></a></li>
                        <li><a href="">Dialog Bearbeiten</a></li>
                    </ul>
                </div><!-- /.navbar-collapse -->
            </div><!-- /.container-fluid -->
        </nav>

        <form class="form-inline" action="createDialog.php" method="post">
        <div id="line0" class="lines">
          <div class="form-group">
            <label for="personA0">Person A</label>
            <textarea id="personA0" class="form-control" rows="2" name="line[0][personA]" ></textarea>
          </div>
          <div class="form-group">
            <label for="gapA0">L&uuml;cke A</label>
            <input type="text" class="form-control" id="gapA0" placeholder="L&uuml;cke A" name="line[0][gapA]">
          </div>
          <div class="form-group">
            <label for="personB0">Person B</label>
            <textarea id="personB0" class="form-control" rows="2" name="line[0][personB]"></textarea>
          </div>
          <div class="form-group">
            <label for="gapB9">L&uuml;cke B </label>
            <input type="text" class="form-control" id="gapB0" placeholder="L&uuml;cke B" name="line[0][gapB]">
          </div>
          <button id="add0" type="button" class="btn btn-default add-line" aria-label="Left Align">
          <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
        </button>
 
        </div>
          <div id="submit-group">
              <div class="form-group">
                <label for="category">Kategorie</label>
                <input type="text" class="form-control" id="category" placeholder="Kategorie" name="category">
              </div>
              <button type="submit" name="create" class="btn btn-default">Dialog abschicken</button>
          </div>
        </form>

        <script src="listHelper.js"></script>
    </body>
</html>
