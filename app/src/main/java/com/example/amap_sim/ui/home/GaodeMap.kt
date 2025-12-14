package com.example.amap_sim.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.baidu.mapapi.map.MapView

@Composable
fun GaodeMap(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 使用remember创建MapView实例，确保只创建一次
    val mapView = remember {
        MapView(context)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    // Activity onResume 时，确保 MapView 也 resume
                    mapView.onResume()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    // Activity onPause 时，暂停 MapView
                    mapView.onPause()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    // Activity onDestroy 时，销毁 MapView
                    mapView.onDestroy()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            // 组件销毁时，如果还没有销毁MapView，则销毁它
            // 注意：这里可能与ON_DESTROY事件重复调用，但大多数SDK都能处理
            try {
                mapView.onDestroy()
            } catch (e: Exception) {
                // 忽略重复销毁的异常
            }
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier.fillMaxSize()
    )
}