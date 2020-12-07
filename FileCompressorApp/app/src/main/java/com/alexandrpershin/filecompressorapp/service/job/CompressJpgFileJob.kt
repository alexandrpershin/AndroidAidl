package com.alexandrpershin.filecompressorapp.service.job

import com.filecompressor.aidl.CompressFileInterface
import com.filecompressor.aidl.FileCompressorCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Concrete implementation of [CompressFileInterface]
 * Target - compression of JPG files
 * */

class CompressJpgFileJob(
    callback: FileCompressorCallback?,
    coroutineContext: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : BaseCompressJob(callback, coroutineContext) {

    override var extension: String = ".jpg"

    /**
     * Checks if [filePath] extension is valid.
     * Launches coroutine
     * Simulates file compressions (delay)
     * returns fake result to client with help of [FileCompressorCallback.onCompressFinished]
     * */

    override fun compressFile(filePath: String) {
        if (isFileExtensionValid(filePath)) {
            callback?.onLoading()

            job = coroutineContext.launch {
                delay(Random.nextLong(2000, 5000)) // Simulate work

                //TODO find the file by [filePath], compress and return the path of the new created file

                val fakeJpgFilePathResult = "/results/jpg/${filePath}"
                callback?.onCompressFinished(fakeJpgFilePathResult)
            }
        }
    }


}