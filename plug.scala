package localhost

import scala.tools.nsc
import nsc.Global
import nsc.Phase
import nsc.plugins.Plugin
import nsc.plugins.PluginComponent

class PrintAllMembers(val global: Global) extends Plugin {
  import global._

  val name = "printMember"
  val description = "print out all defined member's members"
  val components = List[PluginComponent](Component)

  var pos = ""
  override def processOptions(options: List[String], error: String => Unit) {
    for (option <- options) {
      println(option)
    }
  }
  
  private object Component extends PluginComponent {
    val global: PrintAllMembers.this.global.type = PrintAllMembers.this.global
    val runsAfter = List("refchecks")
    // Using the Scala Compiler 2.8.x the runsAfter should be written as below
    // val runsAfter = List[String]("refchecks");
    val phaseName = PrintAllMembers.this.name
    def newPhase(_prev: Phase) = new PrintMemberPhase(_prev)    
    
    class PrintMemberPhase(prev: Phase) extends StdPhase(prev) {
      override def name = PrintAllMembers.this.name
      def apply(unit: CompilationUnit) {
        for (b<-unit.body){
          //this loop will get stackoverflow error, looks like some of them don't have tpe
              println("\ntype:" + b.symbol.tpe +"\n" )
//              println("tag:" + b.symbol.tpe.members)
//              println("b:" + b.name )
              println("line:" + b.pos.line +",colunm:" + b.pos.column)
        }
      }
    }
  }
}
