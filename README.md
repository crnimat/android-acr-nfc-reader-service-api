android-acr-nfc-reader-service-api
==================================
This project hosts the API for interacting with the [ACS NFC Reader Service](https://play.google.com/store/apps/details?id=com.skjolberg.acs) found at Google Play. 
It contains
 * Java API files for integration via broadcasts, and an
 * Android client which demonstrates actual usage

Supported readers
=================
Currently the readers
 * [ACR 122U](http://www.acs.com.hk/index.php?pid=product&id=ACR122U) 
 * [ACR 1222L](http://www.acs.com.hk/index.php?pid=product&id=ACR1222L)
 
are supported and must be connected to your Android device via an On-The-Go (OTG) USB cable.

Supported tag technology
========================
Mifare Ultralight and Mifare Classic tags are supported. NTAG203 also work. I recommend Mifare Ultralights or NTAG203. 
If you need some tags, get some at [RapidNFC](http://rapidnfc.com/r/1372).

API
===
The API defines actions and Intent extras for interaction with the service.
 * Service started/stopped 
 * ACR NFC Reader opened / closed - triggered by connects / disconnects via USB
 * NFC Tag scans 

Demo client
===========
The demo client keeps track of the service states and displays NDEF messages from tags scanned by the ACR NFC Reader.

Free features
===========
Detecting tag presence, type and id are free features.

Premium features
===========
Reading and writing NDEF messages are premium features. This API should speed up NFC development considerably, so consider going premium.

Troubleshooting
===========
Does the ACR reader not light up when connected to your device, even after the service asks for USB permissions? The ACR reader shuts down if there is not enough battery, so try charging your battery more. Please report any issues.

Need help?
===========
If you need professional help with an NFC project, get in touch. A more advanced API is available upon request.
