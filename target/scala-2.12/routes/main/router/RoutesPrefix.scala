
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/tokeburia/sandbox/paymentsystem/conf/routes
// @DATE:Wed Aug 16 12:36:06 BST 2017


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
