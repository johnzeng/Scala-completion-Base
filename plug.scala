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

  var line:Option[Int] = None
  var col:Option[Int] = None
  override def processOptions(options: List[String], error: String => Unit) {
    for (option <- options) {
      val opList = option.split(":")
      if(opList.size == 2){
        line = Some(opList.head.toInt)
        col = Some(opList.last.toInt)
      }
    }
  }
  
  private object Component extends PluginComponent {
    val global: PrintAllMembers.this.global.type = PrintAllMembers.this.global
//    val runsAfter = List("refchecks")
    val runsAfter = List("parser")
    // Using the Scala Compiler below 2.8.0 the runsAfter should be written as below
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

        (line, col) match{
          case (Some(line),Some(col)) =>{
            val matchList = treeList.filter{ t =>
              t.pos.line < line ||(t.pos.line == line && t.pos.column <= col) 
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
              t.toString.startsWith("implicit") ||
              (t.pos.line == matchList.last.pos.line &&
              t.pos.column == matchList.last.pos.column)
            }

            if(aboutLast.size == 1){
              val lastSym = aboutLast.last
              lastSym match{
                case t :Literal => {
                  println(startkey)
                  println(t.tpe.members)
                  System.exit(0)
                }
                case _ => {}
              }
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

            if(isPackage){
              aboutLast.foreach{ t=>
                t.symbol.tpe.members.find{_.toString.endsWith(sufix)}.foreach{ m =>
                  println(startkey)
                  println("Scope{") 
                  m.tpe.members.map(_.toString).filter{s=>
                    s.startsWith("object") || s.startsWith("class") || s.startsWith("package")
                    }.filter{ s=>
                      !s.contains("$")
                      }foreach{member =>
                    println(member) 
                  }
                  println("}") 
                  System.exit(0)
                }
              }
              System.exit(0)
            }else{
              aboutLast.find{ t =>
                //it is still possible that you don't get any symbol or type at that position
                null != t.symbol && null != t.symbol.tpe
                (t.pos.line == matchList.last.pos.line &&
                t.pos.column == matchList.last.pos.column)
                }.foreach{ t=>
                  val tpeVal = if(false == t.tpe.members.isEmpty){
                    t.tpe
                  }
                  else if(false == t.symbol.tpe.members.isEmpty){
                    t.symbol.tpe
                  }
                  else{
                    t.tpe
                  }
                  println(startkey)
                  println(tpeVal.members)
                  //ok, look for the implicit members
                  matchList filter{ 
                    case d:DefDef => {
                      d.vparamss.size > 0 && d.vparamss.head.size > 0 &&
                      d.toString.startsWith("implicit")
                    }
                    case _ => false
                    } foreach{ 
                    case d:DefDef =>{
                      val paramTpe =d.vparamss.head.head.symbol.tpe
                      if(tpeVal <:< d.vparamss.head.head.symbol.tpe){
                        println(d.rhs.tpe.members)
                      }
                    }
                    case _ =>
                  }
              }
              System.exit(0)
            }
          }
          //I don't care about all symbol now,it's useless in current stage
          case _ =>{
            //should print all symbol, line and members
            val sortedList = treeList.toList.filter{t=> 
              ( !t.toString.startsWith("package <empty>") &&
                null !=t.symbol &&
                null != t.symbol.tpe && 
                false == t.symbol.tpe.members.isEmpty &&
                t.symbol.toString != "<none>") ||
              t.toString.startsWith("import")
            }.sortWith{(a,b) =>
              if(a.pos.line == b.pos.line){
                a.pos.column < b.pos.column
              }else{
                a.pos.line < b.pos.line
              }
            }
            
            def printAll(index:Int,lastPrintLine:Int,lastPrintCol:Int,lastImportLine:Int,lastImportStr:String){
              if(index >= sortedList.size){
                return
              }
              val t = sortedList(index)
              if (t.pos.line == lastPrintLine && t.pos.column == lastPrintCol){
                printAll(index + 1, lastPrintLine,lastPrintCol,lastImportLine, lastImportStr)
              }else{
                if (t.pos.line == lastImportLine){
                  //@Todo:handle import print here
                  printAll(index+1, lastPrintLine,lastPrintCol, lastImportLine, lastImportStr)
                }else if(t.toString.startsWith("import")){
                  printAll(index+1, lastPrintLine,lastPrintCol,t.pos.line, t.toString.split(" ").last)
                }else{
                  println(s"${t.toString}:${t.pos.line}:${t.pos.column}:${t.pos.start}:${t.pos.end}")
                  println(t.symbol.tpe.members)
                  printAll(index+1,t.pos.line,t.pos.column,lastImportLine, lastImportStr)
                }
              }
            }
            printAll(0,-1,-1,-1,"")
            System.exit(0)
          }
        }

      }
    }
  }

}
