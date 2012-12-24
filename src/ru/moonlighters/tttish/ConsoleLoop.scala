package ru.moonlighters.tttish

import ru.moonlighters.ttt.Remote

object ConsoleLoop {
  def run() {
    Iterator continually(prompt) map { _.trim } filterNot {_.isEmpty} foreach { _ match {
        case "help" => println(helpText)
        case "exit" => return
        case "updates" => showUpdates()
        case x => println("Unknown command: '%s'. Seriously, type help if not sure." format x)
    }}
  }

  def showUpdates() {
    for(u <- Remote.getUpdates) {
      val end = u.finishedAt match {
        case Some(x) => x.toString
        case None => "..."
      }
      val hours = u.hours match {
        case Some(x) => " (" + x + " h)"
        case None => ""
      }
      println(u.startedAt.toString + " - " + end + hours)
      println("@" + u.user.nickname + ": " + u.humanMessage)
      println()
    }
  }

  def prompt: String = {
    print(promptText)
    readLine()
  }

  val promptText = "ttt > "
  val helpText =
    """
      |Commands list:
      | updates - show the last updates
      | help    - that is, print this text
      | exit    - nuff said, quits the program
      |""".stripMargin
}
