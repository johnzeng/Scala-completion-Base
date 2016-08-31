run: printer.jar
	scalac -Xplugin:printer.jar -P:printMember:7:8 test.scala 

printer.jar: plug.scala
	fsc -d classes plug.scala
	cd classes; jar cf ../printer.jar .
