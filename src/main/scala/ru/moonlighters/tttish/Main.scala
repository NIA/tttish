package ru.moonlighters.tttish

import ru.moonlighters.ttt.Config
import ru.moonlighters.ttt.Config.ReadException

object Main extends App with Controller {
  // Must be the first thing to do
  try {
    Config.init(args)
  } catch {
    case ReadException(msg) => printError(msg)
  }

  printHighlight("TTTish: The Time Tracker interactive shell v." + BuildInfo.version)
  Config[String]("site", "api_key") map { _.trim } match {
    case Some(apiKey) if !apiKey.isEmpty => AccountController.login(apiKey)
    case None => println("Not found api_key setting in .tttrc, not logged in\nType `login <api_key>` to login and start working")
  }
  println("Type help/exit for... you know")

  ConsoleLoop.run()

  println("Exiting. Goodbye!")
}
