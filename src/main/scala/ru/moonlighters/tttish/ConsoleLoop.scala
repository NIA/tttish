package ru.moonlighters.tttish

import collection.immutable.ListMap

object ConsoleLoop {
  def run() {
    Iterator continually(prompt) map { _.trim } filterNot {_.isEmpty} foreach { input =>
      // Allow writing only beginning of command, e.g. "up" for "updates", "h" for "help" etc.
      commands.keys.toList filter { _.startsWith(input) } match {
        case Seq(cmd) => cmd match {
          case "help" => println(helpText)
          case "exit" => return
          case "updates" => UpdatesController.showUpdates()
          case x => println("Sorry, command '%s' is not yet implemented :(" format input)
        }
        case Seq() =>
          println("Unknown command: '%s'. Seriously, type help if not sure." format input)
        case Seq(cmds @ _*) =>
          val candidates = cmds.mkString("'", "', '", "'")
          println("Ambiguous command: '%s'. Possible candidates: %s.".format(input, candidates))
      }
    }
  }

  def prompt: String = {
    print(promptText)
    readLine()
  }

  val promptText = "ttt > "

  val commands = Map(
    "updates" -> "show the last updates",
    "help"    -> "that is, print this text",
    "exit"    -> "nuff said, quit the program"
  )
  val helpTabStop = 8
  val helpText = "Commands list:\n" + commands.map({
    case (cmd, desc) => cmd + " "*(helpTabStop - cmd.length) + "- " + desc
  }).mkString("\n")
}
