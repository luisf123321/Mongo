
function getArticulos(){
    $.getJSON("http://localhost:8080/ProfundizacionMongo/services/ArticulosVendidos/list", function(result){
    	console.log(result);
		var table = '<table class="table table-striped"><thead><tr><th scope="col">#</th><th scope="col"> Articulos</th><th scope="col">Cantidad</th>';
		table = table +'</tr></thead><tbody id="data"></tbody></table>';
		$("#detalle").append(table);
    	for(var row=0; row<result.length; row=row+1){
    		var id = result[row].id;
    		var name = result[row].nombre;
    		var cantidad = result[row].cantidad;
    		var fac='<tr><th scope="row">'+id+'</th><td>'+name+'</td>';
    		fac = fac+  '<td> '+cantidad+'</td></tr>';    		
    		$("#data").append(fac);
    	}
    });
}
