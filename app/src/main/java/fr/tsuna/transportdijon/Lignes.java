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
