package ru.moonlighters.ttt

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import Config._

class SettingsMapSpec extends FlatSpec with ShouldMatchers {

  behavior of "Config.deepMapAsSettingsMap"

  it should "convert empty map to empty" in {
    deepMapAsSettingsMap(JMap()) should equal (Map())
  }

  it should "convert 1-level map to 1-level map" in {
    val map1 = JMap("alpha" -> "aaa", "beta" -> 222)

    deepMapAsSettingsMap(map1) should equal (Map("alpha" -> "aaa", "beta" -> 222))
  }

  it should "convert 2-level map to 2-level map" in {
    val map2 =
      JMap("brother" -> JMap("alpha" -> "aaa", "beta" -> 222),
        "sister" -> JMap("rose" -> 'flower, "unicorn" -> 'animal))

    deepMapAsSettingsMap(map2) should equal (
      Map("brother" -> Map("alpha" -> "aaa", "beta" -> 222),
          "sister" -> Map("rose" -> 'flower, "unicorn" -> 'animal))
    )
  }

  it should "convert 3-level map to 3-level map" in {
    val map3 = JMap("a" -> JMap("b" -> JMap("c" -> "d")))

    deepMapAsSettingsMap(map3) should equal (Map("a" -> Map("b" -> Map("c" -> "d"))))
  }

  behavior of "Config.deepMergeSettings"
  implicit def settingsMapToDeepAddable(m: SettingsMap):DeepAddableMap = new DeepAddableMap(m)

  it should "give empty map from merging two empty map" in {
    Map().asInstanceOf[SettingsMap] +++ Map() should equal (Map())
  }

  it should "not change map by adding empty map" in {
    Map("a" -> Map("b" -> "c")) +++ Map() should equal (Map("a" -> Map("b" -> "c")))
    Map().asInstanceOf[SettingsMap] +++ Map("a" -> Map("b" -> "c")) should equal (Map("a" -> Map("b" -> "c")))
  }

  it should "unite non-intersecting maps" in {
    val map1 = Map("a" -> Map("b" -> "c"))
    val map2 = Map("d" -> Map("e" -> "f"))
    map1 +++ map2 should equal (Map("a" -> Map("b" -> "c"), "d" -> Map("e" -> "f")))
  }

  it should "overwrite the same key in sub-map" in {
    val map1 = Map("a" -> Map("b" -> "c"))
    val map2 = Map("a" -> Map("b" -> "D"))
    map1 +++ map2 should equal (Map("a" -> Map("b" -> "D")))
    map2 +++ map1 should equal (Map("a" -> Map("b" -> "c")))
  }

  it should "unite different keys in sub-map" in {
    val map1 = Map("a" -> Map("b" -> "c"))
    val map2 = Map("a" -> Map("d" -> "e"))
    val map12 = Map("a" -> Map("b" -> "c", "d" -> "e"))
    map1 +++ map2 should equal (map12)
    map2 +++ map1 should equal (map12)
  }

  it should "unite different keys in sub-sub-map" in {
    val map1 = Map("a" ->
                    Map("b" -> "c",
                        "d" ->
                          Map("e" -> "f")))
    val map2 = Map("a" ->
                    Map("x" -> "y",
                        "d" ->
                          Map("e" -> "G")))
    map1 +++ map2 should equal (Map("a" ->
                    Map("b" -> "c",
                        "x" -> "y",
                        "d" ->
                          Map("e" -> "G"))))
    map2 +++ map1 should equal (Map("a" ->
                    Map("b" -> "c",
                        "x" -> "y",
                        "d" ->
                          Map("e" -> "f"))))
  }

  it should "unite different keys while recursively merging maps at same keys" in {
    /* For the ease of checking:
       - symbols (!?&...) are used for non-conflicting keys
       - letters are used for keys that are merged,
         - while uppercase letters are values that will replace another value
    */
    val map1 = Map("_" -> "_",
                   "x" -> "y",
                   "a" ->
                    Map("?" -> "?",
                        "b" -> "c",
                        "d" ->
                          Map("#" -> "#",
                              "e" -> "f"
                          )
                    )
    )
    val map2 = Map("!" -> "!",
                   "x" -> "ZZ",
                   "a" ->
                    Map("&" -> "&",
                        "b" -> "CC",
                        "d" ->
                          Map("*" -> "*",
                              "e" -> "GG"
                          )
                    )
    )
    val res = Map( "_" -> "_",
                   "!" -> "!",
                   "x" -> "ZZ",
                   "a" ->
                    Map("?" -> "?",
                        "&" -> "&",
                        "b" -> "CC",
                        "d" ->
                          Map("#" -> "#",
                              "*" -> "*",
                              "e" -> "GG"
                          )
                    )
    )
    map1 +++ map2 should equal (res)
  }

  /**
   * Helper method: generates Java Hash Map with the following elements
   *
   * @param elems elements to be added to map
   * @return resulting Java Hash Map (java.util.HashMap)
   */
  def JMap(elems: (String,Any)*) = {
    val jmap = new java.util.HashMap[String, Any]
    for ((k,v) <- elems) {
      jmap.put(k, v)
    }
    jmap
  }

  /**
   * Helper wrapper over SettingsMap, giving alias operator +++ to deepMergeSettings
   * @param m original map to be wrapped
   */
  class DeepAddableMap(val m: SettingsMap) {
    def +++(another: SettingsMap) = deepMergeSettings(m, another)
  }
}
