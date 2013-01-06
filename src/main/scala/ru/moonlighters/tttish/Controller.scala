package ru.moonlighters.tttish

import dispatch.Promise
import ru.moonlighters.ttt.Config
import java.io.File

trait Controller {

  def loadSettings(fileName: String) {
    val f = new File(fileName.trim)
    if (f.canRead) {
      println("Loading settings from '%s'..." format f.getAbsolutePath)
      Config.load(f)
    } else {
      printError("No such file '%s'" format fileName)
    }
  }

  /**
   *  --  Helpers --
   */


  def displayProgress[A](p: Promise[A]) {
    while(p.completeOption.isEmpty) {
      Thread.sleep(50)
      print('.')
    }
    protectiveDelay()
  }

  def waitTillComplete[A](p: Promise[A]) {
    p()
    protectiveDelay()
  }

  // TODO: avoid this by better synchronization!
  private def protectiveDelay() {
    Thread.sleep(100)
  }

  def printError(msg: String) {
    println(errorColor + msg + defaultColor)
  }

  def printSuccess(msg: String) {
    println(successColor + msg + defaultColor)
  }

  def printHighlight(msg: String) {
    println(highlightColor + msg + defaultColor)
  }

  def colorsEnabled:Boolean = Config[Boolean]("tttish", "color") getOrElse false
  def _col(col: String) = if (colorsEnabled) col else ""

  def defaultColor = _col(Console.WHITE + Console.RESET)
  def highlightColor = _col(Console.WHITE + Console.BOLD)
  def durationColor = _col(Console.GREEN + Console.BOLD)
  def userColor = _col(Console.BLUE + Console.BOLD)
  def errorColor = _col(Console.RED)
  def successColor = _col(Console.GREEN)
}
