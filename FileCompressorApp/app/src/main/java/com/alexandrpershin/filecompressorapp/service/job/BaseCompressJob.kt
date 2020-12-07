package com.alexandrpershin.filecompressorapp.service.job

import com.filecompressor.aidl.CompressFileInterface
import com.filecompressor.aidl.FileCompressorCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

/**
 * Base class for job of file compression
 * Implements aidl interface [CompressFileInterface]
 * [callback] - is a callback for communication with client apps
* */

abstract class BaseCompressJob(
    protected var callback: FileCompressorCallback?,
    protected val coroutineContext: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : CompressFileInterface.Stub() {
    protected var job: Job? = null

    /**
     * Derived classes should override [extension] to define what kind of files to compress
    * */

    abstract var extension: String

    /**
     * Returns whether the work is in progress
    * */

    override fun isInProgress(): Boolean {
        return if (job == null) {
            false
        } else {
            job?.isActive!!
        }
    }

    /**
     * Validates the file extension of the file provided by client. If actual extension is wrong - notify user and return false
    * */

    protected fun isFileExtensionValid(filePath: String): Boolean {
        return if (!filePath.endsWith(extension)) {
            val actualExtension = filePath.substringAfterLast(".")
            callback?.onError("Client provided wrong file type. Was provided '.$actualExtension' instead of '$extension'")
            false
        } else {
            true
        }
    }

    /**
     * Stops the work of file compression
    * */

    override fun stop() {
        callback = null
        job?.cancel()
    }
}
