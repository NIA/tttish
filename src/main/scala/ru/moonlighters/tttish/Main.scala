package ru.moonlighters.tttish

object Main extends App with Controller {
  printHightlight("TTTish: The Time Tracker interactive shell v." + BuildInfo.version)
  println("Type `login <api_key>` to start working")
  println("Type help/exit for... you know")

  ConsoleLoop.run()

  println("Exiting. Goodbye!")
}
