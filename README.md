# FreedomCut AI Bridge
Android Kotlin experimental CapCut accessibility bridge for OPPO and other Android devices.

## Build
GitHub Actions automatically runs on pushes to main or manually from Actions > Build FreedomCut APK. Download the `FreedomCutBridge-debug-apk` artifact after a successful build.

## Current capabilities
Open CapCut, request accessibility permission, attempt to click New project, STOP. This is an experimental proof of concept, not a complete AI editor or guaranteed CapCut integration. CapCut UI may vary by version. No external AI provider is configured yet.

## Privacy
The accessibility service is limited to the CapCut package and runs its click automation only after the user presses Start. Do not grant accessibility access unless you understand and trust the source code.
