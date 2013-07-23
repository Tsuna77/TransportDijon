package fr.tsuna.transportdijon;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
/*
 * La class DiviaParser permet de récupérer les informations depuis l'api divia totem
 * disponible à l'adresse 84.55.151.139/relais
 * 
 * Les différentes fonctioné appeler sont :
 * 
 * 
 */
public class KeolisParser extends AsyncTask<Object, Object, Object> {
	public static final String url_list_line="http://timeo3.keolis.com/relais/217.php?xml=1";
	public static final String url_list_horaire="http://timeo3.keolis.com/relais/217.php?xml=3&ran=1";
    private static final String ns = null;
    private static final String TAG = "TransportDijon";
    private static Context context= null;

    public boolean isConnected(){
    	boolean connected=false;
    	NetworkInfo info=null;
    	try{
    		info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    	}
    	catch(Exception e){
    		myLog.write(TAG, "ERREUR : "+e.getMessage(),myLog.ERROR);
    	}
		if (info != null && info.isConnected()){
			connected=true;
		}
		
		// ajout d'un bypass pour la montre Imwatch dont la valeur retourn� est incorrect.
		if (android.os.Build.DEVICE.equals("Si14_imWatch")){
			myLog.write(TAG,"Détection d'une Imwatch, activation du réseaux non détectable");
			connected=true;
		}
		return connected;
    }
    
    public static void setContext(Context c){
    	context=c;
    }
	public List<Lignes> parseLine() throws XmlPullParserException, IOException{
		if (!isConnected()){
			throw new IOException("Réseaux de donnée non disponible");
		}
		
		
		myLog.write(TAG, "Début du parsing du fichier de ligne");
		
    	URL url=null;
    	URLConnection urlconnect=null;
    	InputStream result = null;
    	boolean error_detected = false;
    	List<Lignes> list_line = new ArrayList<Lignes>();
    	
    	try {
    		url = new URL(KeolisParser.url_list_line);
			urlconnect = url.openConnection();
	    	result = urlconnect.getInputStream();
		}
    	catch (Exception e){
    		error_detected=true;
    		myLog.write(TAG,"ERREUR get divia line list : "+e.getMessage(),myLog.ERROR);
    	}
    	if (error_detected){
    		list_line.clear();
        	return list_line;
    	}
    	
		try{
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(result,null);
			parser.nextTag();
			return readLine(parser);
		}	
		finally{
			result.close();
		}
	}

