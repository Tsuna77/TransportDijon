package fr.tsuna.transportdijon;



public class diviaStation extends divia {
	private String code="";
	private String nom="";
	private String refs="";
	private static final String TAG="TransportDijon";
	private String vers="";

	/*
	 * Renvoie le type d'objet utilisé
	 */
	@Override
	public String getType(){
		return "diviaStation";
	}
	public diviaStation(String c, String n){
		myLog.write(TAG, "Création de la station "+n);
		setCode(c);
		setNom(n);
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String toString(){
		return getNom();
	}


	public void setRefs(String refs) {
		myLog.write(TAG,"Ajout des références des arrêts : "+refs);
		this.refs = refs;
	}
	public String getRef(){
		return this.refs;
	}

	public String getVers() {
		return vers;
	}

	public void setVers(String vers) {
		this.vers = vers;
	}
}
