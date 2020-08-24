package model;

import org.bson.types.ObjectId;

public class Catalogo {
	protected ObjectId _id;
	protected String id;
	protected String Nombre;
	
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
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
