package ru.moonlighters.tttish

object ConsoleLoop {
  def run() {
    Iterator continually(prompt) map { _.trim } filterNot {_.isEmpty} foreach { input =>
      // Allow writing only beginning of command, e.g. "up" for "updates", "h" for "help" etc.
      commandsList find { _.startsWith(input) } match {
        case Some(cmd) => cmd match {
          case "help" => println(helpText)
          case "exit" => return
          case "updates" => UpdatesController.showUpdates()
          case x => println("Sorry, command '%s' is not yet implemented :(" format x)
        }
        case None => println("Unknown command: '%s'. Seriously, type help if not sure." format input)
      }
    }
  }

  def prompt: String = {
    print(promptText)
    readLine()
  }

  val commandsList = List("help", "exit", "updates")
  val promptText = "ttt > "
  // TODO: convert commandList to map and generate helpText from it
  val helpText =
    """
      |Commands list:
      | updates - show the last updates
      | help    - that is, print this text
      | exit    - nuff said, quits the program
      |""".stripMargin
}
