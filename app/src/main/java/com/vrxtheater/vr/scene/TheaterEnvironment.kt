package com.vrxtheater.vr.scene

import android.content.Context
import android.graphics.Color
import org.rajawali3d.Object3D
import org.rajawali3d.lights.DirectionalLight
import org.rajawali3d.lights.PointLight
import org.rajawali3d.materials.Material
import org.rajawali3d.materials.methods.DiffuseMethod
import org.rajawali3d.materials.textures.ATexture
import org.rajawali3d.materials.textures.Texture
import org.rajawali3d.math.vector.Vector3
import org.rajawali3d.primitives.Plane
import org.rajawali3d.primitives.Sphere
import com.vrxtheater.vr.util.TextureHelper

/**
 * Represents different theater environment types
 */
enum class TheaterEnvironmentType {
    CLASSIC_THEATER,
    MODERN_THEATER,
    IMAX_DOME,
    OUTDOOR_CINEMA,
    SPACE_STATION,
    UNDERWATER
}

/**
 * Class responsible for creating and managing theater environments
 */
class TheaterEnvironment(
    private val context: Context,
    private val textureHelper: TextureHelper
) {
    // Environment objects
    private val environmentObjects = mutableListOf<Object3D>()
    
    // Lights
    private val lights = mutableListOf<org.rajawali3d.lights.ALight>()
    
    // Current environment type
    private var currentEnvironmentType = TheaterEnvironmentType.CLASSIC_THEATER
    
    /**
     * Creates a theater environment based on the specified type
     */
    fun createEnvironment(type: TheaterEnvironmentType): List<Object3D> {
        // Clear previous environment
        environmentObjects.clear()
        lights.clear()
        
        // Create new environment
        when (type) {
            TheaterEnvironmentType.CLASSIC_THEATER -> createClassicTheater()
            TheaterEnvironmentType.MODERN_THEATER -> createModernTheater()
            TheaterEnvironmentType.IMAX_DOME -> createImaxDome()
            TheaterEnvironmentType.OUTDOOR_CINEMA -> createOutdoorCinema()
            TheaterEnvironmentType.SPACE_STATION -> createSpaceStation()
            TheaterEnvironmentType.UNDERWATER -> createUnderwaterEnvironment()
        }
        
        // Update current environment type
        currentEnvironmentType = type
        
        return environmentObjects
    }
    
    /**
     * Returns the lights for the current environment
     */
    fun getLights(): List<org.rajawali3d.lights.ALight> {
        return lights
    }
    
    /**
     * Creates a classic theater environment
     */
    private fun createClassicTheater() {
        // Floor
        val floorTexture = textureHelper.loadTexture("textures/theater_carpet.jpg")
        val floor = Plane(20f, 20f, 1, 1)
        floor.setDoubleSided(true)
        floor.rotation.x = Math.toRadians(-90.0).toFloat()
        floor.position = Vector3(0.0, -2.0, 0.0)
        
        val floorMaterial = Material()
        floorMaterial.colorInfluence = 0f
        floorMaterial.diffuseMethod = DiffuseMethod.Lambert()
        floorMaterial.addTexture(floorTexture)
        floor.material = floorMaterial
        
        environmentObjects.add(floor)
        
        // Walls
        val wallTexture = textureHelper.loadTexture("textures/theater_wall.jpg")
        
        // Back wall
        val backWall = Plane(20f, 10f, 1, 1)
        backWall.rotation.y = Math.toRadians(180.0).toFloat()
        backWall.position = Vector3(0.0, 3.0, -10.0)
        
        val wallMaterial = Material()
        wallMaterial.colorInfluence = 0f
        wallMaterial.diffuseMethod = DiffuseMethod.Lambert()
        wallMaterial.addTexture(wallTexture)
        backWall.material = wallMaterial
        
        environmentObjects.add(backWall)
        
        // Left wall
        val leftWall = Plane(20f, 10f, 1, 1)
        leftWall.rotation.y = Math.toRadians(90.0).toFloat()
        leftWall.position = Vector3(-10.0, 3.0, 0.0)
        leftWall.material = wallMaterial
        
        environmentObjects.add(leftWall)
        
        // Right wall
        val rightWall = Plane(20f, 10f, 1, 1)
        rightWall.rotation.y = Math.toRadians(-90.0).toFloat()
        rightWall.position = Vector3(10.0, 3.0, 0.0)
        rightWall.material = wallMaterial
        
        environmentObjects.add(rightWall)
        
        // Ceiling
        val ceiling = Plane(20f, 20f, 1, 1)
        ceiling.setDoubleSided(true)
        ceiling.rotation.x = Math.toRadians(90.0).toFloat()
        ceiling.position = Vector3(0.0, 8.0, 0.0)
        
        val ceilingMaterial = Material()
        ceilingMaterial.colorInfluence = 0.3f
        ceilingMaterial.diffuseMethod = DiffuseMethod.Lambert()
        ceilingMaterial.setColor(Color.rgb(30, 30, 30))
        ceiling.material = ceilingMaterial
        
        environmentObjects.add(ceiling)
        
        // Theater seats (simplified as rows of boxes)
        val seatTexture = textureHelper.loadTexture("textures/theater_seat.jpg")
        val seatMaterial = Material()
        seatMaterial.colorInfluence = 0f
        seatMaterial.diffuseMethod = DiffuseMethod.Lambert()
        seatMaterial.addTexture(seatTexture)
        
        // Add rows of seats
        for (row in 0 until 5) {
            for (col in -5 until 6) {
                val seat = Plane(0.8f, 0.8f, 1, 1)
                seat.rotation.x = Math.toRadians(-90.0).toFloat()
                seat.position = Vector3(
                    col * 1.5,
                    -1.5,
                    -5.0 - row * 1.5
                )
                seat.material = seatMaterial
                environmentObjects.add(seat)
            }
        }
        
        // Add lights
        val mainLight = DirectionalLight(0f, -1f, -1f)
        mainLight.power = 1.0f
        mainLight.color = Color.rgb(255, 255, 255)
        lights.add(mainLight)
        
        // Ambient lights on the sides
        val leftLight = PointLight()
        leftLight.position = Vector3(-8.0, 5.0, -5.0)
        leftLight.power = 0.5f
        leftLight.color = Color.rgb(255, 223, 170)
        lights.add(leftLight)
        
        val rightLight = PointLight()
        rightLight.position = Vector3(8.0, 5.0, -5.0)
        rightLight.power = 0.5f
        rightLight.color = Color.rgb(255, 223, 170)
        lights.add(rightLight)
    }
    
    /**
     * Creates a modern theater environment
     */
    private fun createModernTheater() {
        // Floor
        val floorTexture = textureHelper.loadTexture("textures/modern_floor.jpg")
        val floor = Plane(20f, 20f, 1, 1)
        floor.setDoubleSided(true)
        floor.rotation.x = Math.toRadians(-90.0).toFloat()
        floor.position = Vector3(0.0, -2.0, 0.0)
        
        val floorMaterial = Material()
        floorMaterial.colorInfluence = 0f
        floorMaterial.diffuseMethod = DiffuseMethod.Lambert()
        floorMaterial.addTexture(floorTexture)
        floor.material = floorMaterial
        
        environmentObjects.add(floor)
        
        // Walls
        val wallTexture = textureHelper.loadTexture("textures/modern_wall.jpg")
        
        // Back wall
        val backWall = Plane(20f, 10f, 1, 1)
        backWall.rotation.y = Math.toRadians(180.0).toFloat()
        backWall.position = Vector3(0.0, 3.0, -10.0)
        
        val wallMaterial = Material()
        wallMaterial.colorInfluence = 0f
        wallMaterial.diffuseMethod = DiffuseMethod.Lambert()
        wallMaterial.addTexture(wallTexture)
        backWall.material = wallMaterial
        
        environmentObjects.add(backWall)
        
        // Left wall
        val leftWall = Plane(20f, 10f, 1, 1)
        leftWall.rotation.y = Math.toRadians(90.0).toFloat()
        leftWall.position = Vector3(-10.0, 3.0, 0.0)
        leftWall.material = wallMaterial
        
        environmentObjects.add(leftWall)
        
        // Right wall
        val rightWall = Plane(20f, 10f, 1, 1)
        rightWall.rotation.y = Math.toRadians(-90.0).toFloat()
        rightWall.position = Vector3(10.0, 3.0, 0.0)
        rightWall.material = wallMaterial
        
        environmentObjects.add(rightWall)
        
        // Ceiling
        val ceiling = Plane(20f, 20f, 1, 1)
        ceiling.setDoubleSided(true)
        ceiling.rotation.x = Math.toRadians(90.0).toFloat()
        ceiling.position = Vector3(0.0, 8.0, 0.0)
        
        val ceilingMaterial = Material()
        ceilingMaterial.colorInfluence = 0.3f
        ceilingMaterial.diffuseMethod = DiffuseMethod.Lambert()
        ceilingMaterial.setColor(Color.rgb(20, 20, 20))
        ceiling.material = ceilingMaterial
        
        environmentObjects.add(ceiling)
        
        // Modern theater seats
        val seatTexture = textureHelper.loadTexture("textures/modern_seat.jpg")
        val seatMaterial = Material()
        seatMaterial.colorInfluence = 0f
        seatMaterial.diffuseMethod = DiffuseMethod.Lambert()
        seatMaterial.addTexture(seatTexture)
        
        // Add rows of seats
        for (row in 0 until 4) {
            for (col in -4 until 5) {
                val seat = Plane(1.0f, 1.0f, 1, 1)
                seat.rotation.x = Math.toRadians(-90.0).toFloat()
                seat.position = Vector3(
                    col * 2.0,
                    -1.5,
                    -5.0 - row * 2.0
                )
                seat.material = seatMaterial
                environmentObjects.add(seat)
            }
        }
        
        // Add lights
        val mainLight = DirectionalLight(0f, -1f, -1f)
        mainLight.power = 0.8f
        mainLight.color = Color.rgb(255, 255, 255)
        lights.add(mainLight)
        
        // Modern LED accent lights
        val blueLight = PointLight()
        blueLight.position = Vector3(-8.0, 5.0, -5.0)
        blueLight.power = 0.3f
        blueLight.color = Color.rgb(100, 150, 255)
        lights.add(blueLight)
        
        val redLight = PointLight()
        redLight.position = Vector3(8.0, 5.0, -5.0)
        redLight.power = 0.3f
        redLight.color = Color.rgb(255, 100, 100)
        lights.add(redLight)
    }
    
    /**
     * Creates an IMAX dome environment
     */
    private fun createImaxDome() {
        // Floor
        val floorTexture = textureHelper.loadTexture("textures/imax_floor.jpg")
        val floor = Plane(20f, 20f, 1, 1)
        floor.setDoubleSided(true)
        floor.rotation.x = Math.toRadians(-90.0).toFloat()
        floor.position = Vector3(0.0, -2.0, 0.0)
        
        val floorMaterial = Material()
        floorMaterial.colorInfluence = 0f
        floorMaterial.diffuseMethod = DiffuseMethod.Lambert()
        floorMaterial.addTexture(floorTexture)
        floor.material = floorMaterial
        
        environmentObjects.add(floor)
        
        // Dome
        val domeTexture = textureHelper.loadTexture("textures/imax_dome.jpg")
        val dome = Sphere(15f, 32, 32)
        dome.setDoubleSided(true)
        dome.position = Vector3(0.0, 10.0, 0.0)
        
        val domeMaterial = Material()
        domeMaterial.colorInfluence = 0f
        domeMaterial.diffuseMethod = DiffuseMethod.Lambert()
        domeMaterial.addTexture(domeTexture)
        dome.material = domeMaterial
        
        environmentObjects.add(dome)
        
        // IMAX seats
        val seatTexture = textureHelper.loadTexture("textures/imax_seat.jpg")
        val seatMaterial = Material()
        seatMaterial.colorInfluence = 0f
        seatMaterial.diffuseMethod = DiffuseMethod.Lambert()
        seatMaterial.addTexture(seatTexture)
        
        // Add rows of seats in a curved arrangement
        for (row in 0 until 5) {
            for (angle in -60..60 step 15) {
                val radians = Math.toRadians(angle.toDouble())
                val radius = 8.0 + row * 1.5
                
                val seat = Plane(1.0f, 1.0f, 1, 1)
                seat.rotation.x = Math.toRadians(-90.0).toFloat()
                seat.rotation.y = radians.toFloat()
                seat.position = Vector3(
                    Math.sin(radians) * radius,
                    -1.5 + row * 0.5,
                    Math.cos(radians) * radius - 5.0
                )
                seat.material = seatMaterial
                environmentObjects.add(seat)
            }
        }
        
        // Add lights
        val mainLight = DirectionalLight(0f, -1f, 0f)
        mainLight.power = 0.5f
        mainLight.color = Color.rgb(255, 255, 255)
        lights.add(mainLight)
        
        // IMAX ambient lighting
        val ambientLight1 = PointLight()
        ambientLight1.position = Vector3(0.0, 5.0, -10.0)
        ambientLight1.power = 0.3f
        ambientLight1.color = Color.rgb(100, 100, 255)
        lights.add(ambientLight1)
        
        val ambientLight2 = PointLight()
        ambientLight2.position = Vector3(0.0, 5.0, 5.0)
        ambientLight2.power = 0.3f
        ambientLight2.color = Color.rgb(255, 255, 255)
        lights.add(ambientLight2)
    }
    
    /**
     * Creates an outdoor cinema environment
     */
    private fun createOutdoorCinema() {
        // Ground
        val groundTexture = textureHelper.loadTexture("textures/grass.jpg")
        val ground = Plane(50f, 50f, 1, 1)
        ground.setDoubleSided(true)
        ground.rotation.x = Math.toRadians(-90.0).toFloat()
        ground.position = Vector3(0.0, -2.0, 0.0)
        
        val groundMaterial = Material()
        groundMaterial.colorInfluence = 0f
        groundMaterial.diffuseMethod = DiffuseMethod.Lambert()
        groundMaterial.addTexture(groundTexture)
        ground.material = groundMaterial
        
        environmentObjects.add(ground)
        
        // Sky dome
        val skyTexture = textureHelper.loadTexture("textures/night_sky.jpg")
        val sky = Sphere(30f, 32, 32)
        sky.setDoubleSided(true)
        sky.position = Vector3(0.0, 15.0, 0.0)
        
        val skyMaterial = Material()
        skyMaterial.colorInfluence = 0f
        skyMaterial.diffuseMethod = DiffuseMethod.Lambert()
        skyMaterial.addTexture(skyTexture)
        sky.material = skyMaterial
        
        environmentObjects.add(sky)
        
        // Outdoor seating (blankets on the ground)
        val blanketTexture = textureHelper.loadTexture("textures/blanket.jpg")
        val blanketMaterial = Material()
        blanketMaterial.colorInfluence = 0f
        blanketMaterial.diffuseMethod = DiffuseMethod.Lambert()
        blanketMaterial.addTexture(blanketTexture)
        
        // Add blankets in a scattered arrangement
        for (row in 0 until 4) {
            for (col in -3 until 4) {
                val blanket = Plane(2.0f, 2.0f, 1, 1)
                blanket.rotation.x = Math.toRadians(-90.0).toFloat()
                blanket.position = Vector3(
                    col * 3.0 + (row % 2) * 1.5,
                    -1.9,
                    -5.0 - row * 3.0
                )
                blanket.material = blanketMaterial
                environmentObjects.add(blanket)
            }
        }
        
        // Add some trees
        val treeTexture = textureHelper.loadTexture("textures/tree.jpg")
        val treeMaterial = Material()
        treeMaterial.colorInfluence = 0f
        treeMaterial.diffuseMethod = DiffuseMethod.Lambert()
        treeMaterial.addTexture(treeTexture)
        
        // Add trees around the perimeter
        for (angle in 0 until 360 step 45) {
            val radians = Math.toRadians(angle.toDouble())
            val radius = 20.0
            
            val tree = Plane(5.0f, 10.0f, 1, 1)
            tree.rotation.y = (radians + Math.PI).toFloat()
            tree.position = Vector3(
                Math.sin(radians) * radius,
                3.0,
                Math.cos(radians) * radius
            )
            tree.material = treeMaterial
            environmentObjects.add(tree)
        }
        
        // Add lights
        // Moonlight
        val moonLight = DirectionalLight(0.2f, -1f, 0.5f)
        moonLight.power = 0.3f
        moonLight.color = Color.rgb(200, 220, 255)
        lights.add(moonLight)
        
        // Lantern lights
        val lanternColors = arrayOf(
            Color.rgb(255, 200, 100),  // Warm yellow
            Color.rgb(255, 150, 100),  // Orange
            Color.rgb(200, 255, 100)   // Yellow-green
        )
        
        for (i in 0 until 6) {
            val angle = Math.toRadians(i * 60.0)
            val radius = 12.0
            
            val lantern = PointLight()
            lantern.position = Vector3(
                Math.sin(angle) * radius,
                0.5,
                Math.cos(angle) * radius
            )
            lantern.power = 0.4f
            lantern.color = lanternColors[i % lanternColors.size]
            lights.add(lantern)
        }
    }
    
    /**
     * Creates a space station environment
     */
    private fun createSpaceStation() {
        // Floor
        val floorTexture = textureHelper.loadTexture("textures/metal_floor.jpg")
        val floor = Plane(20f, 20f, 1, 1)
        floor.setDoubleSided(true)
        floor.rotation.x = Math.toRadians(-90.0).toFloat()
        floor.position = Vector3(0.0, -2.0, 0.0)
        
        val floorMaterial = Material()
        floorMaterial.colorInfluence = 0f
        floorMaterial.diffuseMethod = DiffuseMethod.Lambert()
        floorMaterial.addTexture(floorTexture)
        floor.material = floorMaterial
        
        environmentObjects.add(floor)
        
        // Walls
        val wallTexture = textureHelper.loadTexture("textures/space_station_wall.jpg")
        
        // Back wall
        val backWall = Plane(20f, 10f, 1, 1)
        backWall.rotation.y = Math.toRadians(180.0).toFloat()
        backWall.position = Vector3(0.0, 3.0, -10.0)
        
        val wallMaterial = Material()
        wallMaterial.colorInfluence = 0f
        wallMaterial.diffuseMethod = DiffuseMethod.Lambert()
        wallMaterial.addTexture(wallTexture)
        backWall.material = wallMaterial
        
        environmentObjects.add(backWall)
        
        // Left wall
        val leftWall = Plane(20f, 10f, 1, 1)
        leftWall.rotation.y = Math.toRadians(90.0).toFloat()
        leftWall.position = Vector3(-10.0, 3.0, 0.0)
        leftWall.material = wallMaterial
        
        environmentObjects.add(leftWall)
        
        // Right wall
        val rightWall = Plane(20f, 10f, 1, 1)
        rightWall.rotation.y = Math.toRadians(-90.0).toFloat()
        rightWall.position = Vector3(10.0, 3.0, 0.0)
        rightWall.material = wallMaterial
        
        environmentObjects.add(rightWall)
        
        // Ceiling
        val ceilingTexture = textureHelper.loadTexture("textures/space_station_ceiling.jpg")
        val ceiling = Plane(20f, 20f, 1, 1)
        ceiling.setDoubleSided(true)
        ceiling.rotation.x = Math.toRadians(90.0).toFloat()
        ceiling.position = Vector3(0.0, 8.0, 0.0)
        
        val ceilingMaterial = Material()
        ceilingMaterial.colorInfluence = 0f
        ceilingMaterial.diffuseMethod = DiffuseMethod.Lambert()
        ceilingMaterial.addTexture(ceilingTexture)
        ceiling.material = ceilingMaterial
        
        environmentObjects.add(ceiling)
        
        // Space station seats
        val seatTexture = textureHelper.loadTexture("textures/space_seat.jpg")
        val seatMaterial = Material()
        seatMaterial.colorInfluence = 0f
        seatMaterial.diffuseMethod = DiffuseMethod.Lambert()
        seatMaterial.addTexture(seatTexture)
        
        // Add rows of futuristic seats
        for (row in 0 until 3) {
            for (col in -3 until 4) {
                val seat = Plane(1.2f, 1.2f, 1, 1)
                seat.rotation.x = Math.toRadians(-90.0).toFloat()
                seat.position = Vector3(
                    col * 2.5,
                    -1.5,
                    -5.0 - row * 2.5
                )
                seat.material = seatMaterial
                environmentObjects.add(seat)
            }
        }
        
        // Add space window
        val spaceTexture = textureHelper.loadTexture("textures/space_view.jpg")
        val spaceWindow = Plane(8f, 4f, 1, 1)
        spaceWindow.position = Vector3(0.0, 3.0, 9.5)
        
        val spaceMaterial = Material()
        spaceMaterial.colorInfluence = 0f
        spaceMaterial.diffuseMethod = DiffuseMethod.Lambert()
        spaceMaterial.addTexture(spaceTexture)
        spaceWindow.material = spaceMaterial
        
        environmentObjects.add(spaceWindow)
        
        // Add lights
        // Main light
        val mainLight = DirectionalLight(0f, -1f, 0f)
        mainLight.power = 0.5f
        mainLight.color = Color.rgb(220, 220, 255)
        lights.add(mainLight)
        
        // Futuristic blue accent lights
        for (i in 0 until 4) {
            val blueLight = PointLight()
            blueLight.position = Vector3(
                (i * 6) - 9.0,
                7.0,
                0.0
            )
            blueLight.power = 0.3f
            blueLight.color = Color.rgb(100, 150, 255)
            lights.add(blueLight)
        }
        
        // Red emergency light
        val redLight = PointLight()
        redLight.position = Vector3(0.0, 7.0, -9.0)
        redLight.power = 0.2f
        redLight.color = Color.rgb(255, 50, 50)
        lights.add(redLight)
    }
    
    /**
     * Creates an underwater environment
     */
    private fun createUnderwaterEnvironment() {
        // Ocean floor
        val floorTexture = textureHelper.loadTexture("textures/sand_floor.jpg")
        val floor = Plane(30f, 30f, 1, 1)
        floor.setDoubleSided(true)
        floor.rotation.x = Math.toRadians(-90.0).toFloat()
        floor.position = Vector3(0.0, -2.0, 0.0)
        
        val floorMaterial = Material()
        floorMaterial.colorInfluence = 0f
        floorMaterial.diffuseMethod = DiffuseMethod.Lambert()
        floorMaterial.addTexture(floorTexture)
        floor.material = floorMaterial
        
        environmentObjects.add(floor)
        
        // Water dome
        val waterTexture = textureHelper.loadTexture("textures/water.jpg")
        val waterDome = Sphere(20f, 32, 32)
        waterDome.setDoubleSided(true)
        waterDome.position = Vector3(0.0, 10.0, 0.0)
        
        val waterMaterial = Material()
        waterMaterial.colorInfluence = 0.3f
        waterMaterial.diffuseMethod = DiffuseMethod.Lambert()
        waterMaterial.setColor(Color.rgb(100, 150, 200))
        waterMaterial.addTexture(waterTexture)
        waterDome.material = waterMaterial
        
        environmentObjects.add(waterDome)
        
        // Underwater seating (coral formations)
        val coralTexture = textureHelper.loadTexture("textures/coral.jpg")
        val coralMaterial = Material()
        coralMaterial.colorInfluence = 0f
        coralMaterial.diffuseMethod = DiffuseMethod.Lambert()
        coralMaterial.addTexture(coralTexture)
        
        // Add coral seating in a circular arrangement
        for (angle in 0 until 360 step 30) {
            for (radius in 5..10 step 2) {
                val radians = Math.toRadians(angle.toDouble())
                
                val coral = Plane(1.5f, 1.5f, 1, 1)
                coral.rotation.x = Math.toRadians(-90.0).toFloat()
                coral.position = Vector3(
                    Math.sin(radians) * radius,
                    -1.8,
                    Math.cos(radians) * radius - 2.0
                )
                coral.material = coralMaterial
                environmentObjects.add(coral)
            }
        }
        
        // Add some underwater plants
        val plantTexture = textureHelper.loadTexture("textures/seaweed.jpg")
        val plantMaterial = Material()
        plantMaterial.colorInfluence = 0f
        plantMaterial.diffuseMethod = DiffuseMethod.Lambert()
        plantMaterial.addTexture(plantTexture)
        
        // Add plants around the perimeter
        for (angle in 0 until 360 step 20) {
            val radians = Math.toRadians(angle.toDouble())
            val radius = 15.0
            
            val plant = Plane(2.0f, 4.0f, 1, 1)
            plant.rotation.y = (radians + Math.PI).toFloat()
            plant.position = Vector3(
                Math.sin(radians) * radius,
                0.0,
                Math.cos(radians) * radius
            )
            plant.material = plantMaterial
            environmentObjects.add(plant)
        }
        
        // Add lights
        // Main underwater light
        val mainLight = DirectionalLight(0f, -1f, 0f)
        mainLight.power = 0.5f
        mainLight.color = Color.rgb(100, 150, 200)
        lights.add(mainLight)
        
        // Bioluminescent lights
        val bioColors = arrayOf(
            Color.rgb(100, 200, 255),  // Blue
            Color.rgb(100, 255, 200),  // Green
            Color.rgb(200, 100, 255)   // Purple
        )
        
        for (i in 0 until 8) {
            val angle = Math.toRadians(i * 45.0)
            val radius = 12.0
            
            val bioLight = PointLight()
            bioLight.position = Vector3(
                Math.sin(angle) * radius,
                2.0,
                Math.cos(angle) * radius
            )
            bioLight.power = 0.3f
            bioLight.color = bioColors[i % bioColors.size]
            lights.add(bioLight)
        }
    }
}