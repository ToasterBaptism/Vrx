package com.vrxtheater.ui.vr

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vrxtheater.R
import com.vrxtheater.vr.scene.TheaterEnvironmentType

/**
 * Dialog for selecting theater environments
 */
class EnvironmentSelectionDialog(
    context: Context,
    private val currentEnvironment: TheaterEnvironmentType,
    private val onEnvironmentSelected: (TheaterEnvironmentType) -> Unit
) : Dialog(context) {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EnvironmentAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_environment_selection)
        
        // Set dialog title
        setTitle(R.string.vr_select_environment)
        
        // Initialize RecyclerView
        recyclerView = findViewById(R.id.environmentRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        
        // Create adapter with all environment types
        adapter = EnvironmentAdapter(
            TheaterEnvironmentType.values().toList(),
            currentEnvironment,
            onEnvironmentSelected
        )
        recyclerView.adapter = adapter
    }
    
    /**
     * Adapter for environment selection
     */
    private inner class EnvironmentAdapter(
        private val environments: List<TheaterEnvironmentType>,
        private val currentEnvironment: TheaterEnvironmentType,
        private val onEnvironmentSelected: (TheaterEnvironmentType) -> Unit
    ) : RecyclerView.Adapter<EnvironmentViewHolder>() {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnvironmentViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_environment, parent, false)
            return EnvironmentViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: EnvironmentViewHolder, position: Int) {
            val environment = environments[position]
            holder.bind(environment, environment == currentEnvironment)
        }
        
        override fun getItemCount(): Int = environments.size
    }
    
    /**
     * ViewHolder for environment items
     */
    private inner class EnvironmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.environmentName)
        private val thumbnailImageView: ImageView = itemView.findViewById(R.id.environmentThumbnail)
        private val selectedIndicator: View = itemView.findViewById(R.id.selectedIndicator)
        
        fun bind(environment: TheaterEnvironmentType, isSelected: Boolean) {
            // Set environment name
            nameTextView.text = getEnvironmentName(environment)
            
            // Set environment thumbnail
            thumbnailImageView.setImageResource(getEnvironmentThumbnail(environment))
            
            // Show/hide selected indicator
            selectedIndicator.visibility = if (isSelected) View.VISIBLE else View.GONE
            
            // Set click listener
            itemView.setOnClickListener {
                onEnvironmentSelected(environment)
                dismiss()
            }
        }
        
        /**
         * Returns the display name for an environment type
         */
        private fun getEnvironmentName(environment: TheaterEnvironmentType): String {
            return when (environment) {
                TheaterEnvironmentType.CLASSIC_THEATER -> context.getString(R.string.environment_classic_theater)
                TheaterEnvironmentType.MODERN_THEATER -> context.getString(R.string.environment_modern_theater)
                TheaterEnvironmentType.IMAX_DOME -> context.getString(R.string.environment_imax_dome)
                TheaterEnvironmentType.OUTDOOR_CINEMA -> context.getString(R.string.environment_outdoor_cinema)
                TheaterEnvironmentType.SPACE_STATION -> context.getString(R.string.environment_space_station)
                TheaterEnvironmentType.UNDERWATER -> context.getString(R.string.environment_underwater)
            }
        }
        
        /**
         * Returns the thumbnail resource for an environment type
         */
        private fun getEnvironmentThumbnail(environment: TheaterEnvironmentType): Int {
            return when (environment) {
                TheaterEnvironmentType.CLASSIC_THEATER -> R.drawable.thumb_classic_theater
                TheaterEnvironmentType.MODERN_THEATER -> R.drawable.thumb_modern_theater
                TheaterEnvironmentType.IMAX_DOME -> R.drawable.thumb_imax_dome
                TheaterEnvironmentType.OUTDOOR_CINEMA -> R.drawable.thumb_outdoor_cinema
                TheaterEnvironmentType.SPACE_STATION -> R.drawable.thumb_space_station
                TheaterEnvironmentType.UNDERWATER -> R.drawable.thumb_underwater
            }
        }
    }
}