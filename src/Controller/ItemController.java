package Controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;


import BaseDatos.Conexion;
import Dao.IItems;
import model.Catalogo;
import model.Item;
import model.Producto;

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
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.result.DeleteResult;

@Path("/item")
public class ItemController implements IItems {
	
	
	@GET
	@Path("/add/{product_id}/{cantidad}")
	@Produces("application/json")
	public String guardar(@Context HttpServletRequest request,@PathParam(value = "product_id") ObjectId product_id, @PathParam(value = "cantidad") int cantidad) throws SQLException {
		
		HttpSession session = request.getSession(false);
		ArrayList cart = (ArrayList) session.getAttribute("cart");
		
		if (cart == null) {
			cart = new ArrayList();
		}
		
		for (int i = 0; i < cart.size(); i = i + 1) {
			Item item = (Item) cart.get(i);
			ObjectId id = item.getObjectId();

			if (id == product_id) {
				long items = cart.size();
				String result = "{\"items\":" + items + "}";
				return "ok";
			}
		}

		Item item = new Item();
		item.setObjectId(product_id);
		item.setQuantity(1);
		
		//Conexion conexion  =  new Conexion();
		//Connection con = conexion.getConection();
		
		MongoClient con = Conexion.getMongoclient();
		ClientSession clientSession = con.startSession();
		
		TransactionOptions txnOptions = TransactionOptions.builder()
		        .readPreference(ReadPreference.primary())
		        .readConcern(ReadConcern.LOCAL)
		        .writeConcern(WriteConcern.MAJORITY)
		        .build();
		
		TransactionBody<String> txnBody = new TransactionBody<String>() {
			public String execute() {
				
				MongoCollection<Document> coll1 = con.getDatabase("tienda").getCollection("producto");
				MongoCollection<Document> coll2 = con.getDatabase("tienda").getCollection("catalogo_productos");
				
				MongoCursor<Document> cursor1 = coll1.find(clientSession, eq("_id", product_id )).iterator();
								
				if(coll1.countDocuments()>0) {
					
					while(cursor1.hasNext()) {
						Document nxt1 = cursor1.next();
						String name = nxt1.getString("Nombre");
						ObjectId id = nxt1.getObjectId("_id");
						System.out.println(name);
						item.setNombre(name);
						item.setObjectId(id);
						item.setCantidad(cantidad);

						MongoCursor<Document> cursor2 = coll2.find(clientSession, eq("IdProducto", nxt1.getObjectId("_id"))).iterator();
						
						while(cursor2.hasNext()) {
							Document nxt2 = cursor2.next();
							double pricing = nxt2.getDouble("Precio");
							System.out.println(pricing);
							item.setPrecio(pricing);
							double valor = pricing*cantidad;
							item.setValor(valor);
												
						}
						
						cursor2.close();

					}
					
					clientSession.commitTransaction();
					
				}
				
				cursor1.close();
				
		        return "ok";
		    }		
		};
		
		try {

			/*con.setAutoCommit(false);
			
			String sql = "SELECT Producto.IdProducto, Producto.Nombre, Catalogo_Productos.Precio from Producto INNER JOIN Catalogo_Productos ON Producto.IdProducto = Catalogo_Productos.IdProducto  WHERE Producto.IdProducto = " + product_id;
			PreparedStatement prepared = con.prepareStatement(sql);
			
			ResultSet result = prepared.executeQuery(sql);
			result.last();

			if(result.getRow() > 0) {
				result.beforeFirst();
							
				while(result.next())  {
				// long id = rs1.getInt("id");
				String name = result.getString("Nombre");
				double pricing = result.getDouble("Precio");
				int id = result.getInt("IdProducto");
				System.out.println(pricing);
				System.out.println(name);
				item.setNombre(name);
				item.setPrecio(pricing);
				item.setId(id);
				item.setCantidad(cantidad);
				double valor = pricing*cantidad;
				item.setValor(valor);
				
			}
			}*/
			
			clientSession.withTransaction(txnBody, txnOptions);
			
		}catch (RuntimeException e){
			System.out.println(e.getMessage());
			clientSession.abortTransaction();
			//con.rollback();
		}		
		
		cart.add(item);
		System.out.println(cart.size());
		System.out.println(cart);
		
		session.setAttribute("cart", cart);

		long items = cart.size();
		String result = "{\"items\":" + items + "}";
		System.out.println(result);
	
		
		return clientSession.withTransaction(txnBody, txnOptions);
	}
	
	@GET
	@Path("/list")
	@Produces("application/json")
	public List getItems(@Context HttpServletRequest request) {
		ArrayList<Item> cart = new ArrayList<Item>();
		HttpSession session = request.getSession(false);

		cart = (ArrayList<Item>) session.getAttribute("cart");

		if (cart == null) {
			cart = new ArrayList<Item>();
		}

		return cart;
	}
	
	@GET
	@Path("/Cancelar")
	
	public void cancelar(@Context HttpServletRequest request, HttpServletResponse response) throws IOException {
		ArrayList<Item> cart = new ArrayList<Item>();
		HttpSession session = request.getSession(false);
		session.removeAttribute("cart");
		session.removeAttribute("IdCliente");
	}
	
	
	

}
