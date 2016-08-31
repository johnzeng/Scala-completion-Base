class MyTest{
  def hello() = "hello"
}
object Test {
  val five = 5
  val hello = new MyTest()
  val amount = five / 0
  hello
}
