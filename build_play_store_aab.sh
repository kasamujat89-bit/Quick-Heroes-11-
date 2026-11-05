#!/usr/bin/env bash
set -e

echo "=========================================================="
echo "🏏 Quick Heroes 11 - Play Store .aab (App Bundle) Generator"
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

# 2. Prepare release keystore if missing
if [ ! -f "my-upload-key.jks" ]; then
  echo "Auto-generating upload keystore for release bundle..."
  keytool -genkey -v -keystore my-upload-key.jks -alias upload -keypass "${KEY_PASSWORD:-android}" -storepass "${STORE_PASSWORD:-android}" -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Quick Heroes 11, OU=Cricket, O=QuickHeroes, L=Naliya, ST=Gujarat, C=IN"
fi

export KEYSTORE_PATH="$(pwd)/my-upload-key.jks"
export STORE_PASSWORD="${STORE_PASSWORD:-android}"
export KEY_PASSWORD="${KEY_PASSWORD:-android}"

echo "Building Play Store Release App Bundle (:app:bundleRelease)..."
gradle :app:bundleRelease

AAB_FILE=$(find app/build/outputs/bundle -name "*.aab" | head -n 1)

echo "=========================================================="
echo "✅ SUCCESS: .aab file generated successfully!"
echo "📍 File Path: $AAB_FILE"
echo "=========================================================="
