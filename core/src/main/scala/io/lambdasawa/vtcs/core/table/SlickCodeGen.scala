package io.lambdasawa.vtcs.core.table

object SlickCodeGen {
  def run(): Unit = {
    val profile      = "slick.jdbc.MySQLProfile"
    val jdbcDriver   = "com.mysql.jdbc.Driver"
    val url          = "jdbc:mysql://localhost:3306/vtcs?useSSL=false"
    val outputFolder = "db/src/main/scala/"
    val pkg          = "io.lambdasawa.vtcs.db.table"
    val user         = "root"
    val password     = "root"

    slick.codegen.SourceCodeGenerator.main(
      Array(profile, jdbcDriver, url, outputFolder, pkg, user, password)
    )
  }
}
