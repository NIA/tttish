package ru.moonlighters.ttt

import java.util.Date
import net.liftweb.json._
import org.scala_tools.time.Imports._
import dispatch._

object Remote {
  case class User(nickname: String, name: String)
  case class Update(humanMessage: String, user: User, startedAt: Date, finishedAt: Option[Date], hours: Option[Double], kind: Symbol) {
    def startedAtDt = new DateTime(startedAt)
    def finishedAtDt = finishedAt map { new DateTime(_) }
  }
  case class AuthException(apiKey: String) extends Exception {
    override def getMessage = apiKey match {
      case "" => "Not logged in. Use login command to login"
      case x => "Failed to authenticate with api key " + x
    }
  }

  val baseUrl = "http://ttt.lab9.ru/" // TODO: configurable

  var apiKey = ""
  /** Set current api key and enable network operations.
    *
    * @return Promise[ Either[User]  ] which is complete after web operation are complete
    */
  def login(apiKey: String) = {
    this.apiKey = apiKey
    for (res <- load("account.json").right)
      yield parse(res).transform(transformUnderscored).extract[User]
  }

  /** Get the list of updates from server.
    *
    * @return Promise[ Either[ List[Update] ] ] which is complete after web operation are complete
    */
  def getUpdates = {
    for (res <- load("updates.json").right)
      yield parse(res).transform(transformUnderscored).extract[List[Update]]
  }

  val Underscored = "([a-z]+)_([a-z]+)".r
  implicit val formats = new DefaultFormats {
    override def dateFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
  }
  val transformUnderscored:PartialFunction[JValue, JValue] = {
    case JField(Underscored(a, b), x) => JField(a + b.capitalize, x)
  }

  /** Load JSON data from server.
    *
    * @param urlSuffix - the part that will be added to baseUrl to form URL
    * @return Promise[ Either[String] ], where String is the list of updates in JSON format
    */
  def load(urlSuffix: String) = {
    if (apiKey.isEmpty) {
      Http.promise(Left(new AuthException(apiKey)))
    } else {
      val ttt = url(baseUrl + urlSuffix).addQueryParameter("api_key", apiKey)
      val request = Http(ttt OK as.String).either
      val replaced = for (res <- request.right)
        yield res.replaceAll("([+-]\\d\\d):(\\d\\d)", "$1$2") // Convert timezones from +07:00 to +0700 since SimpleDateFormat doesn't understand the former
      for (err <- replaced.left)
        yield err match {
          case StatusCode(401) => new AuthException(apiKey)
          case StatusCode(406) => new AuthException("")
          case x => x
        }
    }
  }
}
