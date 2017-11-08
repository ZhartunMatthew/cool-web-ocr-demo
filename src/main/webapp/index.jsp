<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css" crossorigin="anonymous">
    <style>
        form {
            margin-top: 10px !important;
        }
        code-list {
            max-width: 500px;
        }
        head-label {
            margin-top: 5px;
        }
    </style>

</head>
<body>
    <div class="container inputs-form">
        <form action="controller" method="post" enctype="multipart/form-data">

            <div class="container col-sm-3"> </div>
            <div class="container col-sm-9">
                <h3 class="display-3 head-label"> PDF recognizing demo </h3>
                <div class="row">
                    <div class="form-group">
                        <div class="col-sm-7">
                            <input type="file" name="file" class="form-control" placeholder="File name ...">
                        </div>
                        <div class="col-sm-2 button-parse">
                            <input type="submit" class="btn btn-primary" name="Parse" value="Parse"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="container col-sm-3"> </div>
        </form>
    </div>
    <br>
    <c:if test="${not empty output}">
        <div class="container code-list">
            <div class="col-sm-5">
                <iframe style="height: 800px; width: 450px;" src="data:application/pdf;base64,${pdf}"></iframe>
            </div>
            <div class="col-sm-7 code-list">
                <pre>
                    ${output}
                </pre>
            </div>
        </div>
    </c:if>
</body>
</html>