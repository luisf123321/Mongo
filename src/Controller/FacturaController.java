package Controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Date;
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

import BaseDatos.Conexion;
import Dao.IFactura;
import model.Cliente;
import model.Factura;
import model.Item;
import model.Producto;

@Path("/Factura")
public class FacturaController implements IFactura{
	
	@GET
	@Path("/Nuevo")
	@Produces("application/json")
	@Override
	public String crear(@Context HttpServletRequest request) throws SQLException {
		// TODO Auto-generated method stub
		
		HttpSession session = request.getSession(false);
		ArrayList cart = (ArrayList) session.getAttribute("cart");
	
		//Conexion conexion  =  new Conexion();
		//Connection con = conexion.getConection();
		
		MongoClient con = Conexion.getMongoclient();
		ClientSession clientSession = con.startSession();
						
		TransactionOptions txnOptions = TransactionOptions.builder()
		        .readPreference(ReadPreference.primary())
		        .readConcern(ReadConcern.LOCAL)
		        .writeConcern(WriteConcern.MAJORITY)
		        .build();
		
		System.out.println(session.getAttribute("user"));
		System.out.println(session.getAttribute("IdCliente"));
		
		ObjectId user =  (ObjectId) session.getAttribute("user");
		ObjectId cliente =  (ObjectId) session.getAttribute("IdCliente"); 
		
		Date date = new Date();
		LocalDate fecha = LocalDate.now();
		int fechaD= fecha.getDayOfMonth();
		int fechaM= fecha.getMonthValue();
		int fechaY= fecha.getYear();
		int fechaHH= fecha.getDayOfMonth();
		LocalTime hora = LocalTime.now();
		int horaH = hora.getHour();
		int horaM = hora.getMinute();
		
		
				
		MongoCollection<Document> coll = con.getDatabase("tienda").getCollection("factura");
		long count1 = coll.countDocuments();
		
		TransactionBody<String> txnBody1 = new TransactionBody<String>() {
			public String execute() {
				
				
				double valor_total = 0;

				for (int i = 0; i < cart.size(); i = i + 1) {
					Item item = (Item) cart.get(i);
					valor_total = valor_total + item.getValor();
				}
				
				coll.insertOne( clientSession, new Document("Fecha", fechaD+"/"+fechaM+"/"+fechaY).append("Hora", horaH+":"+horaM)
		        		.append("Valor_Total", valor_total).append("IdCliente", cliente)
		        		.append("IdImpuesto", 1).append("IdCajero", user).append("IdTienda", 1));
		      
				//------------------
				
				System.out.println("Ingresa a Transacci�n2");
				ObjectId venta = new ObjectId();
				
				MongoCollection<Document> coll = con.getDatabase("tienda").getCollection("factura");
								
				MongoCursor<Document> cursor = coll.find(clientSession,eq("IdCliente", cliente)).iterator();
				
				if(coll.countDocuments()>0) {
					
					while(cursor.hasNext()) {
						
						Document nxt = cursor.next();
						venta = nxt.getObjectId("_id");
																	
					}
				
					System.out.println("Imprimiendo venta de T2 "+venta);
					session.setAttribute("venta", venta);
				}
				
				cursor.close();
				
				//---------------
				
				System.out.println("Ingresa a Transacci�n3");
				for (int i = 0; i < cart.size(); i = i + 1) {
					Item item = (Item) cart.get(i);
					int cantidad = item.getCantidad();
					ObjectId id = item.getObjectId();
					System.out.println(cantidad);
					System.out.println(id);
					ObjectId idCP = new ObjectId();
				
					MongoCollection<Document> coll2 = con.getDatabase("tienda").getCollection("catalogo_productos");				
					MongoCursor<Document> cursor2 = coll2.find(clientSession, eq("IdProducto", id)).iterator();
				
					if(coll.countDocuments()>0) {
					
						while(cursor2.hasNext()) {
						
							Document nxt = cursor2.next();
							idCP = nxt.getObjectId("_id");	
										
						}
								
						System.out.println("Imprime idCP de T3"+ idCP);
											
					}
				
					cursor.close();
					venta(idCP,venta,cantidad);
				
				}	
				
				return "ok inserted";
			}
		};
		
		
		try{
			
			
			clientSession.withTransaction(txnBody1, txnOptions);
			//boolean fl = false;
			clientSession.commitTransaction();
			System.out.println("Se realiza commit Transacci�n1");
			
			long count2 = coll.countDocuments();
	        
	        if(count2>count1) {
				//fl = true;
				System.out.println("cre� factura en bd");
			}else {
				//fl = false;
				System.out.println("no cre� factura en bd");
			}
			
			/*if() {
				System.out.println("creo factura bien");
			}else {
				System.out.println("no creo factura");
			}*/
			
			
		}catch(RuntimeException e) {
			System.out.println(e);
			//clientSession.abortTransaction();
			//con.rollback();			
		}
		return "ok";
		}
		/*
		//ClientSession clientSession2 = con.startSession();
		
		TransactionBody<ObjectId> txnBody2 = new TransactionBody<ObjectId>() {
			public ObjectId execute() {
				
				System.out.println("Ingresa a Transacci�n2");
				ObjectId venta = new ObjectId();
				
				MongoCollection<Document> coll = con.getDatabase("tienda").getCollection("factura");
								
				MongoCursor<Document> cursor = coll.find(clientSession,eq("IdCliente", cliente)).iterator();
				
				if(coll.countDocuments()>0) {
					
					while(cursor.hasNext()) {
						
						Document nxt = cursor.next();
						venta = nxt.getObjectId("_id");
								
					}
				
					System.out.println("Imprimiendo venta de T2 "+venta);
					
				}
				
				cursor.close();
				return venta;
		    }		
		};
		
		try{
			
			clientSession.withTransaction(txnBody2, txnOptions);
			clientSession.commitTransaction();
			System.out.println("Se realiza commit Transacci�n2");
			
			session.setAttribute("venta", clientSession.withTransaction(txnBody2, txnOptions));
			System.out.println("Se asigna venta a la sesi�n");
			
			//System.out.println("numero de venta "+ clientSession.withTransaction(txnBody2, txnOptions));
			//System.out.println(v);
						
		}catch(RuntimeException e) {
			System.out.println(e);
			//clientSession.abortTransaction();
			
		}
		

		
		/*for (int i = 0; i < cart.size(); i = i + 1) {
			Item item = (Item) cart.get(i);
			int cantidad = item.getCantidad();
			ObjectId id = item.getObjectId();
			System.out.println(cantidad);
			System.out.println(id);
						
			TransactionBody<ObjectId> txnBody3 = new TransactionBody<ObjectId>() {
				public ObjectId execute() {
					System.out.println("Ingresa a Transacci�n3");
					ObjectId idCP = new ObjectId();
					
					MongoCollection<Document> coll = con.getDatabase("tienda").getCollection("catalogo_productos");
									
					MongoCursor<Document> cursor = coll.find(clientSession, eq("IdProducto", id)).iterator();
					
					if(coll.countDocuments()>0) {
						
						while(cursor.hasNext()) {
							
							Document nxt = cursor.next();
							idCP = nxt.getObjectId("_id");	
											
						}
						
						
						System.out.println("Imprime idCP de T3"+ idCP);
												
					}
					
					cursor.close();
					return idCP;
			    }		
			};
			
			try {
							
				clientSession.withTransaction(txnBody3, txnOptions);
				
				
				venta(clientSession.withTransaction(txnBody3, txnOptions),clientSession.withTransaction(txnBody2, txnOptions),cantidad);
				clientSession.commitTransaction();
				System.out.println("Se realiza commit Transacci�n3 y ejecut� m�todo Venta");

				
			}catch(RuntimeException e) {
				System.out.println(e);
				//clientSession.abortTransaction();
				//con.rollback();			
			}
			
		}
		
		System.out.println("Sali� de T3");
		return "ok";
		*/
	 
	
	public void venta(ObjectId id, ObjectId venta,int cantidad) {
		
		//Conexion conexion  =  new Conexion();
		//Connection con = conexion.getConection();
		
		MongoClient con = Conexion.getMongoclient();
		ClientSession clientSession = con.startSession();
		
		TransactionOptions txnOptions = TransactionOptions.builder()
		        .readPreference(ReadPreference.primary())
		        .readConcern(ReadConcern.LOCAL)
		        .writeConcern(WriteConcern.MAJORITY)
		        .build();
		
		System.out.println("Ingres� a m�todo venta");
		System.out.println(id);
		System.out.println(venta);
		System.out.println(cantidad);
		
		MongoCollection<Document> coll = con.getDatabase("tienda").getCollection("detalle-factura");
		long count1 = coll.countDocuments();
		
		TransactionBody<String> txnBody = new TransactionBody<String>() {
			public String execute() {
								
		        coll.insertOne(clientSession,new Document("IdVenta", venta).append("IdCatalogo_Productos", id)
		        		.append("CantidadCompra", cantidad));
		        
		        
		        
		        return "ok insert";
		    }		
		};
		
		try {
			
			/*con.setAutoCommit(false);
			String sql = "INSERT INTO `Detalle-Factura` (IdVenta, IdCatalogo_Productos, CantidadCompra) VALUES (?,?,?)";
			System.out.println(sql);
			PreparedStatement prepared = con.prepareStatement(sql);
			prepared.setInt(1, venta);
			prepared.setInt(2, id);
			prepared.setInt(3, cantidad);
			if(prepared.execute()) {
				System.out.println("creo compra bien");
			}else {
				System.out.println("no creo compra");
			}
			con.commit();*/
			clientSession.withTransaction(txnBody, txnOptions);
			clientSession.commitTransaction();
			System.out.println("Se realiza commit de inserci�n en detalle-factura");
			
			long count2 = coll.countDocuments();
	        
	        if(count2>count1) {
				//fl = true;
				System.out.println("cre� detalle-factura");
			}else {
				//fl = false;
				System.out.println("no cre� detalle-factura");
			}
			
			//con.commit();
						
		}catch(RuntimeException e) {
			System.out.println(e);
			//clientSession.abortTransaction();
			//con.rollback();			
		}finally {
			//clientSession.close();
			//con.close();
			//clientSession.close();
		}
	}
	
