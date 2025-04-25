#!/bin/bash

# Controlla se la versione è passata come parametro
if [ -z "$1" ]; then
  echo "Version parameter is missing."
  exit 1
fi

# La versione da aggiornare
NEW_VERSION=$1

# Trova e aggiorna SOLO la riga che contiene "implementation("io.axept.android:android-sdk:"
sed -i "/implementation(\"io.axept.android:android-sdk:/s/\"[^\"]*\"/\"$NEW_VERSION\"/" README.md

echo "README.md updated with version $NEW_VERSION."
