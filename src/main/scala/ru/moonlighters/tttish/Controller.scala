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
}
