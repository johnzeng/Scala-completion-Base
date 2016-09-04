package localhost

import scala.tools.nsc
import nsc.Global
import nsc.Phase
import nsc.plugins.Plugin
import nsc.plugins.PluginComponent

class PrintAllMembers(val global: Global) extends Plugin {
  import global._

  val startkey = "asldfkjaslfdlfd"
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
//    val runsAfter = List("refchecks")
    val runsAfter = List("parser")
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
          t.pos.line <= line && t.pos.column <= col
          }.toList.sortWith{(a,b) =>
            if(a.pos.line == b.pos.line){
              a.pos.column < b.pos.column
            }else{
              a.pos.line < b.pos.line
            }
          }
        val aboutLast = matchList filter{ t =>
          (t.pos.line == matchList.last.pos.line &&
          t.toString.startsWith("import")) ||
          (t.pos.line == matchList.last.pos.line &&
          t.pos.column == matchList.last.pos.column)
        }

        val (isPackage,sufix) = aboutLast.find{ t=>
          t.toString.startsWith("import")
          }match{
            case None => (false,"")
            case Some(t) =>{
              val s =t.toString.split("\\.").last
              (true, s)
            } 
          }

        println(aboutLast + ":" + ":sufix:" + sufix)

        if(isPackage){
          aboutLast.foreach{ t=>
              t.symbol.tpe.members.find{_.toString.endsWith(sufix)}.foreach{ m =>
                println(startkey)
                m.tpe.members.map(_.toString).filter{s=>
                  s.startsWith("object") || s.startsWith("class") || s.startsWith("package")
                  }.filter{ s=>
                    !s.contains("$")
                    }foreach{member =>
                  println(member) 
                }
                System.exit(0)
              }
            }
        }else{
          aboutLast.find{ t =>
            //it is still possible that you don't get any symbol or type at that position
            null != t.symbol && null != t.symbol.tpe
            }.foreach{ t=>
              if(false == t.tpe.members.isEmpty){
                println(startkey)
                println(t.tpe.members)
              }
              else if(false == t.symbol.tpe.members.isEmpty){
                println(startkey)
                println(t.symbol.tpe.members)
              }
          }

        }
        System.exit(0)
      }
    }
  }
}
