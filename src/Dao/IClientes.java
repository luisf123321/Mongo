package Dao;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import model.Cliente;
import model.Producto;

public interface IClientes {
	
	public String crearCliente(Cliente cliente) throws SQLException;
	public List<Cliente> buscarDocumento(@Context HttpServletRequest request,int cedula) throws SQLException;
	boolean eliminar(int numero_identificacion) throws SQLException;
	public List buscar(@Context HttpServletRequest request) throws SQLException;
	
	

}
