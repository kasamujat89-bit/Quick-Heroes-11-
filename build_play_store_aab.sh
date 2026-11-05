#!/usr/bin/env bash
set -e

echo "=========================================================="
echo "🏏 Quick Heroes 11 - Play Store .aab (App Bundle) Generator"
echo "=========================================================="

if [ -f "my-upload-key.jks" ]; then
  echo "Found my-upload-key.jks. Building Signed Release App Bundle (:app:bundleRelease)..."
  export KEYSTORE_PATH="$(pwd)/my-upload-key.jks"
  export STORE_PASSWORD="${STORE_PASSWORD:-}"
  export KEY_PASSWORD="${KEY_PASSWORD:-}"
  gradle :app:bundleRelease
else
  echo "No my-upload-key.jks found. Building Signed Debug App Bundle (:app:bundleDebug)..."
  gradle :app:bundleDebug
fi

AAB_FILE=$(find app/build/outputs/bundle -name "*.aab" | head -n 1)

echo "=========================================================="
echo "✅ SUCCESS: .aab file generated successfully!"
echo "📍 File Path: $AAB_FILE"
echo "💡 Tip: To build an official Play Store Release Bundle with your own key, run:"
echo "   keytool -genkey -v -keystore my-upload-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload"
echo "=========================================================="
