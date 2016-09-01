package localhost

import scala.tools.nsc
import nsc.Global
import nsc.Phase
import nsc.plugins.Plugin
import nsc.plugins.PluginComponent

class PrintAllPackages(val global: Global) extends Plugin {
  import global._

  val name = "printPackage"
  val description = "print out all defined packages"
  val components = List[PluginComponent](Component)

  var line = 0
  var col = 0
  override def processOptions(options: List[String], error: String => Unit) {
    for (option <- options) {
      val opList = option.split(":")
      line = opList.head.toInt
      col = opList.last.toInt
    }
  }
  
  private object Component extends PluginComponent {
    val global: PrintAllPackages.this.global.type = PrintAllPackages.this.global
    val runsAfter = List("packageobjects")
    // Using the Scala Compiler 2.8.x the runsBefore should be written as below
    // val runsAfter = "refchecks"
    val phaseName = PrintAllPackages.this.name
    def newPhase(_prev: Phase) = new PrintPackagePhase(_prev)    
    
    class PrintPackagePhase(prev: Phase) extends StdPhase(prev) {
      override def name = PrintAllPackages.this.name
      def apply(unit: CompilationUnit) {
        for (t <- unit.body){
          if (t.pos.line <= line && t.pos.column <= col){
            if (null != t.symbol && null != t.symbol.tpe){
              t.symbol.tpe.members.find{_.toString.endsWith(" util")}.foreach{ m =>
                println(m.tpe.members)
              }
            }
          }
        }
        System.exit(0)
      }
    }
  }
}
