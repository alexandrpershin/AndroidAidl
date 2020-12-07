package com.alexandrpershin.filecompressorapp.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.alexandrpershin.filecompressorapp.service.job.CompressJpgFileJob
import com.alexandrpershin.filecompressorapp.service.job.CompressPdfFileJob
import com.alexandrpershin.filecompressorapp.service.job.CompressTextFileJob
import com.filecompressor.aidl.CompressFileInterface
import com.filecompressor.aidl.FileCompressorCallback
import com.filecompressor.aidl.FileCompressorServiceAidl
import java.util.concurrent.ConcurrentHashMap

class FileCompressorService : Service() {

    /**
     * [clientCallbacks] is a concurrentHashMap what stores all callbacks provided by caller apps
     *
     * [jobs] - jobs for compression of different types of files (pdf, text, jpg)
     * */

    val clientCallbacks = ConcurrentHashMap<Int, FileCompressorCallback?>()
    val jobs = ConcurrentHashMap<Int, CompressFileInterface?>()

    private val TAG = FileCompressorService::class.java.simpleName

    /**
     * Triggers when new apps bind to service
     **/

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind service")
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    /**
     * Returns client process uid
     * */

    private fun getCallerProcessUid() = Binder.getCallingUid()

    /**
     * Define implementation of [FileCompressorServiceAidl] interface for communication with client apps
     * */

    private val binder = object : FileCompressorServiceAidl.Stub() {

        /**
         * Called by client for compression of *.txt files
         **/

        override fun compressTextFile(filePath: String?) {
            val clientCallback = clientCallbacks[getCallerProcessUid()]

            if (filePath == null) {
                clientCallback?.onError("Provided filePath is shouldn't be a null")
                return
            }

            stopPreviousJobIfInProgress()
            startCompressTextFileJob(filePath, clientCallback!!)
        }

        /**
         * Called by client for compression of *.pdf files
         **/

        override fun compressPdfFile(filePath: String?) {
            val clientCallback = clientCallbacks[getCallerProcessUid()]

            if (filePath == null) {
                clientCallback?.onError("Provided filePath is shouldn't be a null")
                return
            }

            stopPreviousJobIfInProgress()
            startCompressPdfFileJob(filePath, clientCallback!!)
        }

        /**
         * Called by client for compression of *.jpg files
         **/

        override fun compressJpgFile(filePath: String?) {
            val clientCallback = clientCallbacks[getCallerProcessUid()]

            if (filePath == null) {
                clientCallback?.onError("Provided filePath is shouldn't be a null")
                return
            }

            stopPreviousJobIfInProgress()
            startCompressJpgFileJob(filePath, clientCallback!!)
        }

        /**
         * Called by the client app to unregister the callback and compression job
         * */

        override fun removeCallback(callback: FileCompressorCallback?) {
            jobs[getCallerProcessUid()]?.stop()
            jobs.remove(getCallerProcessUid())

            clientCallbacks.remove(getCallerProcessUid())
        }

        /**
         * Called by the client app to register the callback
         * */

        override fun addCallback(callback: FileCompressorCallback) {
            clientCallbacks[getCallerProcessUid()] = callback
        }

    }

    /**
     * Starts the job for compression TXT file
     * */

    private fun startCompressTextFileJob(filePath: String, clientCallback: FileCompressorCallback) {
        CompressTextFileJob(clientCallback).also {
            jobs[getCallerProcessUid()] = it
            it.compressFile(filePath)
        }
    }

    /**
     * Starts the job for compression PDF file
     * */

    private fun startCompressPdfFileJob(filePath: String, clientCallback: FileCompressorCallback) {
        CompressPdfFileJob(clientCallback).also {
            jobs[getCallerProcessUid()] = it
            it.compressFile(filePath)
        }
    }

    /**
     * Starts the job for compression PDF file
     * */

    private fun startCompressJpgFileJob(filePath: String, clientCallback: FileCompressorCallback) {
        CompressJpgFileJob(clientCallback).also {
            jobs[getCallerProcessUid()] = it
            it.compressFile(filePath)
        }
    }

    /**
     * Stops the job for current app if it is in progress
     * */

    private fun stopPreviousJobIfInProgress() {
        if (jobs[getCallerProcessUid()]?.isInProgress == true) jobs[getCallerProcessUid()]?.stop()
    }


}