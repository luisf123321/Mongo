package login;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.TransactionBody;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;
import org.bson.conversions.Bson;


import BaseDatos.Conexion;
import model.Catalogo;

@WebServlet("/LoginUser")
public class LoginUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
    public LoginUser() {
        super();
        // TODO Auto-generated constructor stub
    }
    

	protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		HttpSession session = request.getSession();
		session.invalidate();
		response.sendRedirect("index.jsp");
		
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String userName = request.getParameter("username");
        String password = request.getParameter("password");
        
		HttpSession session;
		
		//Conexion conexion  =  new Conexion();
		//Connection con = conexion.getConection();
		
		MongoClient con = Conexion.getMongoclient();
		ClientSession clientSession = con.startSession();
		
		Connection conexion1 = null;
		User usuario = new User(); 
		
		TransactionOptions txnOptions = TransactionOptions.builder()
		        .readPreference(ReadPreference.primary())
		        .readConcern(ReadConcern.LOCAL)
		        .writeConcern(WriteConcern.MAJORITY)
		        .build();
		
		TransactionBody<MongoCursor<Document>> txnBody = new TransactionBody<MongoCursor<Document>>() {
			public MongoCursor<Document> execute() {
								
				MongoCollection<Document> coll = con.getDatabase("tienda").getCollection("usuario");
								
				MongoCursor<Document> cursor = coll.find(clientSession, and(eq("User", userName), eq("Password", password))).iterator();
				
				return cursor;
		    }		
		};
		
		try {
			
			/*con.setAutoCommit(false);
			String sql = "SELECT * FROM Usuario where User = '"+userName+"' and Password = '"+password+"'" ;
			System.out.println(sql);
			PreparedStatement prepared = con.prepareStatement(sql);
			ResultSet result = prepared.executeQuery(sql);
			System.out.println(result);*/
						 
			/*if(result.next()){ 
				usuario.setId(result.getInt("idusuarios"));
				usuario.setUser(result.getString("User"));
				usuario.setPassword(result.getString("Password"));
				usuario.setNombre(result.getString("Nombre"));
				response.sendRedirect("index.jsp");
			}else {
				response.sendRedirect("login.jsp");
				
			}
			
			session = request.getSession();	
			session.setAttribute("user", usuario.getId());
			session.setAttribute("username", usuario.getNombre());
			System.out.println("id usuario "+usuario.getId());
			System.out.println("usuario "+ usuario.getNombre());*/
			
			MongoCursor<Document> curs = clientSession.withTransaction(txnBody, txnOptions);
			
			if(curs.hasNext()){
				Document nxt = curs.next();
				usuario.setObjectId(nxt.getObjectId("_id"));
				usuario.setUser(nxt.getString("User"));
				usuario.setPassword(nxt.getString("Password"));
				usuario.setNombre(nxt.getString("Nombre"));
				
				response.sendRedirect("index.jsp");
			}else {
				response.sendRedirect("login.jsp");
				
			}
			
			session = request.getSession();	
			session.setAttribute("user", usuario.getObjectId());
			session.setAttribute("username", usuario.getNombre());
			System.out.println("id usuario "+usuario.getObjectId());
			System.out.println("usuario "+ usuario.getNombre());
			
			curs.close();
			
		} 
		catch (Exception e) {
			System.out.println("Error: " + e.toString());
		}
	}

}
