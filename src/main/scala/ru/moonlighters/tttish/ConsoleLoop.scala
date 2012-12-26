package ru.moonlighters.tttish

import collection.immutable.ListMap

object ConsoleLoop {
  private object LoopEnd extends Error

  def run() {
    try {
      Iterator continually(prompt) map { _.trim } filterNot {_.isEmpty} foreach { input =>
        val (cmdLike, args) = input.split("\\s+", 2) match {
          case Array(x) => (x, null)
          case Array(x, y) => (x, y)
        }
        // Allows writing only beginning of command, e.g. "up" for "updates", "h" for "help" etc.
        commands.keys.toSeq filter { _.startsWith(cmdLike) } match {
          case Seq(cmd) =>
            (commands.get(cmd) map { _.func } getOrElse { _: String =>
              println("Sorry, command '%s' is not yet implemented :(" format input)
            })(args)
          case Seq() =>
            println("Unknown command: '%s'. Seriously, type help if not sure." format input)
          case Seq(cmds @ _*) =>
            val candidates = cmds.mkString("'", "', '", "'")
            println("Ambiguous command: '%s'. Possible candidates: %s.".format(input, candidates))
        }
      }
    } catch {
      case LoopEnd => return
    }
  }

  /** Shell command.
    *
    * @param description command description
    * @param func command implementation that can receive some arguments
    */
  private case class Command(description: String, func: (String) => Unit)

  // TODO: move commands to the right place
  private val commands: Map[String, Command] = ListMap(
    "updates" -> Command("show the last updates", { _ =>
      UpdatesController.showUpdates()
    }),
    "post" -> Command("post new update", { msg =>
      // TODO: implement me
      assert(msg != null && msg.nonEmpty)
      println("... hard work of posting update with message \"%s\"..." format msg)
    }),
    "help" -> Command("that is, print this text", { _ =>
      println(helpText)
    }),
    "exit" -> Command("nuff said, quit the program", { _ =>
      throw LoopEnd
    })
  )

  private val promptText = "ttt > "

  private def prompt: String = {
    print(promptText)
    readLine()
  }

  private val helpTabStop = 8
  private val helpText = "Commands list:\n" + commands.map({
    case (cmd, desc) => cmd + " "*(helpTabStop - cmd.length) + "- " + desc
  }).mkString("\n")
}
