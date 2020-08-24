package Dao;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.PathParam;

import model.Producto;

import org.bson.types.ObjectId;

public interface IProductos extends ICrud {
	
	
	public List<Producto> buscarId(ObjectId categotia) throws SQLException;
	public boolean crear(Producto producto) throws SQLException;
	public List<Producto> buscarPr(ObjectId id) throws SQLException;
	//public List<Producto> buscarPr(String codigobarra) throws SQLException;

}
