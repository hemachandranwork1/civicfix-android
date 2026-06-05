package com.civicfix.ui.screens

import android.graphics.Paint
import android.graphics.Point
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.civicfix.ui.theme.*
import com.civicfix.ui.viewmodel.IssueViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.Projection
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import java.io.File

@Composable
fun MapScreen(vm: IssueViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val issues  by vm.allIssues.collectAsState()

    LaunchedEffect(Unit) {
        Configuration.getInstance().apply {
            userAgentValue    = context.packageName
            osmdroidBasePath  = File(context.cacheDir, "osmdroid")
            osmdroidTileCache = File(context.cacheDir, "osmdroid/tiles")
        }
    }

    Box(Modifier.fillMaxSize().background(CivicNavy)) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(13.0)
                    controller.setCenter(GeoPoint(12.9716, 77.5946))
                }
            },
            update = { map ->
                map.overlays.clear()

                map.overlays.add(object : Overlay() {
                    override fun draw(
                        canvas: android.graphics.Canvas,
                        pProjection: Projection
                    ) {
                        val paint = Paint().apply {
                            isAntiAlias = true
                            style       = Paint.Style.FILL
                        }
                        issues
                            .filter { it.latitude != null && it.longitude != null }
                            .forEach { issue ->
                                val gp     = GeoPoint(issue.latitude!!, issue.longitude!!)
                                val pt     = pProjection.toPixels(gp, Point())
                                val radius = (20f + issue.voteCount * 3f).coerceAtMost(60f)
                                val alpha  = (80 + issue.voteCount * 10).coerceAtMost(160)
                                paint.color = android.graphics.Color.argb(alpha, 0, 212, 255)
                                canvas.drawCircle(pt.x.toFloat(), pt.y.toFloat(), radius, paint)
                            }
                    }
                })

                issues
                    .filter { it.latitude != null && it.longitude != null }
                    .forEach { issue ->
                        val marker = Marker(map).apply {
                            position = GeoPoint(issue.latitude!!, issue.longitude!!)
                            title    = issue.title
                            snippet  = "${issue.status} · ${issue.voteCount} votes"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        map.overlays.add(marker)
                    }

                map.invalidate()
            },
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .background(CivicDeepBlue.copy(alpha = 0.92f), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Heatmap", fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold, color = Color.White)
                Text("Brighter = more votes", fontSize = 11.sp, color = CivicMuted)
                Text("${issues.size} issues total", fontSize = 11.sp, color = CivicAccent)
            }
        }
    }
}
