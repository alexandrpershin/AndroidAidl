# About app
  
  This is client application for binding to remote service of another application with help of 
  Android Interface Definition Language (AIDL). Client application is using APIs of remote service
  for compression different types of files. <br/>
  Official Android documentation - https://developer.android.com/guide/components/aidl

## How app works?
  
  The client app (this app) binds to remote service using the app package name (com.alexandrpershin.filecompressorapp) and full name of remote
  service (com.alexandrpershin.filecompressorapp.service.FileCompressorService).
  <br/>
  Communication between client app and remote service is based on AIDL defined on both sides. It is
  important that AIDLs on client and remote sides are the same: have the same package names, types, signatures
  and etc. AIDL allows to client - add/remove callbacks to/from remote service. Also client is able 
  to call the methods of remote service. 
  
## Why it possible?
 
 Because the remote service defines implementation of AIDL interface on its side and returns it as Binder to client
 app. That's how client app calls the methods of remote service.
 
 ## Why AIDL?
 
 AIDL allows to define interface for interposes communication (IPC), doing the communication fast, robust, straightforward and secure. <br/>
 
 Thanks to AIDL:
 - Any application installed on the device are able to make use of the service;
 - Two different applications are  able to make use of the service at the same time;
 - The end client is able to kill the service at any time;
 - The service not expose any data which is being passed through it.
 
 ## Any disadvantages?
 
 Yes, concurrency. It's developer's responsibility to handle all concurrent requests coming to remote service
 from clients.  
  
  