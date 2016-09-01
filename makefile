run: printer.jar
	scalac -Xplugin:printer.jar -P:printMember:10:8 -nowarn test.scala 
	scalac -Xplugin:printer.jar -P:printMember:9:23 -nowarn test.scala 

test: packagePrinter.jar
	scalac -Xplugin:packagePrinter.jar -P:printPackage:1:16 -nowarn test.scala 

packagePrinter.jar: ImportPlug.scala
	fsc -d PrintPackages ImportPlug.scala
	cd PrintPackages ; jar cf ../packagePrinter.jar .


printer.jar: plug.scala
	fsc -d classes plug.scala
	cd classes; jar cf ../printer.jar .
