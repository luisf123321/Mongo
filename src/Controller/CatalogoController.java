package Controller;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import BaseDatos.Conexion;
import Dao.ICrud;
import Dao.IDao;
import model.Catalogo;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCursor;
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



@Path("/Categorias")
public class CatalogoController implements IDao {
	

	@POST
	@Path("/Nuevo")
	@Produces("application/json")
	@Override
	public boolean crear(Catalogo catalogo) throws RuntimeException {
		
		//Conexion conexion  =  new Conexion();
		MongoClient con = Conexion.getMongoclient();
		//Connection con = conexion.getConection();
		ClientSession clientSession = con.startSession();
		
		TransactionOptions txnOptions = TransactionOptions.builder()
		        .readPreference(ReadPreference.primary())
		        .readConcern(ReadConcern.LOCAL)
		        .writeConcern(WriteConcern.MAJORITY)
		        .build();
		
		TransactionBody<String> txnBody = new TransactionBody<String>() {
			public String execute() {
				
				MongoCollection<Document> coll = con.getDatabase("tienda").getCollection("catalogo");

		        coll.insertOne(clientSession, new Document("Nombre", catalogo.getNombre()));
		        
		        return "Inserted into collection";
		    }		
		};
		
		System.out.println(catalogo.getNombre());
		
		boolean flag = true;
		try {
			
			/*con.setAutoCommit(false);
			String sql = "INSERT INTO Catalogo (Nombre) VALUES (?)";
			PreparedStatement prepared = con.prepareStatement(sql);
			prepared.setString(1, catalogo.getNombre());
			
			if(prepared.execute()) {
				System.out.println("creo catalogo bien");
			}else {
				System.out.println("no creo catalogo");
			}
			con.commit(); */
			
			clientSession.withTransaction(txnBody, txnOptions);
			clientSession.commitTransaction();
			
		}catch(/*SQLException*/ RuntimeException e) {
			System.out.println(e);
			//con.rollback();
			clientSession.abortTransaction();
		}finally {
			//con.close();
		    clientSession.close();

		}
		return flag;		
	}
	
	
	@GET
	@Path("/list")
	@Produces("application/json")
	public List buscar(@Context HttpServletRequest request) throws SQLException {
		
		
		MongoClient con = Conexion.getMongoclient();
		ClientSession clientSession = con.startSession();
		
		TransactionOptions txnOptions = TransactionOptions.builder()
		        .readPreference(ReadPreference.primary())
		        .readConcern(ReadConcern.LOCAL)
		        .writeConcern(WriteConcern.MAJORITY)
		        .build();
				
		TransactionBody<List<Catalogo>> txnBody = new TransactionBody<List<Catalogo>>() {
			public List<Catalogo> execute() {
				
				List<Catalogo> catalogos = null;
				
				MongoCollection<Document> coll = con.getDatabase("tienda").getCollection("catalogo");
								
				MongoCursor<Document> cursor = coll.find(clientSession).iterator();
				
				if(coll.countDocuments()>0) {
					
					catalogos = new ArrayList<>();
					
					while(cursor.hasNext()) {
						
						Catalogo catalogo = new Catalogo();
						Document nxt = cursor.next();
						catalogo.setId(nxt.getObjectId("_id").toString());
						System.out.println(nxt.getObjectId("_id").toString());
						catalogo.setNombre(nxt.getString("Nombre"));
						catalogos.add(catalogo);
						
					}
					
										
				}
				
				cursor.close();
				
		        return catalogos;
		    }		
		};
		
		//System.out.println("elementos usuarios "+result.getRow());
		
		try {
			
			/*con.setAutoCommit(false);
			String sql = "SELECT * FROM Catalogo";
			PreparedStatement prepared = con.prepareStatement(sql);
			ResultSet result = prepared.executeQuery(sql);*/
			//result.last();
			//System.out.println("elementos usuarios "+result.getRow());
			
			clientSession.withTransaction(txnBody, txnOptions);
			
			clientSession.commitTransaction();
			/*if(result.getRow() > 0) {
				result.beforeFirst();
				catalogos = new ArrayList<>();
				
				while(result.next()) {
					
					Catalogo catalogo = new Catalogo();
					catalogo.setId(result.getInt("idCatalogo"));
					catalogo.setNombre(result.getString("Nombre"));
					catalogos.add(catalogo);
					
				}
				
				//con.commit();
								
			}*/
			
			//result.close();
			
		}catch (RuntimeException e){
			System.out.println(e.getMessage());
			//clientSession.abortTransaction();
			//con.rollback();
		}finally {
			//clientSession.close();
			//con.close();
		}
		
		System.out.println(clientSession.withTransaction(txnBody, txnOptions));
		// TODO Auto-generated method stub
		
		return clientSession.withTransaction(txnBody, txnOptions);
		
		
	}
	
	@DELETE
	@Path("/Delete/{i_id}")
	@Produces("application/json")
	@Override
	public boolean eliminar_id(@PathParam(value = "i_id")ObjectId _id) throws SQLException {
		
		//Conexion conexion = new Conexion();
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
				
				MongoCollection<Document> coll = con.getDatabase("tienda").getCollection("catalogo");
				
				DeleteResult result = coll.deleteOne(clientSession, eq("_id", _id)); 
				
		        return result;
		    }		
		};
		
		try {
			
			/*con.setAutoCommit(false);
			String sql = "DELETE from Catalogo_Productos where idCatalogo=?";
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
			//con.close();
			clientSession.close();
		}
		return flag;
		
		
	}
	
	
	@PUT
	@Path("/UPDATE")
	@Produces("application/json")
	@Override
	public boolean actualizar(Catalogo catalogo) {
		// TODO Auto-generated method stub
		boolean flag = false;
		
		MongoClient con = Conexion.getMongoclient();
		ClientSession clientSession = con.startSession();
		
		TransactionOptions txnOptions = TransactionOptions.builder()
		        .readPreference(ReadPreference.primary())
		        .readConcern(ReadConcern.LOCAL)
		        .writeConcern(WriteConcern.MAJORITY)
		        .build();
		
		TransactionBody<UpdateResult> txnBody = new TransactionBody<UpdateResult>() {
			public UpdateResult execute() {
				
				MongoCollection<Document> coll = con.getDatabase("tienda").getCollection("catalogo");
				
				UpdateResult result = coll.updateOne(clientSession, eq("_id", catalogo.getObjectId()), new Document("$set", new Document("Nombre", catalogo.getNombre())));
				
				return result;
		    }		
		};
		
		try {
			
			/*con.setAutoCommit(false);
			String sql = "UPDATE  Catalogo set Nombre=? where idCatalogo=?";
			PreparedStatement prepared = con.prepareStatement(sql);
			prepared.setString(1, catalogo.getNombre());
			prepared.setInt(2, catalogo.getId());
			prepared.execute();
			System.out.println(prepared.getUpdateCount());
			if(prepared.getUpdateCount()==1) {
				flag = true;
			}
			con.commit();*/
			
			clientSession.withTransaction(txnBody, txnOptions);
			
			System.out.println(clientSession.withTransaction(txnBody, txnOptions).getModifiedCount());
			
			if(clientSession.withTransaction(txnBody, txnOptions).getModifiedCount()==1) {
				flag = true;
			}
			
			clientSession.commitTransaction();			
			
		// TODO Auto-generated method stub
		}catch (/*SQLException*/RuntimeException e){
			System.out.println(e.getMessage());
			clientSession.abortTransaction();
		}
		return flag;
		
	}

}
