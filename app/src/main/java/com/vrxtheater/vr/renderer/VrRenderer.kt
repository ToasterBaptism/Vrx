package com.vrxtheater.vr.renderer

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.Matrix
import android.view.Surface
import com.vrxtheater.data.models.VrSettings
import com.vrxtheater.vr.scene.TheaterScene
import com.vrxtheater.vr.util.SensorFusion
import com.vrxtheater.vr.util.TextureHelper
import org.rajawali3d.Object3D
import org.rajawali3d.cameras.Camera
import org.rajawali3d.lights.DirectionalLight
import org.rajawali3d.materials.Material
import org.rajawali3d.materials.textures.ATexture
import org.rajawali3d.materials.textures.StreamingTexture
import org.rajawali3d.math.Matrix4
import org.rajawali3d.math.Quaternion
import org.rajawali3d.math.vector.Vector3
import org.rajawali3d.renderer.Renderer
import org.rajawali3d.util.ObjectColorPicker
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Main VR renderer using Rajawali 3D engine
 */
class VrRenderer(
    context: Context,
    private val sensorFusion: SensorFusion,
    private val onScreenTextureReady: (Surface) -> Unit
) : Renderer(context) {
    
    // Scene objects
    private lateinit var theaterScene: TheaterScene
    private lateinit var leftEyeCamera: Camera
    private lateinit var rightEyeCamera: Camera
    private lateinit var screenObject: Object3D
    private lateinit var screenMaterial: Material
    private lateinit var screenTexture: StreamingTexture
    private lateinit var surfaceTexture: SurfaceTexture
    
    // Matrices
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val headRotation = Quaternion()
    
    // Settings
    private var vrSettings = VrSettings.getDefault()
    private var ipd = 0.063f // Interpupillary distance in meters (default 63mm)
    
    // Picking for gaze interaction
    private lateinit var objectPicker: ObjectColorPicker
    
    // Viewport dimensions
    private var viewportWidth = 0
    private var viewportHeight = 0
    
    // Flags
    private var isScreenTextureInitialized = false
    private var isRecenterRequested = false
    
    override fun initScene() {
        // Initialize scene
        theaterScene = TheaterScene(this, vrSettings)
        currentScene.addChild(theaterScene)
        
        // Add lighting
        val mainLight = DirectionalLight(0.1f, -1.0f, 0.2f)
        mainLight.power = 1.5f
        currentScene.addLight(mainLight)
        
        val fillLight = DirectionalLight(-0.1f, -0.1f, -0.1f)
        fillLight.power = 0.5f
        currentScene.addLight(fillLight)
        
        // Create stereo cameras
        setupStereoCameras()
        
        // Create screen texture for game rendering
        setupScreenTexture()
        
        // Initialize object picker for gaze interaction
        objectPicker = ObjectColorPicker(this)
        objectPicker.setOnObjectPickedListener { object3D ->
            // Handle gaze interaction
            if (object3D == screenObject) {
                // Screen was gazed at
            }
        }
        
        // Add screen to theater
        screenObject = theaterScene.getScreenObject()
        
        // Initial head position
        resetHeadPosition()
    }
    
    /**
     * Sets up the stereo cameras for VR rendering
     */
    private fun setupStereoCameras() {
        // Create left eye camera
        leftEyeCamera = Camera()
        leftEyeCamera.position = Vector3(-ipd / 2, 0f, 0f)
        leftEyeCamera.lookAt(Vector3(0.0, 0.0, -1.0))
        leftEyeCamera.fieldOfView = 60.0
        leftEyeCamera.nearPlane = 0.1
        leftEyeCamera.farPlane = 100.0
        
        // Create right eye camera
        rightEyeCamera = Camera()
        rightEyeCamera.position = Vector3(ipd / 2, 0f, 0f)
        rightEyeCamera.lookAt(Vector3(0.0, 0.0, -1.0))
        rightEyeCamera.fieldOfView = 60.0
        rightEyeCamera.nearPlane = 0.1
        rightEyeCamera.farPlane = 100.0
        
        // Set the default camera (will be overridden in render)
        currentScene.camera = leftEyeCamera
    }
    
    /**
     * Sets up the screen texture for game rendering
     */
    private fun setupScreenTexture() {
        // Create material for the screen
        screenMaterial = Material()
        screenMaterial.colorInfluence = 0f
        
        // Create streaming texture for the screen
        screenTexture = StreamingTexture("screenTexture")
        try {
            screenMaterial.addTexture(screenTexture)
        } catch (e: ATexture.TextureException) {
            e.printStackTrace()
        }
        
        // Create surface texture for the screen
        surfaceTexture = SurfaceTexture(TextureHelper.createTexture())
        surfaceTexture.setDefaultBufferSize(1920, 1080) // Default size, will be adjusted
        screenTexture.setSurfaceTexture(surfaceTexture)
    }
    
    override fun onRenderFrame(gl: GL10?) {
        // Update head rotation from sensors
        updateHeadRotation()
        
        // Initialize screen texture if not already done
        if (!isScreenTextureInitialized) {
            val surface = Surface(surfaceTexture)
            onScreenTextureReady(surface)
            isScreenTextureInitialized = true
        }
        
        // Update surface texture
        surfaceTexture.updateTexImage()
        
        // Render left eye
        renderEye(true)
        
        // Render right eye
        renderEye(false)
    }
    
    /**
     * Renders a single eye view
     */
    private fun renderEye(isLeftEye: Boolean) {
        // Set viewport for the eye
        val halfWidth = viewportWidth / 2
        val viewport = if (isLeftEye) {
            intArrayOf(0, 0, halfWidth, viewportHeight)
        } else {
            intArrayOf(halfWidth, 0, halfWidth, viewportHeight)
        }
        
        // Set the camera for the eye
        currentScene.camera = if (isLeftEye) leftEyeCamera else rightEyeCamera
        
        // Apply lens distortion if needed
        applyLensDistortion(isLeftEye)
        
        // Render the scene
        GLES20.glViewport(viewport[0], viewport[1], viewport[2], viewport[3])
        render(0.0)
    }
    
    /**
     * Applies lens distortion for the current eye
     */
    private fun applyLensDistortion(isLeftEye: Boolean) {
        // Apply barrel distortion based on settings
        val distortion = vrSettings.barrelDistortion
        
        // Apply lens offset
        val offsetX = vrSettings.lensOffsetX * (if (isLeftEye) -1 else 1)
        val offsetY = vrSettings.lensOffsetY
        
        // In a real implementation, this would modify the projection matrix
        // or use a post-processing shader for barrel distortion
    }
    
    /**
     * Updates the head rotation from sensor fusion
     */
    private fun updateHeadRotation() {
        // Get rotation from sensor fusion
        val rotation = sensorFusion.getRotation()
        
        // Apply smoothing based on settings
        val smoothing = vrSettings.trackingSmoothing
        
        // Apply comfort mode if enabled
        if (vrSettings.comfortMode) {
            // Reduce rotation speed for comfort
            // In a real implementation, this would limit rotation speed
        }
        
        // Apply rotation to cameras
        headRotation.setAll(rotation)
        
        // If recenter was requested, reset the head position
        if (isRecenterRequested) {
            resetHeadPosition()
            isRecenterRequested = false
        }
        
        // Apply rotation to both cameras
        val rotationMatrix = Matrix4().setAll(headRotation)
        leftEyeCamera.setRotation(rotationMatrix)
        rightEyeCamera.setRotation(rotationMatrix)
    }
    
    /**
     * Resets the head position to forward
     */
    fun resetHeadPosition() {
        sensorFusion.resetRotation()
    }
    
    /**
     * Updates the VR settings
     */
    fun updateSettings(settings: VrSettings) {
        this.vrSettings = settings
        
        // Update IPD
        this.ipd = settings.ipd / 1000f // Convert from mm to meters
        
        // Update camera positions
        leftEyeCamera.position = Vector3(-ipd / 2, 0f, 0f)
        rightEyeCamera.position = Vector3(ipd / 2, 0f, 0f)
        
        // Update theater scene
        theaterScene.updateSettings(settings)
    }
    
    /**
     * Requests a recenter of the view
     */
    fun recenterView() {
        isRecenterRequested = true
    }
    
    override fun onOffsetsChanged(
        xOffset: Float, yOffset: Float, xOffsetStep: Float, yOffsetStep: Float, xPixelOffset: Int, yPixelOffset: Int
    ) {
        // Not used
    }
    
    override fun onTouchEvent(event: MotionEvent?) {
        // Handle touch events for gaze selection
        event?.let {
            // Convert touch coordinates to scene coordinates for picking
            objectPicker.getObjectAt(event.x, event.y)
        }
    }
    
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        viewportWidth = width
        viewportHeight = height
        
        // Update surface texture size
        surfaceTexture.setDefaultBufferSize(width / 2, height)
    }
    
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)
        // Additional initialization if needed
    }
    
    /**
     * Cleans up resources
     */
    fun onDestroy() {
        if (::surfaceTexture.isInitialized) {
            surfaceTexture.release()
        }
    }
}