package com.freedomcut.bridge

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class CapCutService : AccessibilityService() {
 companion object { var instance: CapCutService? = null; private set }
 private var armed = false
 override fun onServiceConnected() { super.onServiceConnected(); instance = this }
 fun arm() { armed = true }
 fun stop() { armed = false }
 override fun onAccessibilityEvent(event: AccessibilityEvent?) {
  if (!armed || event?.packageName?.toString() != "com.lemon.lvoverseas") return
  val root = rootInActiveWindow ?: return
  val button = findButton(root, listOf("Nuovo progetto", "New project", "Crea progetto"))
  if (button != null) { armed = false; button.performAction(AccessibilityNodeInfo.ACTION_CLICK) }
 }
 private fun findButton(node: AccessibilityNodeInfo, names: List<String>): AccessibilityNodeInfo? {
  val label = listOfNotNull(node.text?.toString(), node.contentDescription?.toString()).joinToString(" ").trim()
  if (names.any { label.equals(it, ignoreCase = true) }) {
   var target: AccessibilityNodeInfo? = node
   while (target != null) {
    if (target.isClickable && target.isEnabled) return target
    target = target.parent
   }
  }
  for (i in 0 until node.childCount) {
   node.getChild(i)?.let { child -> findButton(child, names)?.let { return it } }
  }
  return null
 }
 override fun onInterrupt() { stop() }
 override fun onDestroy() { stop(); instance = null; super.onDestroy() }
}