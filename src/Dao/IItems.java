package Dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import org.bson.types.ObjectId;

public interface IItems {
	public List getItems(@Context HttpServletRequest request);
	public String guardar(@Context HttpServletRequest request, ObjectId product_id, int cantidad)throws SQLException;
	public void cancelar(@Context HttpServletRequest request, HttpServletResponse response) throws IOException;

}
