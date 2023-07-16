$release_path = "app/build/outputs/apk/release"
$sdk_version = (Get-ChildItem "$env:ANDROID_SDK_ROOT\build-tools\" -Attributes Directory -Name -Filter "*.*.*" | select -last 1)
& "$env:ANDROID_SDK_ROOT\build-tools\$sdk_version\zipalign.exe" -p -f 4 "$release_path/app-release-unsigned.apk" "$release_path/app-release-aligned.apk"
& "$env:ANDROID_SDK_ROOT\build-tools\$sdk_version\apksigner.bat" sign -v --ks release-key.jks --v1-signing-enabled true "$release_path/app-release-aligned.apk"
Move-Item -Path "$release_path/app-release-aligned.apk" -Destination "$release_path/app-release-signed.apk" -Force