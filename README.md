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
Mifare Ultralight tags are supported, since that is the kind of tags I think you should use. 
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

Need help?
===========
If you need professional help with an NFC project, get in touch. 
