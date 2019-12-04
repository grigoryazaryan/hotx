#!/bin/sh
./gradlew assembleRelease
apk_path="app/build/outputs/apk/user/release/"
latest_apk="$(find "$apk_path" -name "*.apk" -exec basename {} \; | sort -r | head -1)";
if [ ! -z "$latest_apk" ]; then
	echo "found latest version $latest_apk"
	echo "starting upload"
	if scp "$apk_path$latest_apk" "www.zzz.biz"; then
		echo "file uploaded!"
	fi
else
	echo "*.apk file not found in $apk_path";
	exit;
fi