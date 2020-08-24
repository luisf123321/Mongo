package Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import BaseDatos.Conexion;
import Dao.IProductos;
import model.Catalogo;
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

@Path("/Productos")
public class ProductoController implements IProductos{
	
	@GET
	@Path("/list")
	@Produces("application/json")
	@Override
	public List<Producto> buscar(@Context HttpServletRequest request) throws SQLException {
		
		//Conexion conexion  =  new Conexion();
		//Connection con = conexion.getConection();
		
		MongoClient con = Conexion.getMongoclient();
		ClientSession clientSession = con.startSession();
		
		TransactionOptions txnOptions = TransactionOptions.builder()
		        .readPreference(ReadPreference.primary())
		        .readConcern(ReadConcern.LOCAL)
		        .writeConcern(WriteConcern.MAJORITY)
		        .build();
		
		TransactionBody<List<Producto>> txnBody = new TransactionBody<List<Producto>>() {
			public List<Producto> execute() {
				
				List<Producto> productos = null;	
				
				MongoCollection<Document> coll = con.getDatabase("tienda").getCollection("producto");
								
				MongoCursor<Document> cursor = coll.find(clientSession).iterator();
				
				if(coll.countDocuments()>0) {
					
					productos = new ArrayList<>();
					
					while(cursor.hasNext()) {
						
						Document nxt = cursor.next();
						Producto producto = new Producto();
						producto.setObjectId(nxt.getObjectId("_id"));
						producto.setNombre(nxt.getString("Nombre"));
						producto.setDescripcion(nxt.getString("Descripcion"));
						producto.setCodigo(nxt.getString("CodigoBarra"));
						productos.add(producto);
						
					}
					
										
				}
				
				cursor.close();
				
		        return productos;
		    }		
		};
		
		//con.setAutoCommit(false);
		
		try {			
			
			/*String sql = "SELECT * FROM Producto";			
			PreparedStatement prepared = con.prepareStatement(sql);			
			ResultSet result = prepared.executeQuery(sql);			
			result.last();
			
			if(result.getRow() > 0) {
				result.beforeFirst();
				productos = new ArrayList<>();
				
				while(result.next()) {
					Producto producto = new Producto();
					producto.setId(result.getInt("IdProducto"));
					producto.setNombre(result.getString("Nombre"));
					producto.setDescripcion(result.getString("Descripcion"));
					producto.setCodigo(result.getString("CodigoBarra"));
					productos.add(producto);					
				}
				con.commit();
				
			}
			result.close();*/
			
			clientSession.withTransaction(txnBody, txnOptions);
			
			clientSession.commitTransaction();
			
		}catch (RuntimeException e){
			System.out.println(e.getMessage());
			clientSession.abortTransaction();
			//con.rollback();
		}finally {
			clientSession.close();
			//con.close();
		}
		System.out.println(clientSession.withTransaction(txnBody, txnOptions));
		
		return clientSession.withTransaction(txnBody, txnOptions);
	}
	
