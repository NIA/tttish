package ru.moonlighters.tttish

object ConsoleLoop {
  def run() {
    Iterator continually(prompt) map { _.trim } filterNot {_.isEmpty} foreach { input =>
      // Allow writing only beginning of command, e.g. "up" for "updates", "h" for "help" etc.
      commandsList filter { _.startsWith(input) } match {
        case Seq(cmd) => cmd match {
          case "help" => println(helpText)
          case "exit" => return
          case "updates" => UpdatesController.showUpdates()
          case x => println(s"Sorry, command '$input' is not yet implemented :(")
        }
        case Seq() =>
          println(s"Unknown command: '$input'. Seriously, type help if not sure.")
        case Seq(cmds @ _*) =>
          val candidates = cmds.mkString("'", "', '", "'")
          println(s"Ambiguous command: '$input'. Possible candidates: $candidates.")
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
