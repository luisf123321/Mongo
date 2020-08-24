package model;

import org.bson.types.ObjectId;

public class Datos {
	
	protected ObjectId _id;
	protected int id;
	protected String Nombre;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public ObjectId getObjectId() {
		return _id;
	}
	
	public void setObjectId(ObjectId _id) {
		this._id = _id;
	}
	
	public String getNombre() {
		return Nombre;
	}
	public void setNombre(String nombre) {
		Nombre = nombre;
	}
	
	
}
