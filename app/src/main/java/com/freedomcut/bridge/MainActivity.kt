package com.freedomcut.bridge

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
 private val capcutPackage = "com.lemon.lvoverseas"
 private fun openCapCut() {
  val launch = packageManager.getLaunchIntentForPackage(capcutPackage)
  if (launch != null) startActivity(launch)
  else Toast.makeText(this, "CapCut non trovato: installa l'app ufficiale", Toast.LENGTH_LONG).show()
 }
 private fun openOfficialWeb() {
  val uri = Uri.parse("https://www.capcut.com/")
  val intent = Intent(Intent.ACTION_VIEW, uri).apply {
   addCategory(Intent.CATEGORY_BROWSABLE)
   setPackage("com.android.chrome")
  }
  try { startActivity(intent) }
  catch (_: ActivityNotFoundException) {
   startActivity(Intent(Intent.ACTION_VIEW, uri).addCategory(Intent.CATEGORY_BROWSABLE))
  }
 }
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  val layout = LinearLayout(this).apply {
   orientation = LinearLayout.VERTICAL
   setPadding(32, 40, 32, 32)
  }
  val title = TextView(this).apply {
   text = "FreedomCut AI Bridge v0.2"
   textSize = 24f
  }
  layout.addView(title)
  val info = TextView(this).apply {
   text = "Account CapCut ufficiale\n\n1. Apri il sito in Chrome e registrati o accedi manualmente.\n2. Controlla sul sito l'eventuale saldo promozionale.\n3. Apri CapCut Android e accedi con lo stesso account.\n\nL'assegnazione dei crediti dipende da CapCut e non è garantita. FreedomCut non raccoglie password."
   textSize = 16f
   setPadding(0, 20, 0, 16)
  }
  layout.addView(info)
  fun add(label: String, action: () -> Unit) {
   layout.addView(Button(this).apply { text = label; setOnClickListener { action() } })
  }
  add("1. Registrazione Web CapCut (Chrome)") { openOfficialWeb() }
  add("2. Apri CapCut Android / Accedi") { openCapCut() }
  add("3. Controlla crediti sul sito ufficiale") { openOfficialWeb() }
  layout.addView(TextView(this).apply {
   text = "\nAutomazione CapCut sperimentale"
   textSize = 18f
  })
  add("Autorizza accessibilità") { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
  add("Avvia: Nuovo progetto") {
   if (CapCutService.instance == null) {
    Toast.makeText(this, "Attiva prima l'accessibilità", Toast.LENGTH_LONG).show()
   } else {
    CapCutService.instance?.arm()
    openCapCut()
   }
  }
  add("STOP automazione") {
   CapCutService.instance?.stop()
   Toast.makeText(this, "Automazione fermata", Toast.LENGTH_SHORT).show()
  }
  setContentView(ScrollView(this).apply { addView(layout) })
 }
}
