# Scala Completion Basic
This Project is officially change name to scala-completion-basic. This is a scalac compiler plugin that will print out all useful members if you pass through line number and column number.



# sample

test.scala:

```scala
class MyTest{
  def hello() = "hello"
}
object Test {
  val five = 5
  val hello = new MyTest()
  val amount = five / 0
  hello
}
```

run the following command:

```
scalac -Xplugin:printer.jar -P:printMember:7:8 -nowarn test.scala 
```

p.s.: printer.jar is the jar file created from the plugin, you can run `make printer.jar` to build it if you wanna try this plugin, `-P:printMember:7:8` means that you pass an argument `7:8` to the printMember phase,which is the phase created by our plugin, meaning you wanna see the last symbol's member before line 7, column 8.

output:

```shell

Scope{
  def hello(): String;
  def <init>(): MyTest;
  final def $asInstanceOf[T0](): T0;
  final def $isInstanceOf[T0](): Boolean;
  final def synchronized[T0](x$1: T0): T0;
  final def ##(): Int;
  final def !=(x$1: Any): Boolean;
  final def ==(x$1: Any): Boolean;
  final def ne(x$1: AnyRef): Boolean;
  final def eq(x$1: AnyRef): Boolean;
  protected[package lang] def finalize(): Unit;
  final def wait(): Unit;
  final def wait(x$1: Long,x$2: Int): Unit;
  final def wait(x$1: Long): Unit;
  final def notifyAll(): Unit;
  final def notify(): Unit;
  def toString(): String;
  protected[package lang] def clone(): Object;
  def equals(x$1: Any): Boolean;
  def hashCode(): Int;
  final def getClass(): Class[_];
  final def asInstanceOf[T0]: T0;
  final def isInstanceOf[T0]: Boolean
}
one warning found
```

In test.scala, the last symbol before line 7,column 8 is hello, which is an object of MyTest. As you can see at the last scope list, it's printing all the members of  MyTest.

# Log
- Add support to get import objects (commit:7646c5789369287f99aab859f3466865ebb348ac)
- With -nowarn option, we can make the output more clear.
- We can now print all members according to the line and column number (commit:8adbfd4e22503cca39db016ccbc297c4ebb0b663), and this plugin is officially changed to a project that may be used as a completion plugin for hackable editor.
- We can now print the members of a defined member (commit:1bde1df047d66140bbc243a77c63825244acec41)
- We can now print the tree in the compiled file now.Just don't know how to call methods from Global.Tree, looks like it provides some reflact feature, but I don't know how to call it.