	@POST
	@Path("/Nuevo")
	@Produces("application/json")
	@Override
	public boolean crear(Producto producto) throws SQLException {
		
		//Conexion conexion  =  new Conexion();
		//Connection con = conexion.getConection();
		
		MongoClient con = Conexion.getMongoclient();
		ClientSession clientSession = con.startSession();
		
		//con.setAutoCommit(false);
		
		boolean flag = true;
		
		TransactionOptions txnOptions = TransactionOptions.builder()
		        .readPreference(ReadPreference.primary())
		        .readConcern(ReadConcern.LOCAL)
		        .writeConcern(WriteConcern.MAJORITY)
		        .build();
		
		TransactionBody<Boolean> txnBody = new TransactionBody<Boolean>() {
			public Boolean execute() {
				boolean fl = false;
				MongoCollection<Document> coll = con.getDatabase("tienda").getCollection("producto");
				
				long count1 = coll.countDocuments();
				
		        coll.insertOne(clientSession, new Document("Nombre", producto.getNombre()).append("Descripcion", producto.getDescripcion())
		        		.append("CodigoBarra", producto.getCodigo()));
		        
		        long count2 = coll.countDocuments();
		        
		        if(count2>count1) {
					fl = true;
				}else {
					fl = false;
				}
		        
		        
		        return fl;
		    }		
		};
		
		try {
								
			/*String sql = "INSERT INTO Producto (Nombre,Descripcion,CodigoBarra) VALUES (?,?,?,?)";
			
			PreparedStatement prepared = con.prepareStatement(sql);
			prepared.setString(1, producto.getNombre());
			prepared.setString(2, producto.getDescripcion());
			prepared.setString(3,producto.getCodigo());
			
			if(prepared.execute()) {
				System.out.println("creo producto bien");
			}else {
				System.out.println("no creo producto");
			}
			
			con.commit();*/
			
			clientSession.withTransaction(txnBody, txnOptions);
			
			if(clientSession.withTransaction(txnBody, txnOptions)) {
				System.out.println("creo producto bien");
			}else {
				System.out.println("no creo producto");
			}
			
			clientSession.commitTransaction();
			
			
		}catch(RuntimeException e) {
			System.out.println(e);
			clientSession.abortTransaction();
			//con.rollback();
			
		}finally {
			clientSession.close();
			//con.close();
		}
		return clientSession.withTransaction(txnBody, txnOptions);
	}
	
	
	@GET
	@Path("/lista/{category_id}")
	@Produces("application/json")
	@Override
	public List<Producto> buscarId(@PathParam(value = "category_id")ObjectId categoria) throws SQLException {
		
		//Conexion conexion  =  new Conexion();
		//Connection con = conexion.getConection();
		//con.setAutoCommit(false);
		
		MongoClient con = Conexion.getMongoclient();
		ClientSession clientSession = con.startSession();
		
		TransactionOptions txnOptions = TransactionOptions.builder()
		        .readPreference(ReadPreference.primary())
		        .readConcern(ReadConcern.LOCAL)
		        .writeConcern(WriteConcern.MAJORITY)
		        .build();
		
		TransactionBody<List<Producto>> txnBody = new TransactionBody<List<Producto>>() {
			public List<Producto> execute() {
				
				List<Producto> productos = null;
				
				MongoCollection<Document> coll1 = con.getDatabase("tienda").getCollection("catalogo_productos");
				MongoCollection<Document> coll2 = con.getDatabase("tienda").getCollection("producto");
				
				MongoCursor<Document> cursor1 = coll1.find(clientSession, eq("IdCatalogo", categoria )).iterator();
								
				if(coll1.countDocuments()>0) {
					
					productos = new ArrayList<>();
					
					while(cursor1.hasNext()) {
						
						Document nxt1 = cursor1.next();
						Producto producto = new Producto();
						producto.setPrecio(nxt1.getDouble("Precio"));
						
						MongoCursor<Document> cursor2 = coll2.find(clientSession, eq("_id", nxt1.getObjectId("IdProducto"))).iterator();
						
						while(cursor2.hasNext()) {
							
							Document nxt2 = cursor2.next();
							producto.setObjectId(nxt2.getObjectId("_id"));
							producto.setNombre(nxt2.getString("Nombre"));
							producto.setDescripcion(nxt2.getString("Descripcion"));
												
						}
						
						productos.add(producto);
						cursor2.close();

					}
					
				}
				
				cursor1.close();
				
		        return productos;
		    }		
		};
		
		
		try {
			
			
			/*String sql = "SELECT Producto.IdProducto, Producto.Nombre, Producto.Descripcion, Catalogo_Productos.Precio from Producto INNER JOIN Catalogo_Productos ON Producto.IdProducto = Catalogo_Productos.IdProducto  WHERE IdCatalogo="+ categoria;
			System.out.println(sql);
			System.out.println(categoria);
			
			PreparedStatement prepared = con.prepareStatement(sql);
			
			
			ResultSet result = prepared.executeQuery(sql);
			
			result.last();
			
			if(result.getRow() > 0) {
				result.beforeFirst();
				productos = new ArrayList<>();
				
				while(result.next()) {
					Producto producto = new Producto();
					producto.setId(result.getInt("IdProducto"));
					producto.setNombre(result.getString("Nombre"));
					producto.setPrecio(result.getInt("Precio"));
					producto.setDescripcion(result.getString("Descripcion"));
					productos.add(producto);
					
				}
				con.commit();
				
			}
			result.close();*/
			clientSession.withTransaction(txnBody, txnOptions);
			clientSession.commitTransaction();
			
		}catch (RuntimeException e){
			System.out.println(e.getMessage());
			//clientSession.abortTransaction();
			//con.rollback();
		}finally {
			//clientSession.close();
			//con.close();
		}
		System.out.println(clientSession.withTransaction(txnBody, txnOptions));
		
		return clientSession.withTransaction(txnBody, txnOptions);
	}
	
