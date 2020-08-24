package Dao;


import org.bson.types.ObjectId;

import java.sql.SQLException;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

public interface ICrud {
	
	//public boolean eliminar(int id) throws SQLException;
	public boolean eliminar_id(ObjectId _id) throws SQLException;
	public List buscar(@Context HttpServletRequest request) throws SQLException;

}