	@GET
	@Path("/list")
	@Produces("application/json")
	@Override
	public List<Factura> buscar(@Context HttpServletRequest request) throws SQLException {
		
		HttpSession session = request.getSession(false);
		ObjectId venta =  (ObjectId) session.getAttribute("venta");
		System.out.println("Ingres� a m�todo get factura controller");
		System.out.println("la venta "+venta);
		
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
		
		TransactionBody<List<Factura>> txnBody = new TransactionBody<List<Factura>>() {
			public List<Factura> execute() {
				
				List<Factura> facturas = null;
				System.out.println("crea facturas en null " + facturas);
				
				MongoCollection<Document> coll1 = con.getDatabase("tienda").getCollection("factura");
				MongoCollection<Document> coll2 = con.getDatabase("tienda").getCollection("cliente");
				MongoCollection<Document> coll3 = con.getDatabase("tienda").getCollection("usuario");
								
				MongoCursor<Document> cursor1 = coll1.find(clientSession, eq("_id", venta)).iterator();
				
				if(coll1.countDocuments()>0) {
					
					facturas = new ArrayList<>();
					
					while(cursor1.hasNext()) {
						
						Document nxt1 = cursor1.next();
						Factura fac = new Factura();
						fac.setFecha(nxt1.getString("Fecha"));
						fac.setHora(nxt1.getString("Hora"));
						fac.setValorTotal(nxt1.getDouble("Valor_Total"));
						
						MongoCursor<Document> cursor2 = coll2.find(clientSession, eq("_id", nxt1.getObjectId("IdCliente"))).iterator();
						
						while(cursor2.hasNext()) {
							
							Document nxt2 = cursor2.next();
							fac.setNombrecliente(nxt2.getString("Nombre"));
							
						}
						
						MongoCursor<Document> cursor3 = coll3.find(clientSession, eq("_id", nxt1.getObjectId("IdCajero"))).iterator();
						
						while(cursor3.hasNext()) {
							
							Document nxt3 = cursor3.next();
							fac.setNombrecajero(nxt3.getString("Nombre"));
							
						}
						
						facturas.add(fac);
						System.out.println("facturas lleno "+facturas);
						cursor2.close();
						cursor3.close();
											
					}
					
					
				}
				
				
				cursor1.close();
				
				return facturas;
		    }		
		};
		
		try {
			
			/*String sql = "SELECT Factura.Fecha,Factura.Hora, Factura.Valor_Total , Cliente.Nombre as NombreCliente, Usuario.Nombre as NombreCajero from ((Factura INNER JOIN Cliente on Cliente.idCliente = Factura.IdCliente ) INNER JOIN Usuario on Usuario.idusuarios=Factura.IdCajero) where Factura.idVenta="+venta;
			System.out.println(sql);
			System.out.println(venta);
			
			PreparedStatement prepared = con.prepareStatement(sql);
			
			ResultSet result = prepared.executeQuery(sql);
			
			result.last();
			
			if(result.getRow() > 0) {
				result.beforeFirst();
				facturas = new ArrayList<>();
				
				while(result.next()) {
					Factura fac = new Factura();
					fac.setFecha(result.getString("Fecha"));
					fac.setHora(result.getString("Hora"));
					fac.setValorTotal(result.getDouble("Valor_Total"));
					fac.setNombrecliente(result.getString("NombreCliente"));
					fac.setNombrecajero(result.getString("NombreCajero"));
		
					facturas.add(fac);
					
				}
				con.commit();
				
			}
			result.close();*/
			
			clientSession.withTransaction(txnBody, txnOptions);
			clientSession.commitTransaction();
			System.out.println("Se realiza commit de transacci�n get factura controller");
			
		}catch (RuntimeException e){
			System.out.println(e.getMessage());
			//clientSession.abortTransaction();
			//con.rollback();
		}finally {
			//clientSession.close();
			//con.close();
		}
		System.out.println(clientSession.withTransaction(txnBody, txnOptions));
		//clientSession.close();
		System.out.println("sale de transacci�n get factura controller");
		return clientSession.withTransaction(txnBody, txnOptions);
	}
	
