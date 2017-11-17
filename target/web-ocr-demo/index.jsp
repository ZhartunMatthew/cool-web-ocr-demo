<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<!DOCTYPE html>
<html>
<head>
    <script src="//rawgit.com/SheetJS/js-xlsx/master/dist/xlsx.full.min.js"></script>
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



    <c:if test="${not empty pdf}">
        <c:if test="${not empty output}">
            <div class="container code-list">
                <div class="row">
                    <div class="col-sm-6">
                        <iframe style="height: 800px; width: 550px;" src="data:application/pdf;base64,${pdf}"></iframe>
                    </div>
                    <div class="col-sm-6 code-list">
                        <c:if test="${not empty output}">
                            <pre>
                                ${output}
                            </pre>
                        </c:if>
                    </div>
                </div>
            </div>
        </c:if>
        <c:if test="${not empty idDocument}">
            <div>
                <div>
                    <div class="col-sm-4">
                        <iframe style="height: 800px; width: 600px;" src="data:application/pdf;base64,${pdf}"></iframe>
                    </div>
                    <div class="col-sm-8 code-list">
                        <c:if test="${not empty output}">
                            <pre>
                                ${output}
                            </pre>
                        </c:if>

                        <c:if test="${not empty idDocument}">
                            <iframe src="https://docs.google.com/viewer?srcid=${idDocument}&pid=explorer&efh=false&a=v&chrome=false&embedded=true" width="1250px" height="800px"></iframe>
                        </c:if>
                    </div>
                </div>
            </div>
        </c:if>
    </c:if>
</body>
</html>