// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/william.stanistreet/Documents/Scala/play/play-template/conf/routes
// @DATE:Fri Jan 05 14:39:49 GMT 2024


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
