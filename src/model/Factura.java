package model;

public class Factura {
	
	private String id;
	private String fecha;
	private String hora;
	private double valorTotal;
	private String nombrecliente;
	private int idimpuesto;
	private String nombrecajero;
	private int idtienda;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getHora() {
		return hora;
	}
	public void setHora(String hora) {
		this.hora = hora;
	}
	public double getValorTotal() {
		return valorTotal;
	}
	public void setValorTotal(double valorTotal) {
		this.valorTotal = valorTotal;
	}
	
	public int getIdimpuesto() {
		return idimpuesto;
	}
	public void setIdimpuesto(int idimpuesto) {
		this.idimpuesto = idimpuesto;
	}

	public int getIdtienda() {
		return idtienda;
	}
	public void setIdtienda(int idtienda) {
		this.idtienda = idtienda;
	}
	public String getNombrecliente() {
		return nombrecliente;
	}
	public void setNombrecliente(String nombrecliente) {
		this.nombrecliente = nombrecliente;
	}
	public String getNombrecajero() {
		return nombrecajero;
	}
	public void setNombrecajero(String nombrecajero) {
		this.nombrecajero = nombrecajero;
	}
	
	
	
	

}
