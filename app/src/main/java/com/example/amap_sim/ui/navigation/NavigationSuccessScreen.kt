package com.example.amap_sim.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationSuccessScreen(
    startLocation: String = "我的位置",
    waypoint: String? = null,
    endLocation: String = "目的地",
    transportMode: String = "公共交通",
    onConfirmClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Success Icon
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "导航成功",
                modifier = Modifier.size(80.dp),
                tint = Color(0xFF4CAF50)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Success Text
            Text(
                text = "导航成功",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Route Information Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "路线信息",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Start location
                    RouteLocationItem(
                        location = startLocation,
                        color = Color(0xFF4CAF50),
                        label = "起点"
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Waypoint if exists
                    waypoint?.let { waypointText ->
                        RouteLocationItem(
                            location = waypointText,
                            color = Color(0xFF2196F3),
                            label = "途经点"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    // End location
                    RouteLocationItem(
                        location = endLocation,
                        color = Color(0xFFFF5722),
                        label = "终点"
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Transport mode
                    Text(
                        text = "交通方式: $transportMode",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Confirm Button
            Button(
                onClick = onConfirmClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "确定",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun RouteLocationItem(
    location: String,
    color: androidx.compose.ui.graphics.Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: $location",
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}