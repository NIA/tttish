package ru.moonlighters.ttt

import java.util.Date
import net.liftweb.json._
import org.scala_tools.time.Imports._

object Remote {
  case class User(nickname: String, name: String)
  case class Update(humanMessage: String, user: User, startedAt: Date, finishedAt: Option[Date], hours: Option[Double], kind: Symbol) {
    def startedAtDt = new DateTime(startedAt)
    def finishedAtDt = finishedAt map { new DateTime(_) }
  }

  def getUpdates = {
    val json = parse(load) transform {
      case JField(Underscored(a, b), x) => JField(a + b.capitalize, x)
    }
    json.extract[List[Update]]
  }

  val Underscored = "([a-z]+)_([a-z]+)".r
  implicit val formats = new DefaultFormats {
    override def dateFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
  }

  // A stub of function that will connect to server and load data
  def load = {
    """
      |[
      |  {
      |    "human_message":"Test message",
      |    "finished_at":null,
      |    "kind":"status",
      |    "uuid":"a4494860-4dc4-11e2-82b0-fefd6d4acbac",
      |    "updated_at":"2012-12-24T19:22:53+07:00",
      |    "user":{
      |      "name":"Tester by NIA",
      |      "created_at":"2012-12-24T19:20:36+07:00",
      |      "nickname":"tester"
      |    },
      |    "started_at":"2012-12-24T19:22:53+07:00",
      |    "hours":null
      |  },
      |  {
      |    "human_message":"#tttish ",
      |    "finished_at":null,
      |    "kind":"update",
      |    "uuid":"adc36f22-4da3-11e2-82b0-fefd6d4acbac",
      |    "updated_at":"2012-12-24T15:26:56+07:00",
      |    "user":{
      |      "name":"\u0418\u0432\u0430\u043d \u041d\u043e\u0432\u0438\u043a\u043e\u0432",
      |      "created_at":"2009-09-23T18:42:35+07:00",
      |      "nickname":"NIA"
      |    },
      |    "started_at":"2012-12-24T14:56:56+07:00",
      |    "hours":null
      |  },
      |  {
      |    "human_message":"#exclsr ",
      |    "finished_at":"2012-12-24T13:46:30+07:00",
      |    "kind":"update",
      |    "uuid":"a45ab6d8-4d95-11e2-89de-fefd6d4acbac",
      |    "updated_at":"2012-12-24T13:46:30+07:00",
      |    "user":{
      |      "name":"\u0412\u043b\u0430\u0434\u0438\u043c\u0438\u0440 \u041f\u0430\u0440\u0444\u0438\u043d\u0435\u043d\u043a\u043e",
      |      "created_at":"2009-09-22T19:46:28+07:00",
      |      "nickname":"cypok"
      |    },
      |    "started_at":"2012-12-24T10:36:27+07:00",
      |    "hours":3.18
      |  },
      |  {
      |    "human_message":"#tttish ",
      |    "finished_at":"2012-12-24T00:05:04+07:00",
      |    "kind":"update",
      |    "uuid":"55cad5f4-4d1b-11e2-8fdb-fefd6d4acbac",
      |    "updated_at":"2012-12-24T15:25:54+07:00",
      |    "user":{
      |      "name":"\u0418\u0432\u0430\u043d \u041d\u043e\u0432\u0438\u043a\u043e\u0432",
      |      "created_at":"2009-09-23T18:42:35+07:00",
      |      "nickname":"NIA"
      |    },
      |    "started_at":"2012-12-23T22:15:00+07:00",
      |    "hours":1.9
      |  },
      |  {
      |    "human_message":"#medmanager %design% ",
      |    "finished_at":"2012-12-23T15:49:50+07:00",
      |    "kind":"update",
      |    "uuid":"f72dd3bc-4cdd-11e2-a4ac-fefd6d4acbac",
      |    "updated_at":"2012-12-23T16:25:25+07:00",
      |    "user":{
      |      "name":"\u0418\u0432\u0430\u043d \u041d\u043e\u0432\u0438\u043a\u043e\u0432",
      |      "created_at":"2009-09-23T18:42:35+07:00",
      |      "nickname":"NIA"
      |    },
      |    "started_at":"2012-12-23T15:21:39+07:00",
      |    "hours":0.5
      |  },
      |  {
      |    "human_message":"#medmanager %design% ",
      |    "finished_at":"2012-12-23T00:27:16+07:00",
      |    "kind":"update",
      |    "uuid":"f6c29e96-4c54-11e2-9347-fefd6d4acbac",
      |    "updated_at":"2012-12-23T15:51:39+07:00",
      |    "user":{
      |      "name":"\u0418\u0432\u0430\u043d \u041d\u043e\u0432\u0438\u043a\u043e\u0432",
      |      "created_at":"2009-09-23T18:42:35+07:00",
      |      "nickname":"NIA"
      |    },
      |    "started_at":"2012-12-22T23:00:57+07:00",
      |    "hours":1.5
      |  },
      |  {
      |    "human_message":"#coding %qttt%",
      |    "finished_at":"2012-12-22T22:20:56+07:00",
      |    "kind":"update",
      |    "uuid":"d8a356f0-4c52-11e2-9347-fefd6d4acbac",
      |    "updated_at":"2012-12-22T23:30:42+07:00",
      |    "user":{
      |      "name":"\u0418\u0432\u0430\u043d \u041d\u043e\u0432\u0438\u043a\u043e\u0432",
      |      "created_at":"2009-09-23T18:42:35+07:00",
      |      "nickname":"NIA"
      |    },
      |    "started_at":"2012-12-22T22:00:48+07:00",
      |    "hours":0.4
      |  },
      |  {
      |    "human_message":"#medmanager %design%",
      |    "finished_at":"2012-12-22T22:00:53+07:00",
      |    "kind":"update",
      |    "uuid":"ce2ec81c-4c39-11e2-9347-fefd6d4acbac",
      |    "updated_at":"2012-12-23T15:51:39+07:00",
      |    "user":{
      |      "name":"\u0418\u0432\u0430\u043d \u041d\u043e\u0432\u0438\u043a\u043e\u0432",
      |      "created_at":"2009-09-23T18:42:35+07:00",
      |      "nickname":"NIA"
      |    },
      |    "started_at":"2012-12-22T20:45:33+07:00",
      |    "hours":1.3
      |  },
      |  {
      |    "human_message":"#coding %scala% ",
      |    "finished_at":"2012-12-22T19:36:06+07:00",
      |    "kind":"update",
      |    "uuid":"f4fc306a-4c2a-11e2-9347-fefd6d4acbac",
      |    "updated_at":"2012-12-22T19:36:06+07:00",
      |    "user":{
      |      "name":"\u0418\u0432\u0430\u043d \u041d\u043e\u0432\u0438\u043a\u043e\u0432",
      |      "created_at":"2009-09-23T18:42:35+07:00",
      |      "nickname":"NIA"
      |    },
      |    "started_at":"2012-12-22T16:50:15+07:00",
      |    "hours":2.8
      |  },
      |  {
      |    "human_message":"#coding %scala%",
      |    "finished_at":"2012-12-22T00:19:32+07:00",
      |    "kind":"update",
      |    "uuid":"1f7b84fc-4b87-11e2-a579-fefd6d4acbac",
      |    "updated_at":"2012-12-22T18:30:16+07:00",
      |    "user":{
      |      "name":"\u0418\u0432\u0430\u043d \u041d\u043e\u0432\u0438\u043a\u043e\u0432",
      |      "created_at":"2009-09-23T18:42:35+07:00",
      |      "nickname":"NIA"
      |    },
      |    "started_at":"2012-12-21T22:57:29+07:00",
      |    "hours":1.4
      |  },
      |  {
      |    "human_message":"#exclsr ",
      |    "finished_at":"2012-12-21T20:22:39+07:00",
      |    "kind":"update",
      |    "uuid":"8fed011c-4b22-11e2-9ac5-fefd6d4acbac",
      |    "updated_at":"2012-12-21T20:22:39+07:00",
      |    "user":{
      |      "name":"\u0412\u043b\u0430\u0434\u0438\u043c\u0438\u0440 \u041f\u0430\u0440\u0444\u0438\u043d\u0435\u043d\u043a\u043e",
      |      "created_at":"2009-09-22T19:46:28+07:00",
      |      "nickname":"cypok"
      |    },
      |    "started_at":"2012-12-21T10:37:39+07:00",
      |    "hours":9.77
      |  }
      |]""".stripMargin.replaceAll("([+-]\\d\\d):(\\d\\d)", "$1$2") // Convert timezones from +07:00 to +0700 since SimpleDateFormat doesn't understand the former
  }
}
