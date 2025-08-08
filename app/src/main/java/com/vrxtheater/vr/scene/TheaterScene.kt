package com.vrxtheater.vr.scene

import android.graphics.Color
import com.vrxtheater.data.models.VrSettings
import org.rajawali3d.Object3D
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
    private var settings: VrSettings
) : Object3D() {
    
    // Scene objects
    private lateinit var floor: Object3D
    private lateinit var ceiling: Object3D
    private lateinit var leftWall: Object3D
    private lateinit var rightWall: Object3D
    private lateinit var backWall: Object3D
    private lateinit var screen: Object3D
    private lateinit var screenFrame: Object3D
    private lateinit var seats: List<Object3D>
    
    // Materials
    private lateinit var floorMaterial: Material
    private lateinit var wallMaterial: Material
    private lateinit var ceilingMaterial: Material
    private lateinit var screenMaterial: Material
    private lateinit var screenFrameMaterial: Material
    private lateinit var seatMaterial: Material
    
    init {
        // Initialize the theater scene
        createMaterials()
        createTheaterRoom()
        createScreen()
        createSeats()
        
        // Apply initial settings
        updateSettings(settings)
    }
    
    /**
     * Creates materials for the theater objects
     */
    private fun createMaterials() {
        // Floor material
        floorMaterial = Material()
        floorMaterial.colorInfluence = 0f
        floorMaterial.diffuseMethod = DiffuseMethod.Lambert()
        try {
            val floorTexture = Texture("floorTexture", R.drawable.theater_floor)
            floorMaterial.addTexture(floorTexture)
        } catch (e: ATexture.TextureException) {
            e.printStackTrace()
            floorMaterial.diffuseColor = Color.rgb(33, 33, 33)
        }
        
        // Wall material
        wallMaterial = Material()
        wallMaterial.colorInfluence = 0f
        wallMaterial.diffuseMethod = DiffuseMethod.Lambert()
        try {
            val wallTexture = Texture("wallTexture", R.drawable.theater_wall)
            wallMaterial.addTexture(wallTexture)
        } catch (e: ATexture.TextureException) {
            e.printStackTrace()
            wallMaterial.diffuseColor = Color.rgb(26, 26, 26)
        }
        
        // Ceiling material
        ceilingMaterial = Material()
        ceilingMaterial.colorInfluence = 0f
        ceilingMaterial.diffuseMethod = DiffuseMethod.Lambert()
        try {
            val ceilingTexture = Texture("ceilingTexture", R.drawable.theater_ceiling)
            ceilingMaterial.addTexture(ceilingTexture)
        } catch (e: ATexture.TextureException) {
            e.printStackTrace()
            ceilingMaterial.diffuseColor = Color.rgb(13, 13, 13)
        }
        
        // Screen frame material
        screenFrameMaterial = Material()
        screenFrameMaterial.diffuseMethod = DiffuseMethod.Lambert()
        screenFrameMaterial.diffuseColor = Color.rgb(48, 48, 48)
        
        // Seat material
        seatMaterial = Material()
        seatMaterial.diffuseMethod = DiffuseMethod.Lambert()
        seatMaterial.diffuseColor = Color.rgb(66, 66, 66)
    }
    
    /**
     * Creates the theater room (walls, floor, ceiling)
     */
    private fun createTheaterRoom() {
        // Room dimensions
        val roomWidth = 20f
        val roomHeight = 10f
        val roomDepth = 25f
        
        // Floor
        floor = Plane(roomWidth, roomDepth, 1, 1)
        floor.material = floorMaterial
        floor.rotation.x = -Math.PI.toFloat() / 2
        floor.position = Vector3(0f, -2f, 0f)
        addChild(floor)
        
        // Ceiling
        ceiling = Plane(roomWidth, roomDepth, 1, 1)
        ceiling.material = ceilingMaterial
        ceiling.rotation.x = Math.PI.toFloat() / 2
        ceiling.position = Vector3(0f, roomHeight - 2f, 0f)
        addChild(ceiling)
        
        // Left wall
        leftWall = Plane(roomDepth, roomHeight, 1, 1)
        leftWall.material = wallMaterial
        leftWall.rotation.y = Math.PI.toFloat() / 2
        leftWall.position = Vector3(-roomWidth / 2, roomHeight / 2 - 2f, 0f)
        addChild(leftWall)
        
        // Right wall
        rightWall = Plane(roomDepth, roomHeight, 1, 1)
        rightWall.material = wallMaterial
        rightWall.rotation.y = -Math.PI.toFloat() / 2
        rightWall.position = Vector3(roomWidth / 2, roomHeight / 2 - 2f, 0f)
        addChild(rightWall)
        
        // Back wall
        backWall = Plane(roomWidth, roomHeight, 1, 1)
        backWall.material = wallMaterial
        backWall.rotation.y = Math.PI.toFloat()
        backWall.position = Vector3(0f, roomHeight / 2 - 2f, roomDepth / 2)
        addChild(backWall)
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
     * Creates theater seats
     */
    private fun createSeats() {
        seats = mutableListOf()
        
        // Seat dimensions
        val seatWidth = 1.2f
        val seatHeight = 1.0f
        val seatDepth = 1.0f
        
        // Create rows of seats
        val rowCount = 3
        val seatsPerRow = 7
        val startZ = 3f
        val rowSpacing = 2f
        
        for (row in 0 until rowCount) {
            val z = startZ + row * rowSpacing
            val y = -1.5f + row * 0.5f // Each row is slightly higher
            
            for (col in 0 until seatsPerRow) {
                val x = (col - seatsPerRow / 2) * (seatWidth + 0.3f)
                
                // Create seat
                val seat = createSeat(seatWidth, seatHeight, seatDepth)
                seat.position = Vector3(x, y, z)
                
                // Add to scene and list
                addChild(seat)
                (seats as MutableList<Object3D>).add(seat)
            }
        }
    }
    
    /**
     * Creates a single theater seat
     */
    private fun createSeat(width: Float, height: Float, depth: Float): Object3D {
        val seat = Object3D()
        
        // Seat base
        val base = Cube(width, height * 0.3f, depth)
        base.material = seatMaterial
        base.position = Vector3(0f, 0f, 0f)
        seat.addChild(base)
        
        // Seat back
        val back = Cube(width, height * 0.7f, depth * 0.2f)
        back.material = seatMaterial
        back.position = Vector3(0f, height * 0.5f, depth * 0.4f)
        seat.addChild(back)
        
        // Seat arms
        val armWidth = width * 0.1f
        val armHeight = height * 0.3f
        val armDepth = depth * 0.8f
        
        // Left arm
        val leftArm = Cube(armWidth, armHeight, armDepth)
        leftArm.material = seatMaterial
        leftArm.position = Vector3(-width * 0.45f, height * 0.15f, 0f)
        seat.addChild(leftArm)
        
        // Right arm
        val rightArm = Cube(armWidth, armHeight, armDepth)
        rightArm.material = seatMaterial
        rightArm.position = Vector3(width * 0.45f, height * 0.15f, 0f)
        seat.addChild(rightArm)
        
        return seat
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
        // Adjust ambient light intensity
        renderer.currentScene.lights.forEach { light ->
            light.power = brightness * 2f
        }
    }
    
    /**
     * Returns the screen object for texture application
     */
    fun getScreenObject(): Object3D {
        return screen
    }
}