	public List<KeolisHoraire> parser_horaire(String refs) throws IOException{
		if (!isConnected()){
			throw new IOException("R�seaux de donn�e non disponible");
		}
		List<KeolisHoraire> list_horaire = new ArrayList<KeolisHoraire>();
		Boolean erreur = false;
		URLConnection urlconnect=null;
		InputStream result=null;

		String tmp_heure="";
		String tmp_dest="";
		String tmp_time="";
		
		try {
			URL url=new URL(url_list_horaire+"&refs="+refs);
			//Log.d(TAG,"Connexion � l'url : "+url);
			urlconnect = url.openConnection();
	    	result = urlconnect.getInputStream();
		} catch (Exception e) {
			erreur = true;
			myLog.write(TAG,e.getMessage(),myLog.ERROR);
		}
		
		if( ! erreur){
			// d�but de l'analyse du document.
			XmlPullParser parser = Xml.newPullParser();
			try{
				parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
				parser.setInput(result, null);
				parser.nextTag();
				parser.require(XmlPullParser.START_TAG, ns, "xmldata");
				parser.nextTag(); 	// arriv� sur erreur
				Log.d(TAG, "Code Erreur re�u : "+parser.getAttributeValue(0));
				goToTag(parser, "heure");
				tmp_heure = readText(parser);
				goToTag(parser, "passages");
				
				while (parser.next() != XmlPullParser.END_DOCUMENT){
					// analyse jusqu'� la fin du document
					if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("passage")){
						Log.d(TAG,"Analyse du passage num�ro "+parser.getAttributeValue(0));
						goToTag(parser, "duree");	// arriv� sur duree
						tmp_time = readText(parser);
						goToTag(parser, "destination");	// arriv� sur destination
						tmp_dest = readText(parser);
						list_horaire.add(new KeolisHoraire(tmp_dest, tmp_time, tmp_heure));
						
					}
				}
			}
			catch(Exception e){
	    		myLog.write(TAG,e.getMessage(),myLog.ERROR);
			}
		}
		
		
		return list_horaire;
	}
	
	public List<KeolisHoraire> parser_horaire(KeolisStation current_station) throws IOException{
		
		return parser_horaire(current_station.getRef());
		
		
	}
	
	public List<KeolisStation> parse_station(Lignes ligne) throws XmlPullParserException, IOException{
		if (!isConnected()){
			throw new IOException("R�seaux de donn�e non disponible");
		}
		List<KeolisStation> station= new ArrayList<KeolisStation>();
		URL url=null;
		URLConnection urlconnect = null;
		InputStream result=null;
		Boolean error_detected = false;
		
		
		if (!ligne.getVers().equals("")){
			myLog.write(TAG,"Recherche des arr�ts de la ligne "+ligne);
			
			try {
				String uri = KeolisParser.url_list_line+"&ligne="+ligne.getCode()+"&sens="+ligne.getSens();
	    		myLog.write(TAG,"Appel de l'url : "+uri);
				url = new URL(uri);
				urlconnect = url.openConnection();
		    	result = urlconnect.getInputStream();
			}
			catch(Exception e){
				error_detected=true;
	    		myLog.write(TAG,e.getMessage(),myLog.ERROR);
			}
			
			if(!error_detected){
				XmlPullParser parser = Xml.newPullParser();
				parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
				parser.setInput(result, null);
				parser.nextTag();
				parser.require(XmlPullParser.START_TAG, ns, "xmldata");
				myLog.write(TAG,"Analyse des station de la ligne : "+ligne.getCode()+" dans le sens "+ligne.getSens());
				while(parser.next() != XmlPullParser.END_DOCUMENT){
					if (parser.getEventType() == XmlPullParser.TEXT){
						continue;
					}
					else if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("als") ){
						myLog.write(TAG,"Analyse de la station num�ro "+parser.getAttributeValue(0));
						KeolisStation st = new KeolisStation("new", "");
						goToTag(parser, "code");
						st.setCode(this.readText(parser));
						goToTag(parser, "nom");
						st.setNom(this.readText(parser));
						goToTag(parser, "vers");
						st.setVers(readText(parser));
						goToTag(parser, "refs");
						String refs = readText(parser);	// r�cup�ration des r�f�rences des arr�ts
						st.setRefs(refs);
						station.add(st);
					}
				}
				
			}
			else{
				station.clear();
				station.add(new KeolisStation("",Resources.getSystem().getString(R.string.error_in_work)));
			}
		}
		else{
			station.add(new KeolisStation("", "Veuillez choisir une ligne en premier"));
		}
		
		return station;
	}
	
	private List<Lignes> readLine(XmlPullParser parser) throws XmlPullParserException, IOException {
		myLog.write(TAG,"Analyse du document");
		List<Lignes> lines = new ArrayList<Lignes>();
		Lignes line = null;
		parser.require(XmlPullParser.START_TAG, ns, "xmldata");
		
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
			String current_name = parser.getName();
			if (current_name.equals("alss")){
				myLog.write(TAG,"Il y a "+parser.getAttributeValue(0)+" Lignes dans le fichiers");
				parser.nextTag();	// passage du alss au als
			}
			current_name = parser.getName();
			if (current_name.equals("als")){
				line =readals(parser);
				if (line != null) {
					lines.add(line);
				}
				else{
					myLog.write(TAG,"La ligne trouv� est bugg�",myLog.WARNING);
				}
			}
		}
		return lines;
	}
	
	private void goToTag(XmlPullParser parser, String tagName) throws XmlPullParserException, IOException{
		// parse le document XML jusqu'au tag demand�
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals(tagName)){
				return;
			}
		}
		return;
	}
	
	private Lignes readals(XmlPullParser parser) throws XmlPullParserException, IOException{
		Lignes line =null;
		parser.require(XmlPullParser.START_TAG, ns, "als");
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        // Starts by looking for the entry tag
	        if (name.equals("ligne")) {
	        	Lignes read = readEntry(parser);
	        	if (!read.getVers().equals("")){
		        	line = read;
	        	}
	        	break;
	        }
	    }
		return line;
	}
	
	private Lignes readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
	    parser.require(XmlPullParser.START_TAG, ns, "ligne");
	    String code = null;
	    String vers = null;
	    String nom = null;
	    String sens = null;
	    String couleur = null;
	    
	    while( parser.next() != XmlPullParser.END_DOCUMENT){
	    	if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	    	String name = parser.getName();
	    	if (name.equals("nom")){
	    		nom=readText(parser);
	    	}
	    	else if (name.equals("vers")){
	    		vers=readText(parser);
	    	}
	    	else if (name.equals("code")){
	    		code=readText(parser);
	    	}
	    	else if (name.equals("sens")){
	    		sens=readText(parser);
	    	}
	    	else if (name.equals("couleur")){
	    		couleur=readText(parser);
	    		break;
	    	}
	    }
	    if (code==null){
	    	code = "";
	    	vers="";
	    }
	    while(parser.next() != XmlPullParser.END_TAG){
	    	// sortir de la balise couleur
	    }
	    while(parser.next() != XmlPullParser.END_TAG){
	    	// sortir de la balise ligne
	    }
	    while(parser.next() != XmlPullParser.END_TAG){
	    	//sortir de la balise refs
	    }
	    parser.require(XmlPullParser.END_TAG, ns, "als");
	    return new Lignes(code, nom, sens, vers,couleur);
	}
	
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        parser.nextTag();
	    }
	    return result;
	}


	@Override
	protected Object doInBackground(Object... params) {
		myLog.write(TAG, ((divia)params[0]).getType());
		
		return null;
	}

}
