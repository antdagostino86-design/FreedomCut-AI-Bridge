package com.freedomcut.bridge

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(32,48,32,24) }
  val status = TextView(this).apply { text = "FreedomCut AI Bridge v0.1\n\nAutomazione sperimentale: apre CapCut e tenta di premere Nuovo progetto solo su richiesta.\n\nAttiva il servizio di accessibilità e torna qui."; textSize = 18f }
  layout.addView(status)
  fun add(label: String, action: () -> Unit) { layout.addView(Button(this).apply { text = label; setOnClickListener { action() } }) }
  add("1. Autorizza accessibilità") { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
  add("2. Apri CapCut") {
   val launch = packageManager.getLaunchIntentForPackage("com.lemon.lvoverseas")
   if (launch != null) startActivity(launch) else Toast.makeText(this,"CapCut non trovato",Toast.LENGTH_LONG).show()
  }
  add("3. Avvia: Nuovo progetto") {
   if (CapCutService.instance == null) Toast.makeText(this,"Attiva prima l'accessibilità",Toast.LENGTH_LONG).show()
   else { CapCutService.instance?.arm(); packageManager.getLaunchIntentForPackage("com.lemon.lvoverseas")?.let { startActivity(it) } }
  }
  add("STOP automazione") { CapCutService.instance?.stop(); Toast.makeText(this,"Automazione fermata",Toast.LENGTH_SHORT).show() }
  setContentView(layout)
 }
}