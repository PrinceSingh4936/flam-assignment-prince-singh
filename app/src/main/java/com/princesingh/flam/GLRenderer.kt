package com.princesingh.flam

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.roundToInt
import kotlin.system.measureTimeMillis

class GLRenderer(private val fpsCallback: (Int) -> Unit) : GLSurfaceView.Renderer {

    @Volatile private var frameBuffer: ByteArray? = null
    @Volatile private var frameWidth = 0
    @Volatile private var frameHeight = 0

    private var textureId = -1
    private var startTime = System.currentTimeMillis()
    private var frameCount = 0

    // Simple quad
    private val vertexCoords = floatArrayOf(
        -1f, 1f,
        -1f, -1f,
        1f, 1f,
        1f, -1f
    )
    private val texCoords = floatArrayOf(
        0f, 0f,
        0f, 1f,
        1f, 0f,
        1f, 1f
    )
    private lateinit var vtxBuffer: FloatBuffer
    private lateinit var texBuffer: FloatBuffer

    init {
        vtxBuffer = ByteBuffer.allocateDirect(vertexCoords.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        vtxBuffer.put(vertexCoords).position(0)
        texBuffer = ByteBuffer.allocateDirect(texCoords.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        texBuffer.put(texCoords).position(0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        textureId = createTexture()
        GLES20.glClearColor(0f, 0f, 0f, 1f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        val fb = frameBuffer
        val w = frameWidth
        val h = frameHeight
        if (fb != null && w > 0 && h > 0) {
            // Upload texture data (RGBA)
            val bb = ByteBuffer.allocateDirect(fb.size)
            bb.put(fb).position(0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, w, h, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, bb)

            // Render textured quad with simple shaders (use GLUtils to help)
            // For brevity, use fixed pipeline like drawing with glDrawArrays and assume shaders are already set.
            // To keep code minimal, we will use GLES20 immediate shader creation
            drawTexture()
            frameCount++
            val now = System.currentTimeMillis()
            if (now - startTime >= 1000) {
                fpsCallback(frameCount)
                frameCount = 0
                startTime = now
            }
        }
    }

    fun updateFrame(rgba: ByteArray, width: Int, height: Int) {
        // Copy buffer atomically
        frameBuffer = rgba.copyOf()
        frameWidth = width
        frameHeight = height
    }

    private fun createTexture(): Int {
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        return textures[0]
    }

    private fun drawTexture() {
        // Simple shader program
        val vertexShaderCode = """
            attribute vec4 vPosition;
            attribute vec2 vTexCoord;
            varying vec2 texCoord;
            void main() {
              gl_Position = vPosition;
              texCoord = vTexCoord;
            }
        """.trimIndent()

        val fragmentShaderCode = """
            precision mediump float;
            varying vec2 texCoord;
            uniform sampler2D tex;
            void main() {
              gl_FragColor = texture2D(tex, texCoord);
            }
        """.trimIndent()

        val program = createProgram(vertexShaderCode, fragmentShaderCode)
        GLES20.glUseProgram(program)

        val posHandle = GLES20.glGetAttribLocation(program, "vPosition")
        val texHandle = GLES20.glGetAttribLocation(program, "vTexCoord")
        val samplerHandle = GLES20.glGetUniformLocation(program, "tex")

        GLES20.glEnableVertexAttribArray(posHandle)
        GLES20.glVertexAttribPointer(posHandle, 2, GLES20.GL_FLOAT, false, 0, vtxBuffer)

        GLES20.glEnableVertexAttribArray(texHandle)
        GLES20.glVertexAttribPointer(texHandle, 2, GLES20.GL_FLOAT, false, 0, texBuffer)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(samplerHandle, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(posHandle)
        GLES20.glDisableVertexAttribArray(texHandle)

        GLES20.glDeleteProgram(program)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }

    private fun createProgram(vs: String, fs: String): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vs)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fs)
        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        return program
    }
}
