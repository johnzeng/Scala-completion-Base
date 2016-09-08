import java.util.Date
import java.util.UUID

class MyTest{
  def hello() = "hello"
  def now() = new Date()
}

object Test {
  val five = 5
  val hello = new MyTest()
  val amount = five / 1
  val test = Seq(1,2,3,4)
  test.map{ t => t+1 }.foreach{t => println(t)}
}
