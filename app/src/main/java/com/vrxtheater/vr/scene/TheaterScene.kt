package com.vrxtheater.vr.scene

import android.content.Context
import android.graphics.Color
import com.vrxtheater.data.models.VrSettings
import com.vrxtheater.vr.util.TextureHelper
import org.rajawali3d.Object3D
import org.rajawali3d.lights.DirectionalLight
import org.rajawali3d.lights.PointLight
import org.rajawali3d.materials.Material
import org.rajawali3d.materials.methods.DiffuseMethod
import org.rajawali3d.materials.textures.ATexture
import org.rajawali3d.materials.textures.Texture
import org.rajawali3d.math.vector.Vector3
import org.rajawali3d.primitives.Cube
import org.rajawali3d.primitives.Plane
import org.rajawali3d.primitives.Sphere
import org.rajawali3d.renderer.Renderer

/**
 * 3D scene representing the virtual theater
 */
class TheaterScene(
    private val renderer: Renderer,
    private val context: Context,
    private var settings: VrSettings
) : Object3D() {
    
    // Scene objects
    private lateinit var screen: Object3D
    private lateinit var screenFrame: Object3D
    
    // Environment
    private val theaterEnvironment: TheaterEnvironment
    private var currentEnvironmentType = TheaterEnvironmentType.CLASSIC_THEATER
    private var environmentObjects = mutableListOf<Object3D>()
    
    // Texture helper
    private val textureHelper = TextureHelper(context)
    
    // Screen material
    private lateinit var screenMaterial: Material
    private lateinit var screenFrameMaterial: Material
    
    init {
        // Initialize the theater environment
        theaterEnvironment = TheaterEnvironment(context, textureHelper)
        
        // Create screen and frame
        createScreenMaterials()
        createScreen()
        
        // Create initial environment
        createEnvironment(TheaterEnvironmentType.CLASSIC_THEATER)
        
        // Apply initial settings
        updateSettings(settings)
    }
    
    /**
     * Creates materials for the screen
     */
    private fun createScreenMaterials() {
        // Screen frame material
        screenFrameMaterial = Material()
        screenFrameMaterial.diffuseMethod = DiffuseMethod.Lambert()
        screenFrameMaterial.diffuseColor = Color.rgb(48, 48, 48)
        
        // Screen material will be set by the renderer
        screenMaterial = Material()
    }
    
    /**
     * Creates the theater screen
     */
    private fun createScreen() {
        // Screen dimensions (will be adjusted by settings)
        val screenWidth = 16f
        val screenHeight = 9f
        val screenDistance = 5f
        
        // Create screen
        screen = Plane(screenWidth, screenHeight, 1, 1)
        screen.position = Vector3(0f, screenHeight / 2, -screenDistance)
        
        // Create screen frame
        val frameThickness = 0.2f
        val frameDepth = 0.1f
        
        // Top frame
        val topFrame = Cube(screenWidth + frameThickness * 2, frameThickness, frameDepth)
        topFrame.material = screenFrameMaterial
        topFrame.position = Vector3(0f, screenHeight + frameThickness / 2, -screenDistance)
        
        // Bottom frame
        val bottomFrame = Cube(screenWidth + frameThickness * 2, frameThickness, frameDepth)
        bottomFrame.material = screenFrameMaterial
        bottomFrame.position = Vector3(0f, -frameThickness / 2, -screenDistance)
        
        // Left frame
        val leftFrame = Cube(frameThickness, screenHeight + frameThickness * 2, frameDepth)
        leftFrame.material = screenFrameMaterial
        leftFrame.position = Vector3(-screenWidth / 2 - frameThickness / 2, screenHeight / 2, -screenDistance)
        
        // Right frame
        val rightFrame = Cube(frameThickness, screenHeight + frameThickness * 2, frameDepth)
        rightFrame.material = screenFrameMaterial
        rightFrame.position = Vector3(screenWidth / 2 + frameThickness / 2, screenHeight / 2, -screenDistance)
        
        // Combine frames into a single object
        screenFrame = Object3D()
        screenFrame.addChild(topFrame)
        screenFrame.addChild(bottomFrame)
        screenFrame.addChild(leftFrame)
        screenFrame.addChild(rightFrame)
        
        // Add screen and frame to scene
        addChild(screen)
        addChild(screenFrame)
    }
    
    /**
     * Creates the theater environment
     */
    private fun createEnvironment(type: TheaterEnvironmentType) {
        // Remove previous environment objects
        environmentObjects.forEach { removeChild(it) }
        environmentObjects.clear()
        
        // Remove previous lights
        renderer.currentScene.lights.clear()
        
        // Create new environment
        environmentObjects = theaterEnvironment.createEnvironment(type).toMutableList()
        
        // Add environment objects to scene
        environmentObjects.forEach { addChild(it) }
        
        // Add lights to scene
        theaterEnvironment.getLights().forEach { renderer.currentScene.addLight(it) }
        
        // Update current environment type
        currentEnvironmentType = type
    }
    
    /**
     * Updates the scene based on VR settings
     */
    fun updateSettings(settings: VrSettings) {
        this.settings = settings
        
        // Update screen position and size
        val screenWidth = settings.screenSize
        val screenHeight = screenWidth * 9f / 16f // 16:9 aspect ratio
        val screenDistance = settings.screenDistance
        val screenTilt = settings.screenTilt
        
        // Apply screen settings
        screen.scale = Vector3(screenWidth / 16f, screenHeight / 9f, 1f)
        screen.position = Vector3(0f, screenHeight / 2, -screenDistance)
        screen.rotation.x = screenTilt
        
        // Apply screen curvature if needed
        applyCurvature(settings.screenCurvature)
        
        // Update screen frame position
        screenFrame.position = screen.position
        screenFrame.rotation = screen.rotation
        screenFrame.scale = Vector3(screenWidth / 16f, screenHeight / 9f, 1f)
        
        // Update environment brightness
        updateEnvironmentBrightness(settings.environmentBrightness)
    }
    
    /**
     * Changes the theater environment
     */
    fun changeEnvironment(type: TheaterEnvironmentType) {
        if (type != currentEnvironmentType) {
            createEnvironment(type)
            updateSettings(settings) // Apply current settings to new environment
        }
    }
    
    /**
     * Applies curvature to the screen
     */
    private fun applyCurvature(curvature: Float) {
        // In a real implementation, this would modify the screen geometry
        // or use a curved primitive instead of a flat plane
        
        // For simplicity, we're just adjusting the screen here
        if (curvature > 0) {
            // Apply some visual effect to simulate curvature
            // This is a placeholder - real implementation would be more complex
        }
    }
    
    /**
     * Updates the environment brightness
     */
    private fun updateEnvironmentBrightness(brightness: Float) {
        // Adjust light intensity
        renderer.currentScene.lights.forEach { light ->
            light.power = light.power * brightness
        }
    }
    
    /**
     * Returns the screen object for texture application
     */
    fun getScreenObject(): Object3D {
        return screen
    }
    
    /**
     * Returns the current environment type
     */
    fun getCurrentEnvironmentType(): TheaterEnvironmentType {
        return currentEnvironmentType
    }
    
    /**
     * Returns all available environment types
     */
    fun getAvailableEnvironments(): List<TheaterEnvironmentType> {
        return TheaterEnvironmentType.values().toList()
    }
}