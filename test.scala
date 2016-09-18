import java.util.Date
import java.util.UUID

class MyTest{
  def hello() = "hello"
  def now() = new Date()
  def test(i:Int, j:String):Int = 1
}

object Test {
  val five = 5
  val hello = new MyTest()
  val amount = five / 1
  val test = Seq(1,2,3,4)
  test.map{ t => t+1 }.foreach{t => println(t)}
  1 to 2  
  implicit class ImplicitGet(s:String){
    def implicitGetTest() = 123
  }
  
  implicit def implicitStr2int(str:String) = new MyTest()
  
  hello.test(1,"hello")
  val list = 1 to 1000 by 3 
}
