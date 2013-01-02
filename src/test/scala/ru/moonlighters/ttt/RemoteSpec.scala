package ru.moonlighters.ttt

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import java.util.Date

class RemoteSpec extends FlatSpec with ShouldMatchers {

  behavior of "Remote"

  it should "not have api key initially" in {
    Remote.apiKey should be ('empty)
  }

  it should "extract user" in {
    val u = Remote.extractObject[Remote.User]("""{"name":"John","created_at":"2011-11-11T11:11:11+07:00","nickname":"joe"}""")
    u should have (
      'name ("John"),
      'nickname ("joe")
    )
  }

  it should "extract status update" in {
    val updates = Remote.extractObject[List[Remote.Update]](
      """
        |[
        |  {
        |    "human_message":"Test message",
        |    "finished_at":null,
        |    "kind":"status",
        |    "uuid":"a4494860-4dc4-11e2-82b0-fefd6d4acbac",
        |    "updated_at":"2012-12-24T19:22:53+07:00",
        |    "user":{
        |      "name":"John",
        |      "created_at":"2012-12-24T19:20:36+07:00",
        |      "nickname":"joe"
        |    },
        |    "started_at":"2012-12-24T19:22:53+07:00",
        |    "hours":null
        |  }
        |]
      """.stripMargin)

    updates should have size 1
    updates(0) should have (
      'humanMessage ("Test message"),
      'user (Remote.User(name = "John", nickname = "joe")),
      'kind ('status),
      'startedAt (new Date(Date.UTC(112, 11, 24, 12, 22, 53))),
      'finishedAt (None),
      'hours (None)
    )
  }

  it should "extract unfinished update" in {
    val updates = Remote.extractObject[List[Remote.Update]](
      """
        |[
        |  {
        |    "human_message":"#project",
        |    "finished_at":null,
        |    "kind":"update",
        |    "uuid":"a4494860-4dc4-11e2-82b0-fefd6d4acbac",
        |    "updated_at":"2012-12-24T19:22:53+07:00",
        |    "user":{
        |      "name":"John",
        |      "created_at":"2012-12-24T19:20:36+07:00",
        |      "nickname":"joe"
        |    },
        |    "started_at":"2012-12-24T19:22:53+07:00",
        |    "hours":null
        |  }
        |]
      """.stripMargin)

    updates should have size 1
    updates(0) should have (
      'humanMessage ("#project"),
      'user (Remote.User(name = "John", nickname = "joe")),
      'kind ('update),
      'startedAt (new Date(Date.UTC(112, 11, 24, 12, 22, 53))),
      'finishedAt (None),
      'hours (None)
    )
  }


  it should "extract finished update" in {
    val updates = Remote.extractObject[List[Remote.Update]](
      """
        |[
        |  {
        |    "human_message":"#project",
        |    "finished_at":"2012-12-24T20:52:53+07:00",
        |    "kind":"update",
        |    "uuid":"a4494860-4dc4-11e2-82b0-fefd6d4acbac",
        |    "updated_at":"2012-12-24T19:22:53+07:00",
        |    "user":{
        |      "name":"John",
        |      "created_at":"2012-12-24T19:20:36+07:00",
        |      "nickname":"joe"
        |    },
        |    "started_at":"2012-12-24T19:22:53+07:00",
        |    "hours":1.5
        |  }
        |]
      """.stripMargin)

    updates should have size 1
    updates(0) should have (
      'humanMessage ("#project"),
      'user (Remote.User(name = "John", nickname = "joe")),
      'kind ('update),
      'startedAt (new Date(Date.UTC(112, 11, 24, 12, 22, 53))),
      'finishedAt (Some(new Date(Date.UTC(112, 11, 24, 13, 52, 53)))),
      'hours (Some(1.5))
    )
  }

  it should "extract multiple updates" in {
    val updates = Remote.extractObject[List[Remote.Update]](
      """
        |[
        |  {
        |    "human_message":"#project",
        |    "finished_at":"2012-12-24T20:52:53+07:00",
        |    "kind":"update",
        |    "uuid":"a4494860-4dc4-11e2-82b0-fefd6d4acbac",
        |    "updated_at":"2012-12-24T19:22:53+07:00",
        |    "user":{
        |      "name":"John",
        |      "created_at":"2012-12-24T19:20:36+07:00",
        |      "nickname":"joe"
        |    },
        |    "started_at":"2012-12-24T19:22:53+07:00",
        |    "hours":1.5
        |  },
        |  {
        |    "human_message":"Test message",
        |    "finished_at":null,
        |    "kind":"status",
        |    "uuid":"a4494860-4dc4-11e2-89de-fefd6d4acbac",
        |    "updated_at":"2012-12-24T19:22:53+07:00",
        |    "user":{
        |      "name":"John",
        |      "created_at":"2012-12-24T19:20:36+07:00",
        |      "nickname":"joe"
        |    },
        |    "started_at":"2012-12-24T19:22:53+07:00",
        |    "hours":null
        |  }
        |]
      """.stripMargin)

    updates should have size 2
    updates map { _.humanMessage } should be (List("#project", "Test message"))
  }
}
