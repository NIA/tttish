TTTish: The Time Tracker interactive shell
==========================================

[![Build Status](https://travis-ci.org/NIA/tttish.png)](https://travis-ci.org/NIA/tttish)

An interactive console client for [TTT]: The Time Tracker written in **Scala**.

Build
-----

1. Install [SBT], a build tool for Scala
2. Run `sbt` from the root folder of the project (the folder where this file is)
3. Type `compile` to fetch dependencies and compile, `run` to run.
4. Type `assembly` to create portable jar including all dependencies in folder `target`
    * :warning: This jar may have really big size of some dozens of megabytes.

Configure SBT-IDEA integration
------------------------------

If SBT cmdline is not enough for you, do the following to start using [IDEA] with this project:

1. Make sure that [SBT] is installed anyway
2. Open [IDEA] and install [SBT plugin] and [Scala plugin] from repositories:

    1. Go to `File` > `Settings` > `Plugins`, click `Browse repositories...`
    2. Type `SBT` in the search field and double-click on the first result
    2. Do the same with `Scala` keyword
    3. Click `Yes`, `Close`, `OK` to apply changes and agree to restart IDEA
    4. After restart go to `File` > `Settings` > `SBT` and fill in the path to `sbt-launch.jar` (should be in the installation dir of SBT, e.g. `C:\Program Files (x86)\sbt\sbt-launch.jar` for 64-bit Windows)

3. Run `sbt gen-idea` from the root folder of the project (the folder where this file is)
4. Open the generated project in [IDEA]. Configure Run configuration to be able to run TTTish from it:
    1. Go to `Run` > `Edit configurations`
    2. Click green plus (`Add new configuration`), choose `Application`
    3. Fill in `Main Class` with `ru.moonlighters.tttish.Main` (or choose it from `...` button)
    4. Select `tttish` in `Use classpath or module`
    5. Remove `Make` from `Before launch` list, instead add SBT action by clicking green plus (`Add`), choosing `SBT` and selecting `compile` in the dropdown list.

If your IDE is not [IDEA]... Well, have fun ;) Try googling for "%yourIDE% sbt integration" or ask friends... 

Usage
-----

Place your `.tttrc` file (e.g. downloaded from [Account] page) in your home dir or working dir of this app.

Run, then enter commands one by one. Enter `help` for the list of available commands.

You can use `login` command to login if you do not provide `.tttrc` or it doesn't contain your api key.

  [TTT]: http://ttt.lab9.ru
  [IDEA]: http://www.jetbrains.com/idea
  [SBT]: http://www.scala-sbt.org
  [SBT plugin]: http://plugins.intellij.net/plugin?pluginId=5007
  [Scala plugin]: http://plugins.intellij.net/plugin/?id=1347
  [Account]: http://ttt.lab9.ru/account

