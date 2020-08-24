
function getFactura(){
	var id = document.getElementById("idventa").value;
    $.getJSON("http://localhost:8080/ProfundizacionMongo/services/Factura/Generar/"+id, function(result){
    	console.log(result);
		$('#dacfactura').empty();
    	for(var row=0; row<result.length; row=row+1){
    		var fecha = result[row].fecha;
    		var hora = result[row].hora;
    		var valortotal = result[row].valorTotal;
    		var nombrecliente = result[row].nombrecliente;
    		var nombrecajero = result[row].nombrecajero;    		
    		var fac='<div class="row"><label> <strong>Fecha :</strong> </label><label> '+fecha+'</label></div>';
    		fac = fac+ '<div class="row"><label><strong> hora : </strong></label><label> '+hora+'</label></div>';
    		fac = fac+ '<div class="row"><label><strong> Nombre Cliente :</strong> </label><label> '+nombrecliente+'</label></div>';
    		fac = fac+ '<div class="row"><label><strong> Cajero : </strong></label><label> '+nombrecajero+'</label></div>';
    		fac = fac+ '<div class="row"><label><strong> Valor Total : <strong></label><label> '+valortotal+'</label></div>';
    		
    		$("#dacfactura").append(fac);
    		
    		
    	}
    });
}

function getDetalle(){
	var id = document.getElementById("idventa").value;
    $.getJSON("http://localhost:8080/ProfundizacionMongo/services/DetalleFactura/Generar/"+id, function(result){
    	console.log(result);
		$('#detalle').empty();
		var table = '<table class="table table-striped"><thead><tr><th scope="col">#</th><th scope="col"> Nombre </th><th scope="col"> Precio </th><th scope="col">Cantidad</th><th scope="col">Valor</th>';
		table = table +'</tr></thead><tbody id="data"></tbody></table>';
		$("#detalle").append(table);
    	for(var row=0; row<result.length; row=row+1){
    		var id = result[row].id;
    		var name = result[row].nombre;
    		var precio = result[row].precio;
    		var cantidad = result[row].cantidad;
    		var valorUni = precio*cantidad;
    		console.log(id +" "+name+" "+precio+" "+cantidad);
    		var fac='<tr><th scope="row">'+id+'</th> <td> '+name+'</td><td>';
    		fac = fac+ precio +'</td> <td> '+cantidad+'</td><td>'+valorUni+'</td></tr>';    		
    		$("#data").append(fac);
    	}
    });
}
function getVentas(){
	//var id = document.getElementById("idventa").value;
    $.getJSON("http://localhost:8080/ProfundizacionMongo/services/Factura/Ventas", function(result){
    	console.log(result);
		var table = '<table class="table table-striped"><thead><tr><th scope="col">#</th><th scope="col"> Nombre Cliente </th><th scope="col"> Fecha </th><th scope="col">Hora</th><th scope="col">Valor</th>';
		table = table +'</tr></thead><tbody id="data"></tbody></table>';
		$("#dacfactura").append(table);
		//$('#dacfactura').empty();
    	for(var row=0; row<result.length; row=row+1){
    		var fecha = result[row].fecha;
    		var hora = result[row].hora;
    		var valortotal = result[row].valorTotal;
    		var nombrecliente = result[row].nombrecliente;
    		var id = result[row].id;    		
    		var fac='<tr><th scope="row">'+id+'</th>';
    		fac = fac+ '<td>'+nombrecliente+'</td>';
    		fac = fac+ '<td>'+fecha+'</td>';
    		fac = fac+ '<td> '+hora+'</td>';
    		fac = fac+ '<td> '+valortotal+'</td></tr>';
    		
    		$("#data").append(fac);
    		
    		
    	}
    });
}