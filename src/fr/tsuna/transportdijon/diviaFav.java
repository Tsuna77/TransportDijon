package fr.tsuna.transportdijon;

public class diviaFav {
	
	private String code;
	private String vers;
	private String nom;
	private String refs;
	
	public diviaFav(String c, String v, String n, String r){
		setCode(c);
		setVers(v);
		setNom(n);
		setRefs(r);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getVers() {
		return vers;
	}

	public void setVers(String vers) {
		this.vers = vers;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getRefs() {
		return refs;
	}

	public void setRefs(String refs) {
		this.refs = refs;
	}
	
}
