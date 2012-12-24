package ru.moonlighters.tttish

object ConsoleLoop {
  def run() {
    Iterator continually(prompt) map { _.trim } filterNot {_.isEmpty} foreach { _ match {
        case "help" => println(helpText)
        case "exit" => return
        case x => println("Unknown command: '%s'. Seriously, type help if not sure." format x)
    }}
  }

  def prompt: String = {
    Console.print(promptText)
    Console.readLine()
  }

  val promptText = "ttt > "
  val helpText = "Commands list:\nhelp\t- that is, print this text\nexit\t- nuff said, quits the program"
}
