package BaseDatos;

//import java.sql.Connection;
//import java.sql.DriverManager;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class Conexion {
	
	public Conexion(){
		
	}
	
	//private static Connection database;
	private static MongoDatabase database;
	private static MongoClient mongoClient;

	public static MongoClient getMongoclient() {
		
		MongoClientURI uri = new MongoClientURI(
			    "mongodb://root:root@cluster0-shard-00-00.aqdwt.mongodb.net:27017,cluster0-shard-00-01.aqdwt.mongodb.net:27017,cluster0-shard-00-02.aqdwt.mongodb.net:27017/tienda?ssl=true&replicaSet=atlas-vfwoyz-shard-0&authSource=admin&retryWrites=true&w=majority");

		mongoClient = new MongoClient(uri);
		
		return mongoClient;
	}
	
	public  MongoDatabase getConection() {

		//String driver = "com.mysql.jdbc.Driver";
		//String url = "jdbc:mysql://localhost:3307/tienda-dao";
		//String user = "root";
		//String password = "";

		try {
			
			MongoClient cliente = Conexion.getMongoclient();
			database = cliente.getDatabase("tienda");
			
			//Class.forName(driver);
			//conexionDB = DriverManager.getConnection(url, user, password);
			
			System.out.println("conexion exitosa");
			
			
		} catch (Exception e) {
			System.out.println("Error   " + e.toString());
		}
		
		System.out.println(database);
		
		return database;
	}
	
	

}
