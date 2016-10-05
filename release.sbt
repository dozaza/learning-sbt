val ReleaseCommand = Command.command("release") {
  state =>
    "test" :: "publish" :: state
}

commands += ReleaseCommand
