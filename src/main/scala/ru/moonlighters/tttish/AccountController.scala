package ru.moonlighters.tttish

import ru.moonlighters.ttt.Remote

object AccountController extends Controller {
  def login(apiKey: String) {
    if(apiKey.isEmpty) {
      println("Hey, where is the api key?\n  Usage: login <key>")
      return
    }

    val userPromise = Remote.login(apiKey)
    println("Connecting...")

    // If successful
    for (u <- userPromise.right) {
      println("Logged in as " + u.nickname)
    }

    // If failed
    for (error <- userPromise.left) {
      println("Login failed, try again: " + error.getMessage)
    }

    // Wait till complete
    waitTillComplete(userPromise)
  }
}
