package login;

import org.bson.types.ObjectId;

public class User {
	private int id;
	private ObjectId _id;
	private String nombre;
	private String apellido;
	private String tipoIdentificacion;
	private int numerIdentificacion;
	private int celular;
	private int direccion;
	private String user;
	private String password;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getTipoIdentificacion() {
		return tipoIdentificacion;
	}
	public void setTipoIdentificacion(String tipoIdentificacion) {
		this.tipoIdentificacion = tipoIdentificacion;
	}
	public int getNumerIdentificacion() {
		return numerIdentificacion;
	}
	public void setNumerIdentificacion(int numerIdentificacion) {
		this.numerIdentificacion = numerIdentificacion;
	}
	public int getCelular() {
		return celular;
	}
	public void setCelular(int celular) {
		this.celular = celular;
	}
	public int getDireccion() {
		return direccion;
	}
	public void setDireccion(int direccion) {
		this.direccion = direccion;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public ObjectId getObjectId() {
		return _id;
	}
	
	public void setObjectId(ObjectId _id) {
		this._id = _id;
	}
	

}
