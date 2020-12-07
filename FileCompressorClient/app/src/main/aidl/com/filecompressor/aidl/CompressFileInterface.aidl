// CompressFileInterface.aidl
package com.filecompressor.aidl;

// Declare any non-default types here with import statements

interface CompressFileInterface {

    void compressFile( String filePath);

    boolean isInProgress();

    void stop();

}