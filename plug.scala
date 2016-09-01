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
    val global: PrintAllMembers.this.global.type = PrintAllMembers.this.global
    val runsAfter = List("refchecks")
    // Using the Scala Compiler 2.8.x the runsBefore should be written as below
    // val runsAfter = "refchecks"
    val phaseName = PrintAllMembers.this.name
    def newPhase(_prev: Phase) = new PrintMemberPhase(_prev)    
    
    class PrintMemberPhase(prev: Phase) extends StdPhase(prev) {
      override def name = PrintAllMembers.this.name
      def apply(unit: CompilationUnit) {
        def allTrees(tree: Tree): Iterator[Tree] =
        Iterator(tree, analyzer.macroExpandee(tree)).filter(_ != EmptyTree)
          .flatMap(t => Iterator(t) ++ t.children.iterator.flatMap(allTrees))

        val treeList = allTrees(unit.body)

        val matchList = treeList.filter{ t =>
          null != t.symbol && null != t.symbol.tpe && "" != t.symbol.tpe.toString && t.pos.line <= line && t.pos.column <= col
        }.toList

        val aboutLast = matchList filter{ t =>
          t.pos.line == matchList.last.pos.line &&
          t.pos.column == matchList.last.pos.column
        }

//        aboutLast foreach{ t=>
//          println("sym:" + t.symbol + ":tpe:" + t.symbol.tpe)
//        }

        aboutLast.find{ t =>
          //it is still possible that you don't get any symbol or type at that position
          null != t.symbol && null != t.symbol.tpe
          }.foreach{ t=>
          println(t.symbol.tpe.members)
        }
      }
    }
  }
}
