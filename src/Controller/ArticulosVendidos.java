package Controller;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.TransactionBody;

import BaseDatos.Conexion;
import model.Catalogo;
import model.DetalleFactura;

@Path("/ArticulosVendidos")
public class ArticulosVendidos {
	
	@GET
	@Path("/list")
	@Produces("application/json")
	public List<DetalleFactura> buscar(){
		
		MongoClient con = Conexion.getMongoclient();
		ClientSession clientSession = con.startSession();
		
		TransactionOptions txnOptions = TransactionOptions.builder()
		        .readPreference(ReadPreference.primary())
		        .readConcern(ReadConcern.LOCAL)
		        .writeConcern(WriteConcern.MAJORITY)
		        .build();
		List<DetalleFactura> detalle= new ArrayList<>();
		TransactionBody<List<DetalleFactura>> txnBody = new TransactionBody<List<DetalleFactura>>() {
			public List<DetalleFactura> execute() {
				
				MongoCollection<Document> coll1 = con.getDatabase("tienda").getCollection("detalle-factura");
				MongoCollection<Document> coll2 = con.getDatabase("tienda").getCollection("catalogo_productos");
				MongoCollection<Document> coll3 = con.getDatabase("tienda").getCollection("producto");
								
				MongoCursor<Document> cursor1 = coll1.find(clientSession).iterator();
				
				if(coll1.countDocuments()>0) {
					
					while(cursor1.hasNext()) {
						
						DetalleFactura detfac = new DetalleFactura();
						Document nxt1 = cursor1.next();
						detfac.setCantidad(nxt1.getInteger("CantidadCompra"));
						
						MongoCursor<Document> cursor2 = coll2.find(clientSession, eq("_id", nxt1.getObjectId("IdCatalogo_Productos"))).iterator();
						
						while(cursor2.hasNext()) {
							
							Document nxt2 = cursor2.next();
							detfac.setPrecio(nxt2.getDouble("Precio"));
							
							MongoCursor<Document> cursor3 = coll3.find(clientSession, eq("_id", nxt2.getObjectId("IdProducto"))).iterator();
							
							while(cursor3.hasNext()) {
								
								Document nxt3 = cursor3.next();
								detfac.setNombre(nxt3.getString("Nombre"));
								detfac.setObjectId(nxt3.getObjectId("_id"));								
							}							
							cursor3.close();
						} 												
						detalle.add(detfac);
						cursor2.close();						
					}										
				}
		        return detalle;
		    }		
		};
		
		return clientSession.withTransaction(txnBody, txnOptions);
		
	}
	

}
