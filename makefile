run: printer.jar
	scalac -Xplugin:printer.jar test.scala 

printer.jar: plug.scala
	fsc -d classes plug.scala
	cd classes; jar cf ../printer.jar .
