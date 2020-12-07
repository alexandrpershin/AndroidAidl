// FileCompressorCallback.aidl
package com.filecompressor.aidl;


interface FileCompressorCallback {

   oneway void onCompressFinished(in String newFilePath);

   void onLoading();

   void onError(String errorMessage);

}