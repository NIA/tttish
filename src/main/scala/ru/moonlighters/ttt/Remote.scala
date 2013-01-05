package ru.moonlighters.ttt

import java.util.Date
import net.liftweb.json._
import org.scala_tools.time.Imports._
import dispatch._
import java.net.ConnectException

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

  def baseUrl = Config[String]("site", "base_url").getOrElse("empty")

  var apiKey = ""
  /** Set current api key and enable network operations.
    *
    * @return Promise[ Either[User]  ] which is complete after web operation are complete
    */
  def login(apiKey: String) = {
    this.apiKey = apiKey
    for (res <- load("account.json").right)
      yield extractObject[User](res)
  }

  /** Get the list of updates from server.
    *
    * @return Promise[ Either[ List[Update] ] ] which is complete after web operation are complete
    */
  def getUpdates = {
    for (res <- load("updates.json").right)
      yield extractObject[List[Update]](res)
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
      val ttt = url(baseUrl + "/" + urlSuffix).addQueryParameter("api_key", apiKey)
      val request = Http(ttt OK as.String).either
      for (err <- request.left)
        yield err match {
          case StatusCode(401) => new AuthException(apiKey)
          case StatusCode(406) => new AuthException("")
          // Java's ConnectException default message if address is not resolved is simply the address, no explanation
          case x:ConnectException => new ConnectException("Failed to connect to " + x.getMessage)
          case x => x
        }
    }
  }

  /** Extract object of type A from JSON
    *
    * According to liftweb-json docs type A can be:
    *   - case class
    *   - primitive (String, Boolean, Date, etc.)
    *   - supported collection type (List, Seq, Map[String, _], Set)
    *   - any type which has a configured custom deserializer
    * @tparam A the type to be extracted
    * @param jsonString a JSON string to parse
    * @return extracted object of type A
    */
  def extractObject[A](jsonString: String)(implicit mf: scala.reflect.Manifest[A]) = {
    val replacedString = jsonString.replaceAll("([+-]\\d\\d):(\\d\\d)", "$1$2") // Convert timezones from +07:00 to +0700 since SimpleDateFormat doesn't understand the former
    parse(replacedString).transform(transformUnderscored).extract[A]
  }
}
