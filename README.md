# Android-Signature-And-Integrity-Check-Bypass
You can bypass various Android security system checks such as Signature Verification, Integrity Check, etc. with this repo.
# About this.
This is a hook for bypassing checks such as signature and integrity.

inspired from [ApkSignatureKiller](https://github.com/L-JINBIN/ApkSignatureKiller)


*Tested on the latest android sdk also and it's working fine.*


# *Call the hook in app*
Add this line on ```attachBaseContext``` or ```onCreate``` method in the class.I prefer adding at attachBaseContext

```bash
invoke-static {p1}, Lriyadmondol2006/SignFix;->attachBaseContext(Landroid/content/Context;)V
```

# We Do not promote any kind of illegal use of this hook,This hook is made for apk security testing. 
