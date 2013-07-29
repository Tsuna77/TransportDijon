/*
 * Copyright (C) 2013 Ricordeau Raphaël
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package fr.tsuna.transportdijon;



public class KeolisStation extends divia {
	private String code="";
	private String nom="";
	private String refs="";
	private static final String TAG="TransportDijon";
	private String vers="";
	private String couleur="";

	/*
	 * Renvoie le type d'objet utilisé
	 */
	@Override
	public String getType(){
		return "diviaStation";
	}
	public KeolisStation(String c, String n){
		myLog.write(TAG, "Création de la station "+n);
		setCode(c);
		setNom(n);
	}
	
	public KeolisStation(String c, String n, String r){
		myLog.write(TAG, "Création de la station "+n);
		setCode(c);
		setNom(n);
		setRefs(r);
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
	public String getCouleur() {
		return couleur;
	}
	public void setCouleur(String couleur) {
		this.couleur = couleur;
	}
}
