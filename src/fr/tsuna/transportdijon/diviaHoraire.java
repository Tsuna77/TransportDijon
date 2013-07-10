package fr.tsuna.transportdijon;


public class diviaHoraire extends divia {
	private String dest="";
	private String time="";
	private String refTime = "";
	private static final String TAG="TransportDijon";

	/*
	 * Renvoie le type d'objet utilisé
	 */
	@Override
	public String getType(){
		return "diviaHoraire";
	}
	
	public String getDest() {
		return dest;
	}
	public void setDest(String dest) {
		this.dest = dest;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public diviaHoraire(String dest, String time, String refTime){
		myLog.write(TAG,"Création d'un nouvelle horaire pour la destination "+dest+" passage à "+time);
		setDest(dest);
		setTime(time);
		setRefTime(refTime);	// heure de génération du xml, permet de calculé le temps restant
	}
	public String getRefTime() {
		return refTime;
	}
	public void setRefTime(String refTime) {
		this.refTime = refTime;
	}
	
	public String get_Left_Time(){
		// renvoie le temps restant
		String left = "";
		
		String [] time_splitted = getTime().split(":");
		String [] ref_splitted = getRefTime().split(":");
		
		
		int time_in_minutes = Integer.parseInt(time_splitted[1])+(Integer.parseInt(time_splitted[0])*60);
		int ref_in_minutes = Integer.parseInt(ref_splitted[1])+(Integer.parseInt(ref_splitted[0])*60);
		
		if (time_in_minutes < ref_in_minutes){
			// l'heure d'arrivé est avant l'heure de téléchargement. Cela signifie un changement de jour
			time_in_minutes += 24*60; 	// ajout de 24 heure pour permettre le calcule du temps restant
		}
		
		int time_left = time_in_minutes-ref_in_minutes;
		
		
		if (time_left == 0){
			// il reste moins d'une minute
			left="A l'approche";
		}
		else{
			int work_time = time_left;
			myLog.write(TAG, "temps restant en minutes : "+time_left);
			left="Dans ";
			if (work_time >= 60){
				// au moins 1 heure d'attente
				int nb_heure = work_time/60;
				left+=nb_heure+"h";
				work_time-= 60*nb_heure;
				
			}
			left+=work_time+" min ";
		}
		return left;
	}
	
}
