#!/usr/bin/env python
# -*- coding: utf-8 -*-
import urllib2
import codecs
from bs4 import BeautifulSoup



divia_api="http://timeo3.keolis.com/relais/217.php"

lignes_xml=urllib2.urlopen(divia_api+"?xml=1").read()

f=codecs.open('mybdd.java','w',"utf-8");

soup_lignes = BeautifulSoup(lignes_xml)

print "import des lignes :"
for ligne in soup_lignes.find_all('ligne'):
	f.write(u"db.execSQL(\"insert into \"+TABLE_LIGNE+\" ('\"+LIGNE_CODE+\"','\"+LIGNE_NOM+\"','\"+LIGNE_SENS+\"','\"+LIGNE_VERS+\"','\"+LIGNE_COLOR+\"') VALUES ('"+ligne.code.string+"','"+ligne.nom.string.replace("'","''")+"','"+ligne.sens.string+"','"+ligne.vers.string.replace("'","''")+"','"+ligne.couleur.string+"');\");\n")

print "import des arrêts de chaque ligne :"
for ligne in soup_lignes.find_all('ligne'):
	ligne_xml=urllib2.urlopen(divia_api+"?xml=1&ligne="+ligne.code.string+"&sens="+ligne.sens.string).read()
	print "Import des arrets de la ligne "+ligne.code.string+" dans le sens "+ligne.sens.string
	soup_ligne=BeautifulSoup(ligne_xml)
	for arret in soup_ligne.find_all('als'):
		# print arret.arret.code.string,
		# print arret.arret.nom.string,
		# print arret.refs.string,
		# print arret.ligne.nom.string,
		# print arret.ligne.sens.string
		f.write(u"db.execSQL(\"insert into \"+TABLE_STATION+\" ('\"+STATION_CODE+\"','\"+STATION_NOM+\"','\"+STATION_REFS+\"','\"+STATION_LIGNE_CODE+\"','\"+STATION_LIGNE_SENS+\"') VALUES ('"+arret.arret.code.string+"','"+arret.arret.nom.string.replace("'","''")+"','"+arret.refs.string+"','"+arret.ligne.code.string+"','"+arret.ligne.sens.string+"');\");\n")