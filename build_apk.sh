#!/usr/bin/env bash
set -e

echo "=========================================================="
echo "🏏 Quick Heroes 11 - Installable APK Generator"
echo "=========================================================="

# 1. Restore debug.keystore if missing
if [ ! -f "debug.keystore" ]; then
  if [ -f "debug.keystore.base64" ]; then
    echo "Restoring debug.keystore from base64..."
    base64 -d debug.keystore.base64 > debug.keystore
  else
    keytool -genkey -v -keystore debug.keystore -storepass android -alias androiddebugkey -keypass android -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Android Debug,O=Android,C=US"
  fi
fi

echo "Building Debug APK (:app:assembleDebug)..."
gradle :app:assembleDebug

APK_FILE=$(find .build-outputs app/build/outputs/apk -name "*.apk" 2>/dev/null | head -n 1)

echo "=========================================================="
echo "✅ SUCCESS: APK file generated successfully!"
echo "📍 APK Path: $APK_FILE"
echo "=========================================================="
