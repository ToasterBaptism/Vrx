package com.vrxtheater.vr.util

import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLUtils
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.content.Context
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Helper class for texture operations
 */
object TextureHelper {
    
    /**
     * Creates an OpenGL texture
     */
    fun createTexture(): Int {
        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)
        
        if (textureIds[0] == 0) {
            throw RuntimeException("Failed to generate texture")
        }
        
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0])
        
        // Set texture parameters
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        
        return textureIds[0]
    }
    
    /**
     * Loads a texture from a resource
     */
    fun loadTexture(context: Context, resourceId: Int): Int {
        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)
        
        if (textureIds[0] == 0) {
            throw RuntimeException("Failed to generate texture")
        }
        
        // Load bitmap
        val options = BitmapFactory.Options()
        options.inScaled = false
        
        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)
        
        if (bitmap == null) {
            GLES20.glDeleteTextures(1, textureIds, 0)
            throw RuntimeException("Failed to load texture resource: $resourceId")
        }
        
        // Bind texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0])
        
        // Set texture parameters
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        
        // Load bitmap into texture
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        
        // Generate mipmaps
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)
        
        // Clean up
        bitmap.recycle()
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        
        return textureIds[0]
    }
    
    /**
     * Creates a framebuffer for rendering to texture
     */
    fun createFramebuffer(width: Int, height: Int): Pair<Int, Int> {
        // Generate framebuffer
        val framebuffers = IntArray(1)
        GLES20.glGenFramebuffers(1, framebuffers, 0)
        val framebufferId = framebuffers[0]
        
        // Generate texture
        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)
        val textureId = textureIds[0]
        
        // Bind texture and set parameters
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
            GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null
        )
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        
        // Generate renderbuffer for depth
        val renderbuffers = IntArray(1)
        GLES20.glGenRenderbuffers(1, renderbuffers, 0)
        val depthRenderbufferId = renderbuffers[0]
        
        // Bind renderbuffer and allocate storage
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRenderbufferId)
        GLES20.glRenderbufferStorage(
            GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16,
            width, height
        )
        
        // Bind framebuffer and attach texture and renderbuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebufferId)
        GLES20.glFramebufferTexture2D(
            GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
            GLES20.GL_TEXTURE_2D, textureId, 0
        )
        GLES20.glFramebufferRenderbuffer(
            GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
            GLES20.GL_RENDERBUFFER, depthRenderbufferId
        )
        
        // Check framebuffer status
        val status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            throw RuntimeException("Framebuffer not complete: $status")
        }
        
        // Unbind framebuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        
        return Pair(framebufferId, textureId)
    }
    
    /**
     * Creates a quad for rendering textures
     */
    fun createQuadVertices(): FloatBuffer {
        val vertices = floatArrayOf(
            // X, Y, Z, U, V
            -1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
            1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f, 1.0f
        )
        
        val buffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        
        buffer.put(vertices)
        buffer.position(0)
        
        return buffer
    }
    
    /**
     * Applies barrel distortion to a texture
     */
    fun applyBarrelDistortion(
        textureId: Int,
        width: Int,
        height: Int,
        distortion: Float
    ): Int {
        // Create shader program for barrel distortion
        val vertexShader = """
            attribute vec4 a_Position;
            attribute vec2 a_TexCoord;
            varying vec2 v_TexCoord;
            
            void main() {
                gl_Position = a_Position;
                v_TexCoord = a_TexCoord;
            }
        """.trimIndent()
        
        val fragmentShader = """
            precision mediump float;
            varying vec2 v_TexCoord;
            uniform sampler2D u_Texture;
            uniform float u_Distortion;
            
            void main() {
                vec2 texCoord = v_TexCoord;
                
                // Convert to polar coordinates
                vec2 normCoord = 2.0 * texCoord - 1.0;
                float r = length(normCoord);
                float theta = atan(normCoord.y, normCoord.x);
                
                // Apply barrel distortion
                float r2 = r * (1.0 + u_Distortion * r * r);
                
                // Convert back to Cartesian coordinates
                vec2 distortedCoord = 0.5 * (vec2(r2 * cos(theta), r2 * sin(theta)) + 1.0);
                
                // Sample the texture
                if (distortedCoord.x >= 0.0 && distortedCoord.x <= 1.0 &&
                    distortedCoord.y >= 0.0 && distortedCoord.y <= 1.0) {
                    gl_FragColor = texture2D(u_Texture, distortedCoord);
                } else {
                    gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
                }
            }
        """.trimIndent()
        
        // Create and link shader program
        val program = createProgram(vertexShader, fragmentShader)
        
        // Create framebuffer for output
        val (framebufferId, outputTextureId) = createFramebuffer(width, height)
        
        // Bind framebuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebufferId)
        GLES20.glViewport(0, 0, width, height)
        
        // Clear framebuffer
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        
        // Use shader program
        GLES20.glUseProgram(program)
        
        // Set up vertex attributes
        val quadVertices = createQuadVertices()
        
        val positionHandle = GLES20.glGetAttribLocation(program, "a_Position")
        GLES20.glEnableVertexAttribArray(positionHandle)
        quadVertices.position(0)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 20, quadVertices)
        
        val texCoordHandle = GLES20.glGetAttribLocation(program, "a_TexCoord")
        GLES20.glEnableVertexAttribArray(texCoordHandle)
        quadVertices.position(3)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 20, quadVertices)
        
        // Set uniforms
        val textureHandle = GLES20.glGetUniformLocation(program, "u_Texture")
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(textureHandle, 0)
        
        val distortionHandle = GLES20.glGetUniformLocation(program, "u_Distortion")
        GLES20.glUniform1f(distortionHandle, distortion)
        
        // Draw quad
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        
        // Clean up
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glUseProgram(0)
        
        // Unbind framebuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        
        // Delete framebuffer
        GLES20.glDeleteFramebuffers(1, intArrayOf(framebufferId), 0)
        
        return outputTextureId
    }
    
    /**
     * Creates and links a shader program
     */
    private fun createProgram(vertexShaderCode: String, fragmentShaderCode: String): Int {
        // Compile shaders
        val vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        
        // Create program
        val program = GLES20.glCreateProgram()
        
        if (program == 0) {
            throw RuntimeException("Failed to create program")
        }
        
        // Attach shaders
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        
        // Link program
        GLES20.glLinkProgram(program)
        
        // Check link status
        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
        
        if (linkStatus[0] == 0) {
            val log = GLES20.glGetProgramInfoLog(program)
            GLES20.glDeleteProgram(program)
            throw RuntimeException("Failed to link program: $log")
        }
        
        // Delete shaders
        GLES20.glDeleteShader(vertexShader)
        GLES20.glDeleteShader(fragmentShader)
        
        return program
    }
    
    /**
     * Compiles a shader
     */
    private fun compileShader(type: Int, shaderCode: String): Int {
        // Create shader
        val shader = GLES20.glCreateShader(type)
        
        if (shader == 0) {
            throw RuntimeException("Failed to create shader")
        }
        
        // Set shader source
        GLES20.glShaderSource(shader, shaderCode)
        
        // Compile shader
        GLES20.glCompileShader(shader)
        
        // Check compile status
        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        
        if (compileStatus[0] == 0) {
            val log = GLES20.glGetShaderInfoLog(shader)
            GLES20.glDeleteShader(shader)
            throw RuntimeException("Failed to compile shader: $log")
        }
        
        return shader
    }
}