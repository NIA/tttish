package ru.moonlighters.ttt

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class ConfigSpec extends FlatSpec with ShouldMatchers{

  behavior of "Config.apply"

  it should "get None from empty map" in {
    Config reset Map()
    Config[String]("aaa") should be (None)
  }

  it should "get root key" in {
    Config reset Map("key" -> "value")
    Config[String]("key") should be (Some("value"))
    Config[Int]("key") should be (None)
    Config[Int]("not-a-key") should be (None)
  }

  it should "get None if there is a subtree, not value" in {
    Config reset Map("key" -> Map("subkey" -> "value"))
    Config[String]("key") should be (None)
  }

  it should "return the entire subtree if properly asked" in {
    Config reset Map("key" -> Map("subkey" -> "value"))
    Config[Config.SettingsMap]("key") should be (Some(Map("subkey" -> "value")))
  }

  it should "get 2-level-deep value" in {
    Config reset Map("key" -> Map("subkey" -> "value"))
    Config[String]("key", "subkey") should be (Some("value"))
    Config[Int]("key", "subkey") should be (None)
    Config[String]("key", "garbage") should be (None)
    Config[String]("garbage", "garbage") should be (None)
  }

  it should "get 3-level-deep value" in {
    Config reset Map("key" -> Map("subkey" -> Map("subsubkey" -> "value")))
    Config[String]("key", "subkey", "subsubkey") should be (Some("value"))
    Config[Int]("key", "subkey", "subsubkey") should be (None)
    Config[String]("key", "subkey", "garbage") should be (None)
    Config[String]("garbage", "garbage", "garbage") should be (None)
  }
}
