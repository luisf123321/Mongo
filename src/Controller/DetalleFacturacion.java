package Controller;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCursor;

import org.bson.Document;
import org.bson.types.ObjectId;
import com.mongodb.client.ClientSession;
import com.mongodb.TransactionOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ReadConcern;
import com.mongodb.WriteConcern;
import com.mongodb.client.TransactionBody;
import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import com.mongodb.client.result.UpdateResult;

import BaseDatos.Conexion;
import model.DetalleFactura;

import com.mongodb.client.result.DeleteResult;

@Path("DetalleFactura")
public class DetalleFacturacion {
	
	@GET
	@Path("/list")
	@Produces("application/json")
	public List<DetalleFactura> buscar(@Context HttpServletRequest request) {
		
		System.out.println("Ingresa a get detalle controller");
		HttpSession session = request.getSession(false);
		
		MongoClient con = Conexion.getMongoclient();
		ClientSession clientSession = con.startSession();
		
		TransactionOptions txnOptions = TransactionOptions.builder()
		        .readPreference(ReadPreference.primary())
		        .readConcern(ReadConcern.LOCAL)
		        .writeConcern(WriteConcern.MAJORITY)
		        .build();
		
		
		TransactionBody<List<DetalleFactura>> txnBody = new TransactionBody<List<DetalleFactura>>() {
			public List<DetalleFactura> execute() {

				ObjectId venta = (ObjectId) session.getAttribute("venta");
				System.out.println("trae venta de la sesión "+venta);
				List<DetalleFactura> detalle = null;
				System.out.println("crea detalle null "+ detalle);
				
				MongoCollection<Document> coll1 = con.getDatabase("tienda").getCollection("detalle-factura");
				MongoCollection<Document> coll2 = con.getDatabase("tienda").getCollection("catalogo_productos");
				MongoCollection<Document> coll3 = con.getDatabase("tienda").getCollection("producto");
								
				MongoCursor<Document> cursor1 = coll1.find(clientSession, eq("IdVenta", venta)).iterator();
				
				if(coll1.countDocuments()>0) {

					detalle = new ArrayList<>();
					
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
				
				cursor1.close();
				System.out.println("detalle lleno "+ detalle);
		        return detalle;
		    }		
		};
		
		try {		
			clientSession.withTransaction(txnBody, txnOptions);
			clientSession.commitTransaction();
			System.out.println("Se realiza commit get detalle controller");
			
		}catch (RuntimeException e){
			System.out.println(e.getMessage());
			
		}
		
		System.out.println("Sale de get detalle controller y retorna detalle "+clientSession.withTransaction(txnBody, txnOptions));
		
		return clientSession.withTransaction(txnBody, txnOptions);
	}
	
	@GET
	@Path("/Generar/{venta}")
	@Produces("application/json")
	public List<DetalleFactura> Generar(@PathParam(value = "venta")ObjectId venta) {
		
		System.out.println("Ingresa a get detalle controller");
		
		MongoClient con = Conexion.getMongoclient();
		ClientSession clientSession = con.startSession();
		
		TransactionOptions txnOptions = TransactionOptions.builder()
		        .readPreference(ReadPreference.primary())
		        .readConcern(ReadConcern.LOCAL)
		        .writeConcern(WriteConcern.MAJORITY)
		        .build();
		
		
		TransactionBody<List<DetalleFactura>> txnBody = new TransactionBody<List<DetalleFactura>>() {
			public List<DetalleFactura> execute() {


				List<DetalleFactura> detalle = null;
				System.out.println("crea detalle null "+ detalle);
				
				MongoCollection<Document> coll1 = con.getDatabase("tienda").getCollection("detalle-factura");
				MongoCollection<Document> coll2 = con.getDatabase("tienda").getCollection("catalogo_productos");
				MongoCollection<Document> coll3 = con.getDatabase("tienda").getCollection("producto");
								
				MongoCursor<Document> cursor1 = coll1.find(clientSession, eq("IdVenta", venta)).iterator();
				
				if(coll1.countDocuments()>0) {

					detalle = new ArrayList<>();
					
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
				
				cursor1.close();
				System.out.println("detalle lleno "+ detalle);
		        return detalle;
		    }		
		};
		
		try {		
			clientSession.withTransaction(txnBody, txnOptions);
			clientSession.commitTransaction();
			System.out.println("Se realiza commit get detalle controller");
			
		}catch (RuntimeException e){
			System.out.println(e.getMessage());
			
		}
		
		System.out.println("Sale de get detalle controller y retorna detalle "+clientSession.withTransaction(txnBody, txnOptions));
		
		return clientSession.withTransaction(txnBody, txnOptions);
	}
	

}
