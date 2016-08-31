run: divbyzero.jar
	scalac -Xplugin:divbyzero.jar test.scala 

divbyzero.jar: plug.scala
	fsc -d classes plug.scala
	cd classes; jar cf ../divbyzero.jar .
