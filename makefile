run: printer.jar
	scalac -Xplugin:printer.jar -P:printMember:1:16 -nowarn test.scala 
	scalac -Xplugin:printer.jar -P:printMember:12:13 -nowarn test.scala 

test: printer.jar
	scalac -Xplugin:printer.jar -P:printMember:12:13 -nowarn test.scala 

printer.jar: plug.scala
	fsc -d classes plug.scala
	cd classes; jar cf ../printer.jar .
