// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/william.stanistreet/Documents/Scala/play/play-template/conf/routes
// @DATE:Mon Jan 08 14:46:47 GMT 2024


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
