package ru.moonlighters.ttt

import org.yaml.snakeyaml.Yaml
import collection.JavaConversions._
import java.lang.ClassCastException
import java.io.File
import io.Source

object Config {

  case class ReadException(msg: String) extends Exception(msg)
  type JavaMap = java.util.Map[String, Any]
  type SettingsMap = Map[String, Any]

  private var current:SettingsMap = defaults

  /**
   * Initializes configuration by loading all existing files from configFiles,
   * and then from all existing files given as arguments. Thus the latter will
   * have higher priority.
   *
   * @param extraConfigFiles additional paths to config files with the highest priority
   */
  def init(extraConfigFiles: Seq[String] = Seq()) {
    reset(defaults)
    (configFiles ++ extraConfigFiles) map { new File(_) } filter { _.canRead } foreach load
  }

  /**
   * Hard-reset config object to given settings map
   *
   * @param settings the settings that will be set
   */
  def reset(settings: SettingsMap) {
    current = settings
  }

  /**
   * Reads settings from file and merge with current settings
   * Settings from file has higher priority over current settings
   *
   * @param file yaml file to parse
   */
  def load(file: File) {
    try {
      val settings = parse(Source.fromFile(file).mkString)
      current = deepMergeSettings(current, settings)
    } catch {
      case ReadException(msg) => throw new ReadException("Error in config file " + file.getAbsolutePath + ": " + msg)
    }
  }

  /**
   * Given keys = Seq(k1, k2, kn) it will try to fetch the value
   * from settings map, assuming that under key k1 is another map holding key k2 etc.
   * The result will be Some() of this: map(k1)(k2)...(kn) if all the keys are present
   * and the type of value is A, or None otherwise
   *
   * @param keys a sequence of keys to be queried recursively
   * @tparam T the required type of the value
   * @return Some(value) from map if it is found and has type A, otherwise None
   */
  def apply[T <: AnyRef](keys: String*)(implicit mf: ClassManifest[T]): Option[T] = _get[T](current, keys)

  /**
   * HACK: The following is a way to emulate partial template specialization. The following functions would not
   * compile if they had similar signature after type erasure (Option[Int] and Option[Boolean] both become simply Option)
   * To work around this we add nominal implicit parameters of different type to each function
   * and define implicit values for them below. This doesn't affect invoking anyway: Config[Int]("category", "key")
   */

  def apply[T <: Int](keys: String*)(implicit ev: T =:= Int, void: VoidI): Option[Int]
    = _get[java.lang.Integer](current, keys) map Integer2int

  def apply[T <: Boolean](keys: String*)(implicit ev: T =:= Boolean, void: VoidB): Option[Boolean]
   = _get[java.lang.Boolean](current, keys) map Boolean2boolean

  def apply[T <: Long](keys: String*)(implicit ev: T =:= Long, void: VoidL): Option[Long]
   = _get[java.lang.Long](current, keys) map Long2long

  def apply[T <: Float](keys: String*)(implicit ev: T =:= Float, void: VoidF): Option[Float]
   = _get[java.lang.Float](current, keys) map Float2float

  def apply[T <: Double](keys: String*)(implicit ev: T =:= Double, void: VoidD): Option[Double]
   = _get[java.lang.Double](current, keys) map Double2double

  class VoidI; class VoidB; class VoidL; class VoidF; class VoidD
  implicit val voidI = new VoidI; implicit val voidB = new VoidB; implicit val voidL = new VoidL; implicit val voidF = new VoidF; implicit val voidD = new VoidD

  /*
    Internal recursive worker for apply
   */
  def _get[T](map: SettingsMap, keys: Seq[String])(implicit mf: ClassManifest[T]): Option[T] = {
    val classT = mf.erasure

    if (keys.nonEmpty && map.contains(keys.head)) {
      keys match {
        // We cannot do .isInstanceOf[T] due to type erasure, so we use isAssignableFrom on its class objects
        case Seq(lastKey) if classT isAssignableFrom map(lastKey).getClass =>
          Some(map(lastKey).asInstanceOf[T])
        // On class mismatch: return None to avoid match on next statement
        case Seq(_) => None
        case Seq(firstKey, others @ _*) if (map(firstKey).isInstanceOf[SettingsMap]) =>
          _get(map(firstKey).asInstanceOf[SettingsMap], others)
        case _ => None
      }
    } else {
      None
    }
  }

  /**
   * Parses yaml string containing settings map
   *
   * @param yaml yaml string as it is read from .tttrc file
   * @return map of maps representing this yaml tree structure
   */
  def parse(yaml: String) = {
    val res = new Yaml().load(yaml)
    if (res == null) {
      throw new ReadException("Config doesn't contain any valid YAML lines")
    }
    try {
      deepMapAsSettingsMap(res.asInstanceOf[JavaMap])
    } catch {
      case e: ClassCastException => throw new ReadException("Incorrect format of config")
    }
  }

  /**
   * Default settings. They have the lowest priority when loading settings.
   */
  val defaults = Map(
    "site" -> Map(
      "base_url" -> "http://ttt.lab9.ru",
      "api_key" -> ""
    ),
    "tttish" -> Map(
      "color" -> true
    )
  )

  /**
   * Locations to seek config files in
   *
   * Last settings added overwrite previous on conflict,
   * thus the last item it the list has the highest priority
   */
  val configFiles = Seq(
    // Assumed for global, system-wide settings like site:base_url
    "/etc/tttrc",
    // User-specific values like site:api_key and overrides
    "$HOME/.tttrc", "%USERPROFILE%/.tttrc",
    // Special overrides for current working dir
    "../.tttrc", ".tttrc"
  )

  /**
   * Merges settings maps preserving tree structure:
   *
   *   a ->
   *     b -> bb
   *     c -> cc
   *
   * merged with
   *
   *   a ->
   *     c -> ccc
   *     d -> ddd
   *
   * does not replace entire "a ->" subtree, but only its repeated keys:
   *
   *   a ->
   *     b -> bb
   *     c -> ccc
   *     d -> ddd
   *
   * This allows to have combination of settings from two maps, second having higher priority
   *
   * @param orig original map of settings
   * @param latter latter map of settings to be merged with original one
   */
  def deepMergeSettings(orig: SettingsMap, latter: SettingsMap): SettingsMap = {
    (orig ++ latter) map { case(key, value) =>
      if (orig.contains(key)) {
        (orig(key), value) match {
          case (origMap: SettingsMap, newMap: SettingsMap) => key -> deepMergeSettings(origMap, newMap)
          case (origValue, newValue) => key -> newValue
        }
      } else {
        key -> value
      }
    }
  }

  /**
   * Converts java.util.Map[String,Any] to immutable.Map[String,Any] recursively:
   * also converting each "java map" that appeared as value to "scala map"
   *
   * @param jmap original "java map"
   * @return jmap converted to "scala map"
   */
  def deepMapAsSettingsMap(jmap: JavaMap): SettingsMap = {
    mapAsScalaMap(jmap).toMap mapValues  { _ match {
      case jmap: JavaMap => deepMapAsSettingsMap(jmap)
      case value => value
    }}
  }
}
