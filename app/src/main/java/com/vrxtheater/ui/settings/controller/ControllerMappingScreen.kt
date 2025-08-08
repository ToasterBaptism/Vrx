package com.vrxtheater.ui.settings.controller

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vrxtheater.R
import com.vrxtheater.data.models.ControllerButtonMapping
import com.vrxtheater.ui.theme.VrxTheaterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllerMappingScreen(
    viewModel: ControllerMappingViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val controllerMappings by viewModel.controllerMappings.collectAsState()
    val selectedController by viewModel.selectedController.collectAsState()
    val isListening by viewModel.isListeningForInput.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.controller_mapping_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = stringResource(R.string.controller_mapping_reset)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Controller selection
            if (viewModel.availableControllers.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.controller_select),
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        viewModel.availableControllers.forEach { controller ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.selectController(controller) }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = controller.id == selectedController?.id,
                                    onClick = { viewModel.selectController(controller) }
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Column {
                                    Text(
                                        text = controller.name,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = controller.connectionType,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Controller visualization
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.controller_mapping_visual),
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Simple controller visualization
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        // Left analog stick
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                .align(Alignment.CenterStart)
                        )
                        
                        // Right analog stick
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                .align(Alignment.CenterEnd)
                        )
                        
                        // Face buttons (A, B, X, Y)
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(end = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ControllerButton(
                                label = "Y",
                                isHighlighted = isListening && viewModel.currentMappingButton == ControllerButtonMapping.BUTTON_Y
                            ) {
                                viewModel.startListeningForButton(ControllerButtonMapping.BUTTON_Y)
                            }
                            
                            ControllerButton(
                                label = "X",
                                isHighlighted = isListening && viewModel.currentMappingButton == ControllerButtonMapping.BUTTON_X
                            ) {
                                viewModel.startListeningForButton(ControllerButtonMapping.BUTTON_X)
                            }
                        }
                        
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 16.dp, bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ControllerButton(
                                label = "B",
                                isHighlighted = isListening && viewModel.currentMappingButton == ControllerButtonMapping.BUTTON_B
                            ) {
                                viewModel.startListeningForButton(ControllerButtonMapping.BUTTON_B)
                            }
                            
                            ControllerButton(
                                label = "A",
                                isHighlighted = isListening && viewModel.currentMappingButton == ControllerButtonMapping.BUTTON_A
                            ) {
                                viewModel.startListeningForButton(ControllerButtonMapping.BUTTON_A)
                            }
                        }
                        
                        // D-pad
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(start = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ControllerButton(
                                label = "↑",
                                isHighlighted = isListening && viewModel.currentMappingButton == ControllerButtonMapping.DPAD_UP
                            ) {
                                viewModel.startListeningForButton(ControllerButtonMapping.DPAD_UP)
                            }
                            
                            Row {
                                ControllerButton(
                                    label = "←",
                                    isHighlighted = isListening && viewModel.currentMappingButton == ControllerButtonMapping.DPAD_LEFT
                                ) {
                                    viewModel.startListeningForButton(ControllerButtonMapping.DPAD_LEFT)
                                }
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                ControllerButton(
                                    label = "→",
                                    isHighlighted = isListening && viewModel.currentMappingButton == ControllerButtonMapping.DPAD_RIGHT
                                ) {
                                    viewModel.startListeningForButton(ControllerButtonMapping.DPAD_RIGHT)
                                }
                            }
                            
                            ControllerButton(
                                label = "↓",
                                isHighlighted = isListening && viewModel.currentMappingButton == ControllerButtonMapping.DPAD_DOWN
                            ) {
                                viewModel.startListeningForButton(ControllerButtonMapping.DPAD_DOWN)
                            }
                        }
                    }
                    
                    if (isListening) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = stringResource(R.string.controller_mapping_listening),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = { viewModel.cancelListening() }
                        ) {
                            Text(stringResource(R.string.controller_mapping_cancel))
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Button mappings
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.controller_mapping_buttons),
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Face buttons
                    MappingCategory(title = stringResource(R.string.controller_mapping_face_buttons)) {
                        MappingRow(
                            buttonName = stringResource(R.string.controller_mapping_a),
                            mappedValue = controllerMappings[ControllerButtonMapping.BUTTON_A] ?: "Not mapped",
                            onClick = { viewModel.startListeningForButton(ControllerButtonMapping.BUTTON_A) }
                        )
                        
                        MappingRow(
                            buttonName = stringResource(R.string.controller_mapping_b),
                            mappedValue = controllerMappings[ControllerButtonMapping.BUTTON_B] ?: "Not mapped",
                            onClick = { viewModel.startListeningForButton(ControllerButtonMapping.BUTTON_B) }
                        )
                        
                        MappingRow(
                            buttonName = stringResource(R.string.controller_mapping_x),
                            mappedValue = controllerMappings[ControllerButtonMapping.BUTTON_X] ?: "Not mapped",
                            onClick = { viewModel.startListeningForButton(ControllerButtonMapping.BUTTON_X) }
                        )
                        
                        MappingRow(
                            buttonName = stringResource(R.string.controller_mapping_y),
                            mappedValue = controllerMappings[ControllerButtonMapping.BUTTON_Y] ?: "Not mapped",
                            onClick = { viewModel.startListeningForButton(ControllerButtonMapping.BUTTON_Y) }
                        )
                    }
                    
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    // D-pad
                    MappingCategory(title = stringResource(R.string.controller_mapping_dpad)) {
                        MappingRow(
                            buttonName = stringResource(R.string.controller_mapping_dpad_up),
                            mappedValue = controllerMappings[ControllerButtonMapping.DPAD_UP] ?: "Not mapped",
                            onClick = { viewModel.startListeningForButton(ControllerButtonMapping.DPAD_UP) }
                        )
                        
                        MappingRow(
                            buttonName = stringResource(R.string.controller_mapping_dpad_down),
                            mappedValue = controllerMappings[ControllerButtonMapping.DPAD_DOWN] ?: "Not mapped",
                            onClick = { viewModel.startListeningForButton(ControllerButtonMapping.DPAD_DOWN) }
                        )
                        
                        MappingRow(
                            buttonName = stringResource(R.string.controller_mapping_dpad_left),
                            mappedValue = controllerMappings[ControllerButtonMapping.DPAD_LEFT] ?: "Not mapped",
                            onClick = { viewModel.startListeningForButton(ControllerButtonMapping.DPAD_LEFT) }
                        )
                        
                        MappingRow(
                            buttonName = stringResource(R.string.controller_mapping_dpad_right),
                            mappedValue = controllerMappings[ControllerButtonMapping.DPAD_RIGHT] ?: "Not mapped",
                            onClick = { viewModel.startListeningForButton(ControllerButtonMapping.DPAD_RIGHT) }
                        )
                    }
                    
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    // Shoulder buttons
                    MappingCategory(title = stringResource(R.string.controller_mapping_shoulder_buttons)) {
                        MappingRow(
                            buttonName = stringResource(R.string.controller_mapping_l1),
                            mappedValue = controllerMappings[ControllerButtonMapping.BUTTON_L1] ?: "Not mapped",
                            onClick = { viewModel.startListeningForButton(ControllerButtonMapping.BUTTON_L1) }
                        )
                        
                        MappingRow(
                            buttonName = stringResource(R.string.controller_mapping_r1),
                            mappedValue = controllerMappings[ControllerButtonMapping.BUTTON_R1] ?: "Not mapped",
                            onClick = { viewModel.startListeningForButton(ControllerButtonMapping.BUTTON_R1) }
                        )
                        
                        MappingRow(
                            buttonName = stringResource(R.string.controller_mapping_l2),
                            mappedValue = controllerMappings[ControllerButtonMapping.BUTTON_L2] ?: "Not mapped",
                            onClick = { viewModel.startListeningForButton(ControllerButtonMapping.BUTTON_L2) }
                        )
                        
                        MappingRow(
                            buttonName = stringResource(R.string.controller_mapping_r2),
                            mappedValue = controllerMappings[ControllerButtonMapping.BUTTON_R2] ?: "Not mapped",
                            onClick = { viewModel.startListeningForButton(ControllerButtonMapping.BUTTON_R2) }
                        )
                    }
                    
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    // Special buttons
                    MappingCategory(title = stringResource(R.string.controller_mapping_special_buttons)) {
                        MappingRow(
                            buttonName = stringResource(R.string.controller_mapping_start),
                            mappedValue = controllerMappings[ControllerButtonMapping.BUTTON_START] ?: "Not mapped",
                            onClick = { viewModel.startListeningForButton(ControllerButtonMapping.BUTTON_START) }
                        )
                        
                        MappingRow(
                            buttonName = stringResource(R.string.controller_mapping_select),
                            mappedValue = controllerMappings[ControllerButtonMapping.BUTTON_SELECT] ?: "Not mapped",
                            onClick = { viewModel.startListeningForButton(ControllerButtonMapping.BUTTON_SELECT) }
                        )
                        
                        MappingRow(
                            buttonName = stringResource(R.string.controller_mapping_l3),
                            mappedValue = controllerMappings[ControllerButtonMapping.BUTTON_L3] ?: "Not mapped",
                            onClick = { viewModel.startListeningForButton(ControllerButtonMapping.BUTTON_L3) }
                        )
                        
                        MappingRow(
                            buttonName = stringResource(R.string.controller_mapping_r3),
                            mappedValue = controllerMappings[ControllerButtonMapping.BUTTON_R3] ?: "Not mapped",
                            onClick = { viewModel.startListeningForButton(ControllerButtonMapping.BUTTON_R3) }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Save button
            Button(
                onClick = { viewModel.saveControllerMappings() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.controller_mapping_save))
            }
        }
        
        // Reset confirmation dialog
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text(stringResource(R.string.controller_mapping_reset_confirm_title)) },
                text = { Text(stringResource(R.string.controller_mapping_reset_confirm_message)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.resetControllerMappings()
                            showResetDialog = false
                        }
                    ) {
                        Text(stringResource(R.string.controller_mapping_reset))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showResetDialog = false }
                    ) {
                        Text(stringResource(R.string.action_cancel))
                    }
                }
            )
        }
    }
}

@Composable
fun MappingCategory(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        content()
    }
}

@Composable
fun MappingRow(
    buttonName: String,
    mappedValue: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buttonName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = mappedValue,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ControllerButton(
    label: String,
    isHighlighted: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(
                if (isHighlighted) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .border(
                width = 1.dp,
                color = if (isHighlighted) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isHighlighted) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun RadioButton(
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimary)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ControllerMappingScreenPreview() {
    VrxTheaterTheme {
        ControllerMappingScreen(
            onNavigateUp = {}
        )
    }
}