	@GET
	@Path("/Generar/{venta}")
	@Produces("application/json")
	public List<Factura> generar(@PathParam(value = "venta")ObjectId venta) throws SQLException {
		
		System.out.println("Ingres� a m�todo get factura controller");
		System.out.println("la venta "+venta);
		
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
		
		TransactionBody<List<Factura>> txnBody = new TransactionBody<List<Factura>>() {
			public List<Factura> execute() {
				
				List<Factura> facturas = null;
				System.out.println("crea facturas en null " + facturas);
				
				MongoCollection<Document> coll1 = con.getDatabase("tienda").getCollection("factura");
				MongoCollection<Document> coll2 = con.getDatabase("tienda").getCollection("cliente");
				MongoCollection<Document> coll3 = con.getDatabase("tienda").getCollection("usuario");
								
				MongoCursor<Document> cursor1 = coll1.find(clientSession, eq("_id", venta)).iterator();
				
				if(coll1.countDocuments()>0) {
					
					facturas = new ArrayList<>();
					
					while(cursor1.hasNext()) {
						
						Document nxt1 = cursor1.next();
						Factura fac = new Factura();
						fac.setFecha(nxt1.getString("Fecha"));
						fac.setHora(nxt1.getString("Hora"));
						fac.setValorTotal(nxt1.getDouble("Valor_Total"));
						
						MongoCursor<Document> cursor2 = coll2.find(clientSession, eq("_id", nxt1.getObjectId("IdCliente"))).iterator();
						
						while(cursor2.hasNext()) {
							
							Document nxt2 = cursor2.next();
							fac.setNombrecliente(nxt2.getString("Nombre"));
							
						}
						
						MongoCursor<Document> cursor3 = coll3.find(clientSession, eq("_id", nxt1.getObjectId("IdCajero"))).iterator();
						
						while(cursor3.hasNext()) {
							
							Document nxt3 = cursor3.next();
							fac.setNombrecajero(nxt3.getString("Nombre"));
							
						}
						
						facturas.add(fac);
						System.out.println("facturas lleno "+facturas);
						cursor2.close();
						cursor3.close();
											
					}
					
					
				}
				
				
				cursor1.close();
				
				return facturas;
		    }		
		};
		
		try {
			
			/*String sql = "SELECT Factura.Fecha,Factura.Hora, Factura.Valor_Total , Cliente.Nombre as NombreCliente, Usuario.Nombre as NombreCajero from ((Factura INNER JOIN Cliente on Cliente.idCliente = Factura.IdCliente ) INNER JOIN Usuario on Usuario.idusuarios=Factura.IdCajero) where Factura.idVenta="+venta;
			System.out.println(sql);
			System.out.println(venta);
			
			PreparedStatement prepared = con.prepareStatement(sql);
			
			ResultSet result = prepared.executeQuery(sql);
			
			result.last();
			
			if(result.getRow() > 0) {
				result.beforeFirst();
				facturas = new ArrayList<>();
				
				while(result.next()) {
					Factura fac = new Factura();
					fac.setFecha(result.getString("Fecha"));
					fac.setHora(result.getString("Hora"));
					fac.setValorTotal(result.getDouble("Valor_Total"));
					fac.setNombrecliente(result.getString("NombreCliente"));
					fac.setNombrecajero(result.getString("NombreCajero"));
		
					facturas.add(fac);
					
				}
				con.commit();
				
			}
			result.close();*/
			
			clientSession.withTransaction(txnBody, txnOptions);
			clientSession.commitTransaction();
			System.out.println("Se realiza commit de transacci�n get factura controller");
			
		}catch (RuntimeException e){
			System.out.println(e.getMessage());
			//clientSession.abortTransaction();
			//con.rollback();
		}finally {
			//clientSession.close();
			//con.close();
		}
		System.out.println(clientSession.withTransaction(txnBody, txnOptions));
		//clientSession.close();
		System.out.println("sale de transacci�n get factura controller");
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
				
				MongoCollection<Document> coll = con.getDatabase("tienda").getCollection("factura");
				
				DeleteResult result = coll.deleteOne(clientSession, eq("_id", _id)); 
				
		        return result;
		    }		
		};
		
		try {
			
			/*con.setAutoCommit(false);
			String sql = "DELETE from  Factura where idVenta=?";
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
			//clientSession.abortTransaction();
			//con.rollback();
		}finally {
			//clientSession.close();
			//con.close();
		}
		return flag;
		
		
	}
	
	@GET
	@Path("/Fin")
	@Produces("application/json")
	public void fin(@Context HttpServletRequest request) throws IOException {
		ArrayList<Item> cart = new ArrayList<Item>();
		HttpSession session = request.getSession(false);
		session.removeAttribute("cart");
		session.removeAttribute("IdCliente");
		session.removeAttribute("venta");
	}
	
	@GET
	@Path("/Ventas")
	@Produces("application/json")
	public List<Factura> generar() throws SQLException {
		
		System.out.println("Ingres� a m�todo get factura controller");
				
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
		
		TransactionBody<List<Factura>> txnBody = new TransactionBody<List<Factura>>() {
			public List<Factura> execute() {
				
				List<Factura> facturas = null;
				System.out.println("crea facturas en null " + facturas);
				
				MongoCollection<Document> coll1 = con.getDatabase("tienda").getCollection("factura");
				MongoCollection<Document> coll2 = con.getDatabase("tienda").getCollection("cliente");
				MongoCollection<Document> coll3 = con.getDatabase("tienda").getCollection("usuario");
								
				MongoCursor<Document> cursor1 = coll1.find(clientSession).iterator();
				
				if(coll1.countDocuments()>0) {
					
					facturas = new ArrayList<>();
					
					while(cursor1.hasNext()) {
						
						Document nxt1 = cursor1.next();
						Factura fac = new Factura();
						fac.setFecha(nxt1.getString("Fecha"));
						fac.setHora(nxt1.getString("Hora"));
						fac.setValorTotal(nxt1.getDouble("Valor_Total"));
						fac.setId(nxt1.getObjectId("_id").toString());	
						MongoCursor<Document> cursor2 = coll2.find(clientSession, eq("_id", nxt1.getObjectId("IdCliente"))).iterator();
						while(cursor2.hasNext()) {
							Document nxt2 = cursor2.next();
							fac.setNombrecliente(nxt2.getString("Nombre"));	
						}	
						facturas.add(fac);
						System.out.println("facturas lleno "+facturas);
						cursor2.close();											
					}
					
				}
				
				
				cursor1.close();
				
				return facturas;
		    }		
		};
		
		try {
			
			/*String sql = "SELECT Factura.Fecha,Factura.Hora, Factura.Valor_Total , Cliente.Nombre as NombreCliente, Usuario.Nombre as NombreCajero from ((Factura INNER JOIN Cliente on Cliente.idCliente = Factura.IdCliente ) INNER JOIN Usuario on Usuario.idusuarios=Factura.IdCajero) where Factura.idVenta="+venta;
			System.out.println(sql);
			System.out.println(venta);
			
			PreparedStatement prepared = con.prepareStatement(sql);
			
			ResultSet result = prepared.executeQuery(sql);
			
			result.last();
			
			if(result.getRow() > 0) {
				result.beforeFirst();
				facturas = new ArrayList<>();
				
				while(result.next()) {
					Factura fac = new Factura();
					fac.setFecha(result.getString("Fecha"));
					fac.setHora(result.getString("Hora"));
					fac.setValorTotal(result.getDouble("Valor_Total"));
					fac.setNombrecliente(result.getString("NombreCliente"));
					fac.setNombrecajero(result.getString("NombreCajero"));
		
					facturas.add(fac);
					
				}
				con.commit();
				
			}
			result.close();*/
			
			clientSession.withTransaction(txnBody, txnOptions);
			clientSession.commitTransaction();
			System.out.println("Se realiza commit de transacci�n get factura controller");
			
		}catch (RuntimeException e){
			System.out.println(e.getMessage());
			//clientSession.abortTransaction();
			//con.rollback();
		}finally {
			//clientSession.close();
			//con.close();
		}
		System.out.println(clientSession.withTransaction(txnBody, txnOptions));
		//clientSession.close();
		System.out.println("sale de transacci�n get factura controller");
		return clientSession.withTransaction(txnBody, txnOptions);
	}
	
	

}
