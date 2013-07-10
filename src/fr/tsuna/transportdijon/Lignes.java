package fr.tsuna.transportdijon;

public class Lignes {
	private String code;
	private String nom;
	private String sens;
	private String vers;
	private String couleur;
	
	public Lignes(){}
	
	public Lignes(String code, String nom, String sens, String vers, String couleur){
		this.setCode(code);
		this.setNom(nom);
		this.setSens(sens);
		this.setCouleur(couleur);
		this.setVers(vers);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getSens() {
		return sens;
	}

	public void setSens(String sens) {
		this.sens = sens;
	}

	public String getCouleur() {
		return couleur;
	}

	public void setCouleur(String couleur) {
		this.couleur = couleur;
	}
	
	public String toString(){
		return getNom()+"("+getVers()+")";
	}

	public String getVers() {
		return vers;
	}

	public void setVers(String vers) {
		this.vers = vers;
	}
	
}
