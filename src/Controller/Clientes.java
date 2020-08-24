package Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import com.mongodb.bulk.BulkWriteResult;

import BaseDatos.Conexion;
import Dao.IClientes;
import Dao.ICrud;
import model.Catalogo;
import model.Cliente;
import model.Producto;

@Path("/Clientes")
public class Clientes implements IClientes {
	
	@POST
	@Path("/Nuevo")
	@Produces("application/json")
	public String crearCliente(Cliente cliente) throws SQLException {
		
		System.out.println(cliente.getNombre());
		System.out.println(cliente.getApellido());
		System.out.println(cliente.getCelular());
		System.out.println(cliente.getTipoDocumento());
		System.out.println(cliente.getNumeroDocumento());
		System.out.println(cliente.getDirrecion());
		
		//Conexion conexion  =  new Conexion();
		//Connection con = conexion.getConection();
		
		MongoClient con = Conexion.getMongoclient();
		ClientSession clientSession = con.startSession();
		
		//boolean flag = true;
		
		TransactionOptions txnOptions = TransactionOptions.builder()
		        .readPreference(ReadPreference.primary())
		        .readConcern(ReadConcern.LOCAL)
		        .writeConcern(WriteConcern.MAJORITY)
		        .build();
		
		TransactionBody<Boolean> txnBody = new TransactionBody<Boolean>() {
			public Boolean execute() {
				boolean fl = false;
				MongoCollection<Document> coll = con.getDatabase("tienda").getCollection("cliente");
				
				long count1 = coll.countDocuments();
				System.out.println(count1);
				
		        coll.insertOne(clientSession, new Document("Nombre", cliente.getNombre()).append("Apellido", cliente.getApellido())
		        		.append("Tipo_Identificacion", cliente.getTipoDocumento()).append("Numero_Identificacion", cliente.getNumeroDocumento())
		        		.append("Celular", cliente.getCelular()).append("Direcion", cliente.getDirrecion()));
				
		        clientSession.commitTransaction();

		        
		        long count2 = coll.countDocuments();
				System.out.println(count2);

		        
		        if(count2>count1) {
					fl = true;
				}else {
					fl = false;
				}
		        
		        
		        return fl;
		    }		
		};
		
		try {
			
			/*con.setAutoCommit(false);
			
			String sql = "INSERT INTO Cliente (Nombre, Apellido , Celular, IdTipo_Identificacion, Numero_Identificacion, Direcion) VALUES (?,?,?,?,?,?)";
			
			PreparedStatement prepared = con.prepareStatement(sql);
			prepared.setString(1, cliente.getNombre());
			prepared.setString(2, cliente.getApellido());
			prepared.setInt(3, cliente.getCelular());
			prepared.setInt(4,cliente.getTipoDocumento());
			prepared.setInt(5, cliente.getNumeroDocumento());
			prepared.setString(6, cliente.getDirrecion());*/
			
			if(clientSession.withTransaction(txnBody, txnOptions)) {
				System.out.println("creo cliente bien");
			}else {
				System.out.println("no creo cliente");
			}
			
			//con.commit();
			
		}catch(RuntimeException e) {
			System.out.println(e);
			clientSession.abortTransaction();
			//con.rollback();
			
		}finally {
			//con.close();
			clientSession.close();
		}
		return "ok resgistro cliente";
		
	}
	
	@GET
	@Path("/list/{cedula}")
	@Produces("application/json")
	@Override
	public List<Cliente> buscarDocumento(@Context HttpServletRequest request,@PathParam(value = "cedula") int cedula) throws SQLException {
		
		HttpSession session = request.getSession(false);
		if(session.getAttribute("IdCliente") != null) {
			session.removeAttribute("IdCliente");
		}		
		
		// TODO Auto-generated method stub
		//Conexion conexion  =  new Conexion();
		//Connection con = conexion.getConection();
		
		MongoClient con = Conexion.getMongoclient();
		ClientSession clientSession = con.startSession();
		
		
		TransactionOptions txnOptions = TransactionOptions.builder()
		        .readPreference(ReadPreference.primary())
		        .readConcern(ReadConcern.LOCAL)
		        .writeConcern(WriteConcern.MAJORITY)
		        .build();
		
		
		
		TransactionBody<List<Cliente>> txnBody = new TransactionBody<List<Cliente>>() {
			public List<Cliente> execute() {
				
				List<Cliente> clientes = null;
				
				MongoCollection<Document> coll = con.getDatabase("tienda").getCollection("cliente");
								
				MongoCursor<Document> cursor = coll.find(clientSession, eq("Numero_Identificacion", cedula)).iterator();
				
				if(coll.countDocuments()>0) {
					
					clientes = new ArrayList<>();
					
					while(cursor.hasNext()) {
						
						Cliente cliente = new Cliente();
						Document nxt = cursor.next();
						cliente.setObjectId(nxt.getObjectId("_id"));
						cliente.setNombre(nxt.getString("Nombre"));
						cliente.setApellido(nxt.getString("Apellido"));
						cliente.setDirrecion(nxt.getString("Direcion"));
						cliente.setCelular(nxt.getInteger("Celular"));
						clientes.add(cliente);
						session.setAttribute("IdCliente", cliente.getObjectId());
						
											
					}
					
										
				}
				
				cursor.close();
				
		        return clientes;
		    }		
		};
		
		try {
			
			/*con.setAutoCommit(false);
			
			String sql = "SELECT * FROM Cliente where Numero_Identificacion = "+ cedula;
			System.out.println(sql);
			System.out.println(cedula);
			
			PreparedStatement prepared = con.prepareStatement(sql);
			
			
			ResultSet result = prepared.executeQuery(sql);
			
			result.last();
			
			if(result.getRow() > 0) {
				result.beforeFirst();
				clientes = new ArrayList<>();
				
				while(result.next()) {
					Cliente cliente = new Cliente();
					cliente.setId(result.getInt("idCliente"));
					cliente.setNombre(result.getString("Nombre"));
					cliente.setApellido(result.getString("Apellido"));
					cliente.setDirrecion(result.getString("Direcion"));
					cliente.setCelular(result.getInt("Celular"));
					clientes.add(cliente);
					session.setAttribute("IdCliente", cliente.getId());
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
	@Path("/Delete/{numero_identificacion}")
	@Produces("application/json")
	@Override
	public boolean eliminar(@PathParam(value = "numero_identificacion")int numero_identificacion) throws SQLException {
		
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
				
				MongoCollection<Document> coll = con.getDatabase("tienda").getCollection("cliente");
				
				DeleteResult result = coll.deleteOne(clientSession, eq("Numero_Identificacion", numero_identificacion)); 
				
		        return result;
		    }		
		};
		
		try {
			
			/*con.setAutoCommit(false);
			String sql = "DELETE from Cliente where Numero_Identificacion=?";
			PreparedStatement prepared = con.prepareStatement(sql);
			prepared.setInt(1, numero_identificacion);
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

	@Override
	public List buscar(HttpServletRequest request) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	

}
