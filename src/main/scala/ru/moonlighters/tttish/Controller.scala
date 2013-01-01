package ru.moonlighters.tttish

import dispatch.Promise

trait Controller {
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

  def printHightlight(msg: String) {
    println(Console.BOLD + msg + Console.RESET)
  }

  val defaultColor = Console.WHITE + Console.RESET
  val durationColor = Console.GREEN + Console.BOLD
  val userColor = Console.BLUE + Console.BOLD
  val errorColor = Console.RED
  val successColor = Console.GREEN
}
