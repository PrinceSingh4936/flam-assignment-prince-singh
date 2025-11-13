package com.princesingh.flam

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.TextureView
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.opengl.GLSurfaceView

class MainActivity : AppCompatActivity() {

    private lateinit var textureView: TextureView
    private lateinit var glSurface: GLSurfaceView
    private lateinit var btnToggle: Button
    private lateinit var textFps: TextView

    private lateinit var cameraController: Camera2Controller
    private lateinit var renderer: GLRenderer

    private var showProcessed = true

    companion object {
        init { System.loadLibrary("native-lib") } // native library name
        const val REQUEST_CAMERA = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textureView = findViewById(R.id.texture_view)
        glSurface = findViewById(R.id.gl_surface)
        btnToggle = findViewById(R.id.btn_toggle)
        textFps = findViewById(R.id.text_fps)

        // Setup GLSurfaceView and renderer
        glSurface.setEGLContextClientVersion(2)
        renderer = GLRenderer { fps ->
            runOnUiThread { textFps.text = "FPS: $fps" }
        }
        glSurface.setRenderer(renderer)
        glSurface.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

        cameraController = Camera2Controller(this) { nv21, w, h ->
            // Called on background thread when a frame is available
            // Pass to native processor and render result
            if (showProcessed) {
                val rgba = processFrameNV21(nv21, w, h) // native call returns RGBA byte array
                if (rgba != null) {
                    renderer.updateFrame(rgba, w, h)
                }
            } else {
                // When showing raw camera, we can let TextureView show preview directly (cameraController handles preview)
            }
        }

        btnToggle.setOnClickListener {
            showProcessed = !showProcessed
            // Show or hide GLSurfaceView to let raw preview be visible
            glSurface.visibility = if (showProcessed) View.VISIBLE else View.GONE
        }

        checkPermissionAndStart()
    }

    private fun checkPermissionAndStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
        } else {
            startCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                finish()
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun startCamera() {
        // Start camera preview into TextureView and start frame streaming to callback
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                cameraController.startCameraPreview(surface, textureView.width, textureView.height)
                cameraController.startFrameStream() // frames sent to callback
            }
            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture) = true
            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
        }
    }

    // Native method: takes NV21 bytes, width, height -> returns RGBA byte[] or null
    external fun processFrameNV21(nv21: ByteArray, width: Int, height: Int): ByteArray?
}
