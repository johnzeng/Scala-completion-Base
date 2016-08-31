run: printer.jar
	scalac -Xplugin:printer.jar -P:printMember:1:2:3 test.scala 

printer.jar: plug.scala
	fsc -d classes plug.scala
	cd classes; jar cf ../printer.jar .
