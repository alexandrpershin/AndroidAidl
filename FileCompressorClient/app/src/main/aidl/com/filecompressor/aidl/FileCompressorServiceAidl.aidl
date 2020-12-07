// FileCompressorServiceAidl.aidl
package com.filecompressor.aidl;


import com.filecompressor.aidl.FileCompressorCallback;


interface FileCompressorServiceAidl {

    void compressTextFile(String filePath);

    void compressPdfFile(String filePath);

    void compressJpgFile(String filePath);

    void addCallback( FileCompressorCallback callback);

    void removeCallback( FileCompressorCallback callback);

}