	@GET
	@Path("/lista/id/{i_id}")
	@Produces("application/json")
	@Override
	public List<Producto> buscarPr(@PathParam(value = "i_id")ObjectId _id) throws SQLException {
		
		
		//Conexion conexion  =  new Conexion();
		//Connection con = conexion.getConection();
		//con.setAutoCommit(false);
		
		MongoClient con = Conexion.getMongoclient();
		ClientSession clientSession = con.startSession();
		
		
		TransactionOptions txnOptions = TransactionOptions.builder()
		        .readPreference(ReadPreference.primary())
		        .readConcern(ReadConcern.LOCAL)
		        .writeConcern(WriteConcern.MAJORITY)
		        .build();
		
		TransactionBody<List<Producto>> txnBody = new TransactionBody<List<Producto>>() {
			public List<Producto> execute() {
				
				List<Producto> productos = null;
				
				MongoCollection<Document> coll1 = con.getDatabase("tienda").getCollection("producto");
				MongoCollection<Document> coll2 = con.getDatabase("tienda").getCollection("catalogo_productos");
				
				MongoCursor<Document> cursor1 = coll1.find(clientSession, eq("_id", _id)).iterator();
								
				if(coll1.countDocuments()>0) {
					
					productos = new ArrayList<>();
					
					while(cursor1.hasNext()) {
						
						Document nxt1 = cursor1.next();
						Producto producto = new Producto();
						producto.setObjectId(nxt1.getObjectId("_id"));
						producto.setNombre(nxt1.getString("Nombre"));
						
						MongoCursor<Document> cursor2 = coll2.find(clientSession, eq("IdProducto", nxt1.getObjectId("_id"))).iterator();
						
						while(cursor2.hasNext()) {
							
							Document nxt2 = cursor2.next();
							producto.setPrecio(nxt2.getInteger("Precio"));
							
						}
						
						productos.add(producto);
						cursor2.close();

					}
					
				}
				
				cursor1.close();
				
		        return productos;
		    }		
		};
		
		try {
			
			
			/*String sql = "SELECT Producto.IdProducto, Producto.Nombre, Catalogo_Productos.Precio from Producto INNER JOIN Catalogo_Productos ON Producto.IdProducto = Catalogo_Productos.IdProducto  WHERE Producto.IdProducto = "+id;
			System.out.println(sql);
			System.out.println(id);
			
			PreparedStatement prepared = con.prepareStatement(sql);
			
			
			ResultSet result = prepared.executeQuery(sql);
			
			result.last();
			
			if(result.getRow() > 0) {
				result.beforeFirst();
				productos = new ArrayList<>();
				
				while(result.next()) {
					Producto producto = new Producto();
					producto.setId(result.getInt("IdProducto"));
					producto.setNombre(result.getString("Nombre"));
					producto.setPrecio(result.getInt("Precio"));
					productos.add(producto);
					
				}
				con.commit();
				
			}
			result.close();*/
			
			clientSession.withTransaction(txnBody, txnOptions);
			clientSession.commitTransaction();
		}catch (RuntimeException e){
			System.out.println(e.getMessage());
			clientSession.abortTransaction();
			//con.rollback();
		}finally {
			clientSession.close();	
			//con.close();
		}
		
		System.out.println(clientSession.withTransaction(txnBody, txnOptions));
		
		return clientSession.withTransaction(txnBody, txnOptions);

	}
	
	@GET
	@Path("/Delete/{i_id}")
	@Produces("application/json")
	@Override
	public boolean eliminar_id(@PathParam(value = "i_id")ObjectId _id) throws SQLException {
		
		//Conexion conexion  =  new Conexion();
		//Connection con = conexion.getConection();
		
		boolean flag = false;
		
		MongoClient con = Conexion.getMongoclient();
		ClientSession clientSession = con.startSession();
		
		TransactionOptions txnOptions = TransactionOptions.builder()
		        .readPreference(ReadPreference.primary())
		        .readConcern(ReadConcern.LOCAL)
		        .writeConcern(WriteConcern.MAJORITY)
		        .build();
		
		TransactionBody<DeleteResult> txnBody = new TransactionBody<DeleteResult>() {
			public DeleteResult execute() {
				
				MongoCollection<Document> coll = con.getDatabase("tienda").getCollection("producto");
				
				DeleteResult result = coll.deleteOne(clientSession, eq("_id", _id)); 
				
		        return result;
		    }		
		};
		
		try {
			
			/*con.setAutoCommit(false);
			String sql = "DELETE from Producto where IdProducto=?";
			PreparedStatement prepared = con.prepareStatement(sql);
			prepared.setInt(1, id);
			prepared.execute();
			System.out.println(prepared.getUpdateCount());
			if(prepared.getUpdateCount()==1) {
				flag = true;
			}
			con.commit();*/
			
			clientSession.withTransaction(txnBody, txnOptions);
			
			if(clientSession.withTransaction(txnBody, txnOptions).getDeletedCount()==1) {
				flag = true;
			}
			
			clientSession.commitTransaction();
			
		// TODO Auto-generated method stub
		}catch (RuntimeException e){			
			System.out.println(e.getMessage());
			clientSession.abortTransaction();
			//con.rollback();
		}finally {
			clientSession.close();
			//con.close();
		}
		return flag;
				
	}

}
