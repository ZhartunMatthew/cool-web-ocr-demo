<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css" crossorigin="anonymous">
    <style>
        form {
            margin-top: 10px !important;
        }
        code-listing {
            margin-top: 20px;
        }
    </style>

</head>
<body>
    <div class="container">
        <form action="controller" method="post" enctype="multipart/form-data">
            <div class="container col-sm-3"> </div>
            <div class="container col-sm-6">
                <div class="row">
                    <div class="form-group" >
                        <div class="col-sm-10 in">
                            <input type="file" name="file" class="form-control" placeholder="File name ...">
                        </div>
                        <div class="col-sm-2">
                            <input type="submit" class="btn btn-primary"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="container col-sm-3"> </div>
        </form>
    </div>
    <div class="container code-listing">
        <pre>
            ${output}
        </pre>
    </div>
</body>
</html>