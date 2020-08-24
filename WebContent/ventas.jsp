<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
  <!-- Custom styles for this template -->
  <link href="css/shop-homepage.css" rel="stylesheet">
<title>Insert title here</title>

</head>
<body>
<jsp:include page="nav.jsp"></jsp:include>


<div class="container">
<br>
<br>
<div  id="dacfactura">	
	</div>
<br>
<br>
<div id="detalle">
</div>
</div>


<jsp:include page="footer.jsp"></jsp:include>
<script src="vendor/jquery/jquery.min.js"></script>
<script src="vendor/bootstrap/js/bootstrap.bundle.min.js"></script><!-- Bootstrap core JavaScript -->
<script src="vendor/popper/popper.min.js"></script>
<script src="vendor/bootstrap/js/bootstrap.min.js"></script>
<script src="js/generar.js"></script>
<script>
	  	$(document).ready(function(){
			getVentas();
		});
</script>



</body>
</html>