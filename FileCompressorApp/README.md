# About app
  
  This is host application of remote service which provides APIs for compression different types of files.<br/>
  Communication between client app and remote service is based on Android Interface Definition Language (AIDL).
  Official Android documentation - https://developer.android.com/guide/components/aidl

## How app works?
  
  The remote service allows clients to bind to itself using the app package name 
  (com.alexandrpershin.filecompressorapp) and full name of service (com.alexandrpershin.filecompressorapp.service.FileCompressorService).
  <br/>
  Communication between client app and remote service is based on AIDL defined on both sides. It is
  important that AIDLs on client and remote sides are the same: have the same package names, types, signatures
  and etc. AIDL allows to client - add/remove callbacks to/from remote service. Also client is able 
  to call the methods of remote service. 
  
## Why it possible?
 
 Because the remote service defines implementation of AIDL interface on its side and returns it as Binder to client
 app. That's how client app calls the methods of remote service.
 
 ## Whi AIDL?
 
 AIDL allows to define interface for interposes communication (IPC), doing the communication fast, robust, straightforward and secure. <br/>
 
 Thanks to AIDL:
 - Any application installed on the device are able to make use of the service;
 - Two different applications are  able to make use of the service at the same time;
 - The end client is able to kill the service at any time;
 - The service not expose any data which is being passed through it.
 
 ## Any disadvantages?
 
 Yes, concurrency. It's developer's responsibility to handle all concurrent requests coming to remote service
 from clients.  
 
 ## More about current app AIDLSs
 Android Interface Definition Language contracts are defined in [aidl package](./app/src/main/aidl/com/filecompressor/aidl).
 
 A bit more about them.
 
 1. [CompressFileInterface](./app/src/main/aidl/com/filecompressor/aidl/CompressFileInterface.aidl) - is base interface for compression of file.
 The concrete implementation of the file compression depends on file extension. <br/> 
 Example: [CompressJpgFileJob](./app/src/main/java/com/alexandrpershin/filecompressorapp/service/job/CompressJpgFileJob.kt), 
 [CompressPdfFileJob](./app/src/main/java/com/alexandrpershin/filecompressorapp/service/job/CompressPdfFileJob.kt),
 [CompressTextFileJob](./app/src/main/java/com/alexandrpershin/filecompressorapp/service/job/CompressTextFileJob.kt)
 2. [FileCompressorCallback](./app/src/main/aidl/com/filecompressor/aidl/FileCompressorCallback.aidl) - the interface which
  is responsible for communication back from remote service to client app. Is implemented by client and passed as callback to
  remote service<br/>
 3. [FileCompressorServiceAidl](./app/src/main/aidl/com/filecompressor/aidl/FileCompressorServiceAidl.aidl) - the interface which
   is responsible for communication between remote service to client app. Is implemented by remote service and returned as Binder.
    The client calls the methods of remote service via this interface<br/> 
  

 
 
  
  