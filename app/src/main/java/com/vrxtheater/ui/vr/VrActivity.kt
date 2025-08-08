package com.vrxtheater.ui.vr

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.view.Surface
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.vrxtheater.R
import com.vrxtheater.VrxTheaterApplication
import com.vrxtheater.data.models.VrSettings
import com.vrxtheater.data.repository.GameRepository
import com.vrxtheater.data.repository.SettingsRepository
import com.vrxtheater.databinding.ActivityVrBinding
import com.vrxtheater.services.VrProjectionService
import com.vrxtheater.vr.renderer.VrRenderer
import com.vrxtheater.vr.scene.TheaterEnvironmentType
import com.vrxtheater.vr.util.SensorFusion
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class VrActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityVrBinding
    private lateinit var vrRenderer: VrRenderer
    private lateinit var sensorFusion: SensorFusion
    
    private var mediaProjectionManager: MediaProjectionManager? = null
    private var mediaProjection: MediaProjection? = null
    private var packageName: String? = null
    private var isTestPattern = false
    
    @Inject
    lateinit var settingsRepository: SettingsRepository
    
    @Inject
    lateinit var gameRepository: GameRepository
    
    private val startMediaProjection = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            // Start projection service
            val intent = Intent(this, VrProjectionService::class.java).apply {
                putExtra("resultCode", result.resultCode)
                putExtra("resultData", result.data)
                putExtra("packageName", packageName)
            }
            startService(intent)
            
            // Show VR view
            binding.vrSurfaceView.visibility = View.VISIBLE
            binding.loadingLayout.visibility = View.GONE
        } else {
            // User denied screen capture permission
            Toast.makeText(
                this,
                "Screen capture permission denied",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Keep screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        // Hide system UI
        hideSystemUI()
        
        // Initialize binding
        binding = ActivityVrBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Get package name from intent
        packageName = intent.getStringExtra("package_name")
        
        // Check if this is a test pattern request
        isTestPattern = intent.getBooleanExtra("test_pattern", false)
        
        // Initialize sensor fusion
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorFusion = SensorFusion(sensorManager)
        
        // Initialize VR renderer
        initializeVrRenderer()
        
        // Set up UI controls
        setupControls()
        
        // Request media projection if not in test pattern mode
        if (!isTestPattern) {
            requestMediaProjection()
        } else {
            // Show VR view immediately for test pattern
            binding.vrSurfaceView.visibility = View.VISIBLE
            binding.loadingLayout.visibility = View.GONE
            
            // Enable test pattern in renderer
            vrRenderer.setTestPatternMode(true)
        }
    }
    
    private fun initializeVrRenderer() {
        vrRenderer = VrRenderer(
            context = this,
            sensorFusion = sensorFusion,
            onScreenTextureReady = { surface ->
                // Surface is ready for rendering game content
                handleScreenTextureReady(surface)
            }
        )
        
        // Set renderer to surface view
        binding.vrSurfaceView.setRenderer(vrRenderer)
        
        // Load settings
        lifecycleScope.launch {
            val settings = settingsRepository.settings.first()
            vrRenderer.updateSettings(settings)
        }
    }
    
    private fun setupControls() {
        // Recenter button
        binding.recenterButton.setOnClickListener {
            vrRenderer.recenterView()
        }
        
        // Exit button
        binding.exitButton.setOnClickListener {
            finish()
        }
        
        // Environment button
        binding.environmentButton.setOnClickListener {
            showEnvironmentSelectionDialog()
        }
    }
    
    private fun showEnvironmentSelectionDialog() {
        val currentEnvironment = vrRenderer.getCurrentEnvironmentType()
        
        EnvironmentSelectionDialog(
            this,
            currentEnvironment
        ) { selectedEnvironment ->
            vrRenderer.changeEnvironment(selectedEnvironment)
        }.show()
    }
    
    private fun requestMediaProjection() {
        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        
        if (packageName != null) {
            // Launch game first
            if (gameRepository.launchGame(packageName!!)) {
                // Wait a moment for the game to start
                binding.vrSurfaceView.postDelayed({
                    // Then request screen capture
                    startMediaProjection.launch(mediaProjectionManager?.createScreenCaptureIntent())
                }, 1000)
            } else {
                // Failed to launch game
                Toast.makeText(
                    this,
                    getString(R.string.error_launch_failed),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        } else {
            // No game selected, just capture the screen
            startMediaProjection.launch(mediaProjectionManager?.createScreenCaptureIntent())
        }
    }
    
    private fun handleScreenTextureReady(surface: Surface) {
        // Surface is ready for rendering game content
        // This will be handled by the VrProjectionService
    }
    
    override fun onResume() {
        super.onResume()
        sensorFusion.start()
        binding.vrSurfaceView.onResume()
    }
    
    override fun onPause() {
        super.onPause()
        sensorFusion.stop()
        binding.vrSurfaceView.onPause()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        
        // Stop projection service if not in test pattern mode
        if (!isTestPattern) {
            stopService(Intent(this, VrProjectionService::class.java))
        }
        
        // Clean up resources
        vrRenderer.onDestroy()
    }
    
    private fun hideSystemUI() {
        // Hide the status bar and navigation bar
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
        )
    }
}