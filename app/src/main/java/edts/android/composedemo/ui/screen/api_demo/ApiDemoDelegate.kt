package edts.android.composedemo.ui.screen.api_demo

interface ApiDemoDelegate {
    fun onBackToHome()
}

/** dummy delegate object for compose preview */
object DummyApiDemoDelegate : ApiDemoDelegate {
    override fun onBackToHome() {}
}