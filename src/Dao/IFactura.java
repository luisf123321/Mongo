package Dao;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import model.Factura;


public interface IFactura extends ICrud {
	
	public String crear(@Context HttpServletRequest request) throws SQLException;
	

}
