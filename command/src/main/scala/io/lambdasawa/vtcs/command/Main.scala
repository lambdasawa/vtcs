package io.lambdasawa.vtcs.command
import io.lambdasawa.vtcs.core.table.SlickCodeGen
import io.lambdasawa.vtcs.job.{Runner => JobRunner}
import io.lambdasawa.vtcs.web.{Runner => WebRunner}
import org.rogach.scallop.{ScallopConf, Subcommand}

object Main {

  def main(args: Array[String]): Unit = {
    run(args)
  }

  def run(args: Seq[String]): Unit = {
    val conf = new Conf(args)
    conf.subcommands.lastOption match {
      case Some(command) =>
        command match {
          case cmd: Cmd => cmd.run()
          case _        => command.printHelp()
        }
      case _ =>
        conf.printHelp()
    }
  }

  class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
    addSubcommand(new WebCommand)
    addSubcommand(new JobCommand)
    addSubcommand(new DBCommand)
    verify()
  }

  abstract class Cmd(name: String) extends Subcommand(name) with Runnable {
    override def run(): Unit = printHelp()
  }

  class WebCommand extends Cmd("web") {
    addSubcommand(new WebStartCommand)
  }

  class WebStartCommand extends Cmd("start") {
    override def run(): Unit = {
      WebRunner.run()
    }
  }

  class JobCommand extends Cmd("job") {
    addSubcommand(new JobStartCommand)
  }

  class JobStartCommand extends Cmd("start") {
    override def run(): Unit = {
      JobRunner.run()
    }
  }

  class DBCommand extends Cmd("db") {
    addSubcommand(new DBCodeGenCommand)
  }

  class DBCodeGenCommand extends Cmd("code-gen") {
    override def run(): Unit = {
      SlickCodeGen.run()
    }
  }

}
