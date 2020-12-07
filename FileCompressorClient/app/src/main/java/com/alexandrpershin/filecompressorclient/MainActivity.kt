package com.alexandrpershin.filecompressorclient

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.filecompressor.aidl.FileCompressorCallback
import com.filecompressor.aidl.FileCompressorServiceAidl


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = MainActivity::class.java.simpleName

    private lateinit var btnConnect: Button
    private lateinit var grServiceManipulation: Group
    private lateinit var progressBar: ProgressBar
    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        grServiceManipulation = findViewById(R.id.grServiceManipulation)
        progressBar = findViewById(R.id.progress)
        tvResult = findViewById(R.id.tvResult)

        initButtonClickListeners()
    }

    fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    private fun initButtonClickListeners() {
        btnConnect = findViewById<Button>(R.id.btnConnect)

        val btnDisconnect = findViewById<Button>(R.id.btnDisconnect)

        val btnCompressPdf = findViewById<Button>(R.id.btnCompressPdf)
        val btnCompressPhoto = findViewById<Button>(R.id.btnCompressJpg)
        val btnCompressTxt = findViewById<Button>(R.id.btnCompressTxt)

        btnConnect.setOnClickListener(this)
        btnDisconnect.setOnClickListener(this)
        btnCompressPdf.setOnClickListener(this)
        btnCompressPhoto.setOnClickListener(this)
        btnCompressTxt.setOnClickListener(this)
    }

    fun hideServiceConnectedUi() {
        grServiceManipulation.makeGone()
        btnConnect.makeVisible()
    }

    fun showServiceConnectedUi() {
        grServiceManipulation.makeVisible()
        btnConnect.makeGone()

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnConnect -> connectToService()
            R.id.btnDisconnect -> disconnectService()
            R.id.btnCompressPdf -> compressPdfFile()
            R.id.btnCompressJpg -> compressJpgFile()
            R.id.btnCompressTxt -> compressTxtFile()
        }
    }

    /**
     * Call [FileCompressorServiceAidl.compressTextFile]
     * */

    private fun compressTxtFile() {
        val wrongTxtExtension = ".stxts"
        service?.compressTextFile("src/test$wrongTxtExtension")
    }

    /**
     * Call [FileCompressorServiceAidl.compressPhotoFile]
     * */

    private fun compressJpgFile() {
        service?.compressJpgFile("src/test.jpg")
    }

    /**
     * Call [FileCompressorServiceAidl.compressPdfFile]
     * */

    private fun compressPdfFile() {
        service?.compressPdfFile("src/test.pdf")
    }

    /**
     * Disconnect from service using current [serviceConnection]
     * */

    private fun disconnectService() {
        try {
            service?.removeCallback(fileCompressCallback)
            hideServiceConnectedUi()
            unbindService(serviceConnection!!)
        } catch (e: Exception) {
            showToast("Disconnected")
        }
    }

    /**
     * Connect to remote service  using [serviceAppPackageName] and [serviceFullName]
     * */

    private fun connectToService() {
        val serviceAppPackageName = getString(R.string.service_app_package_name)
        val serviceFullName = getString(R.string.service_full_name)

        val intent = Intent().apply {
            component = ComponentName(serviceAppPackageName, serviceFullName)
        }
        val bound = applicationContext.bindService(
            intent,
            initNewServiceConnection(),
            Context.BIND_AUTO_CREATE
        )

        if (!bound) showToast("Couldn't find the service. Make sure the service host app is installed")
    }

    /**
     * Define service connection callback
     * */

    private fun initNewServiceConnection(): ServiceConnection {
        val newServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
                Log.d(TAG, "onServiceConnected")
                service = createService(binder)
                showToast("Service connected")
                showServiceConnectedUi()
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                Log.d(TAG, "onServiceDisconnected")
                service = null
                showToast("Service disconnected")
                hideServiceConnectedUi()
            }

        }.also {
            serviceConnection = it
        }

        return newServiceConnection
    }

    /**
     *  FileCompressorServiceAidl interface which allows client to interact with remote service
     * */

    private var service: FileCompressorServiceAidl? = null

    /**
     * [serviceConnection] allows client binds to remote service
     * */

    private var serviceConnection: ServiceConnection? = null

    /**
     * Cast FileCompressorServiceAidl.Stub interface implementation
     * */

    private fun createService(binder: IBinder?): FileCompressorServiceAidl? {
        val service = FileCompressorServiceAidl.Stub.asInterface(binder)
        service?.addCallback(fileCompressCallback)

        return service
    }

    /**
     * The callback which will be triggered by service
     * */

    private val fileCompressCallback = object : FileCompressorCallback.Stub() {
        val handler = Handler(Looper.getMainLooper())

        /**
         * Triggers once compress work successfully finished
         * */

        override fun onCompressFinished(newFilePath: String?) {
            handler.post {
                tvResult.text = "The compressed file path is: $newFilePath"
                progressBar.makeGone()
            }
        }

        /**
         * Triggers when compress work is in progress
         * */

        override fun onLoading() {
            handler.post {
                tvResult.text = ""
                progressBar.makeVisible()
            }
        }

        /**
         * Triggers if any errors occurred
         * */

        override fun onError(errorMessage: String?) {
            handler.post {
                progressBar.makeGone()
                showToast(errorMessage.toString())
            }
        }

    }

}

