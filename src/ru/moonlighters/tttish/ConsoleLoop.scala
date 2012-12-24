package ru.moonlighters.tttish

object ConsoleLoop {
  def run() {
    Iterator continually(prompt) map { _.trim } filterNot {_.isEmpty} foreach { _ match {
        case "help" => println(helpText)
        case "exit" => return
        case "updates" => UpdatesController.showUpdates()
        case x => println("Unknown command: '%s'. Seriously, type help if not sure." format x)
    }}
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
