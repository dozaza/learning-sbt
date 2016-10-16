import complete._
import DefaultParsers._

def releaseParser(state: State): Parser[String] = ID

def releaseAction(state: State, version: String): State = {
  "all test" :: "publish" :: state
}

val ReleaseCommand = Command.apply("release") { releaseParser } { releaseAction }

commands += ReleaseCommand
