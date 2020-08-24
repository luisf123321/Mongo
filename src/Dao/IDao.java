package Dao;

import java.sql.SQLException;

import model.Catalogo;


public interface IDao extends ICrud {
	
	public boolean crear(Catalogo catalogo) throws SQLException;
	boolean actualizar(Catalogo catalogo);
	

}
