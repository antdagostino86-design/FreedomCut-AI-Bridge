package com.freedomcut.bridge

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
 private val capcutPackage = "com.lemon.lvoverseas"
 private val officialUrl = "https://www.capcut.com/"
 private var portal: WebView? = null
 private var desktopMode = true
 private lateinit var home: ScrollView
 private fun openCapCut() {
  val launch = packageManager.getLaunchIntentForPackage(capcutPackage)
  if (launch != null) startActivity(launch)
  else Toast.makeText(this, "CapCut non trovato", Toast.LENGTH_LONG).show()
 }
 private fun showPortal() {
  home.visibility = android.view.View.GONE
  val web = WebView(this)
  portal = web
  web.settings.javaScriptEnabled = true
  web.settings.useWideViewPort = true
  web.settings.loadWithOverviewMode = true
  web.settings.builtInZoomControls = true
  web.settings.displayZoomControls = false
  web.settings.setSupportZoom(true)
  val mobileAgent = web.settings.userAgentString
  val desktopAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"
  web.settings.userAgentString = if (desktopMode) desktopAgent else mobileAgent
  web.settings.domStorageEnabled = true
  web.settings.allowFileAccess = false
  web.settings.allowContentAccess = false
  CookieManager.getInstance().setAcceptCookie(true)
  web.webChromeClient = WebChromeClient()
  web.webViewClient = object : WebViewClient() {
   override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
    val uri = request?.url ?: return false
    val scheme = uri.scheme?.lowercase()
    if (scheme != "https") return true
    val host = uri.host?.lowercase() ?: return true
    val allowed = host == "capcut.com" || host.endsWith(".capcut.com") ||
      host == "bytedance.com" || host.endsWith(".bytedance.com") ||
      host == "tiktok.com" || host.endsWith(".tiktok.com")
    if (!allowed) {
     Toast.makeText(this@MainActivity, "Dominio esterno: apertura bloccata nel portale", Toast.LENGTH_SHORT).show()
    }
    return !allowed
   }
  }
  val page = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
  page.addView(Button(this).apply {
   text = "Torna a FreedomCut"
   setOnClickListener { closePortal() }
  })
  page.addView(Button(this).apply {
   text = if (desktopMode) "Vista desktop attiva · passa a mobile" else "Vista mobile attiva · passa a desktop"
   setOnClickListener {
    desktopMode = !desktopMode
    web.settings.userAgentString = if (desktopMode) desktopAgent else mobileAgent
    text = if (desktopMode) "Vista desktop attiva · passa a mobile" else "Vista mobile attiva · passa a desktop"
    web.reload()
   }
  })
  page.addView(web, LinearLayout.LayoutParams(-1, 0, 1f))
  setContentView(page)
  web.loadUrl(officialUrl)
 }
 private fun closePortal() {
  portal?.apply { stopLoading(); destroy() }
  portal = null
  home.visibility = android.view.View.VISIBLE
  setContentView(home)
 }
 @Deprecated("Deprecated in Java")
 override fun onBackPressed() {
  val web = portal
  if (web != null && web.canGoBack()) web.goBack()
  else if (web != null) closePortal()
  else super.onBackPressed()
 }
 override fun onDestroy() {
  portal?.destroy()
  portal = null
  super.onDestroy()
 }
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  val layout = LinearLayout(this).apply {
   orientation = LinearLayout.VERTICAL
   setPadding(32, 40, 32, 32)
  }
  layout.addView(TextView(this).apply {
   text = "FreedomCut AI Bridge v0.4"
   textSize = 24f
  })
  layout.addView(TextView(this).apply {
   text = "Portale CapCut integrato in modalità desktop\n\nRegistrati o accedi manualmente sul sito ufficiale senza uscire da FreedomCut. Le credenziali restano nella pagina CapCut. L'assegnazione di eventuali crediti promozionali non è garantita; alcuni accessi esterni potrebbero non funzionare in WebView."
   textSize = 16f
   setPadding(0, 20, 0, 16)
  })
  fun add(label: String, action: () -> Unit) {
   layout.addView(Button(this).apply { text = label; setOnClickListener { action() } })
  }
  add("1. Apri portale CapCut integrato") { showPortal() }
  add("2. Apri CapCut Android / Accedi") { openCapCut() }
  add("3. Verifica crediti nel portale") { showPortal() }
  layout.addView(TextView(this).apply { text = "\nAutomazione CapCut sperimentale"; textSize = 18f })
  add("Autorizza accessibilità") { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
  add("Avvia: Nuovo progetto") {
   if (CapCutService.instance == null) Toast.makeText(this, "Attiva prima l'accessibilità", Toast.LENGTH_LONG).show()
   else { CapCutService.instance?.arm(); openCapCut() }
  }
  add("STOP automazione") {
   CapCutService.instance?.stop()
   Toast.makeText(this, "Automazione fermata", Toast.LENGTH_SHORT).show()
  }
  home = ScrollView(this).apply { addView(layout) }
  setContentView(home)
 }
}
