package ru.moonlighters.tttish

import ru.moonlighters.ttt.Remote
import java.util.Date
import org.scala_tools.time.Imports._

object UpdatesController {
  def showUpdates() {
    println()
    for(u <- Remote.getUpdates) {
      displayUpdate(u)
    }
  }

  def displayUpdate(u: Remote.Update) {
    val startDate = u.startedAtDt.toLocalDate match {
      case x if x == LocalDate.now => "today"
      case x if x == LocalDate.now - 1.day => "yesterday"
      case x => x.toString(dateFormat)
    }
    val startTime = u.startedAtDt.toLocalTime.toString(timeFormat)
    val endTime = u.finishedAtDt match {
      case Some(x) => x.toLocalTime.toString(timeFormat)
      case None => "..."
    }
    val hours = u.hours match {
      case Some(x) => " (" + x + " h)"
      case None => ""
    }
    val suffix = u.kind match {
      case 'update => " - " + endTime + hours
      case _ => ""
    }
    println(startDate.capitalize + " " + startTime + suffix)
    println("@" + u.user.nickname + ": " + u.humanMessage)
    println()
  }

  val dateFormat = "dd.MM.yyyy"
  val timeFormat = "HH:mm"
}
