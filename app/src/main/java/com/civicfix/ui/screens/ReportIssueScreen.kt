package com.civicfix.ui.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.civicfix.ui.theme.*
import com.civicfix.ui.viewmodel.IssueViewModel
import com.google.accompanist.permissions.*
import java.io.File

private val issueCategories = listOf("Pothole", "Garbage", "Streetlight", "Water Leak", "Other")

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReportIssueScreen(
    navController: NavController,
    vm: IssueViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(issueCategories[0]) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageFile by remember { mutableStateOf<File?>(null) }
    var showImageSheet by remember { mutableStateOf(false) }

    val isLoading by vm.isLoading.collectAsState()
    val reportSuccess by vm.reportSuccess.collectAsState()
    val error by vm.error.collectAsState()

    var lat by remember { mutableDoubleStateOf(12.9716) }
    var lng by remember { mutableDoubleStateOf(77.5946) }
    var address by remember { mutableStateOf("Bengaluru, Karnataka") }

    LaunchedEffect(reportSuccess) {
        if (reportSuccess) { vm.clearReportSuccess(); navController.popBackStack() }
    }

    val cameraFile = remember {
        File(context.cacheDir, "images/camera_${System.currentTimeMillis()}.jpg")
            .also { it.parentFile?.mkdirs() }
    }
    val cameraUri = remember(cameraFile) {
        FileProvider.getUriForFile(context, "${context.packageName}.provider", cameraFile)
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) { imageUri = cameraUri; imageFile = cameraFile }
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { imageUri = it; imageFile = uriToFile(context, it) }
    }

    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    val locationPermission = rememberMultiplePermissionsState(
        listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    )

    LaunchedEffect(Unit) {
        if (!locationPermission.allPermissionsGranted) locationPermission.launchMultiplePermissionRequest()
    }
    LaunchedEffect(locationPermission.allPermissionsGranted) {
        if (locationPermission.allPermissionsGranted) {
            getLastLocation(context) { la, ln, addr -> lat = la; lng = ln; address = addr }
        }
    }

    Scaffold(
        containerColor = CivicNavy,
        topBar = {
            TopAppBar(
                title = { Text("Report Issue", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CivicDeepBlue, titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Image picker
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp)
                    .clip(RoundedCornerShape(16.dp)).background(CivicDeepBlue)
                    .border(1.dp, if (imageUri != null) CivicAccent.copy(0.3f) else CivicBorder, RoundedCornerShape(16.dp))
                    .clickable { showImageSheet = true },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(model = imageUri, contentDescription = "Selected image",
                        modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    Box(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                        .clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(0.6f))
                        .clickable { showImageSheet = true }.padding(6.dp)) {
                        Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddAPhoto, null, tint = CivicAccent, modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Add photo", color = CivicMuted, fontSize = 14.sp)
                    }
                }
            }

            if (showImageSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showImageSheet = false },
                    containerColor = CivicDeepBlue
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Choose image source", fontWeight = FontWeight.SemiBold, color = Color.White)
                        Spacer(Modifier.height(8.dp))
                        ImageSourceOption(Icons.Default.CameraAlt, "Camera") {
                            showImageSheet = false
                            if (cameraPermission.status.isGranted) cameraLauncher.launch(cameraUri)
                            else cameraPermission.launchPermissionRequest()
                        }
                        ImageSourceOption(Icons.Default.Image, "Gallery") {
                            showImageSheet = false
                            galleryLauncher.launch("image/*")
                        }
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }

            CivicTextField(value = title, onValueChange = { title = it },
                label = "Issue title", leadingIcon = Icons.Default.Title)

            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(14.dp), maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CivicAccent, unfocusedBorderColor = CivicBorder,
                    focusedLabelColor = CivicAccent, unfocusedLabelColor = CivicMuted,
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    focusedContainerColor = CivicDeepBlue, unfocusedContainerColor = CivicDeepBlue
                )
            )

            Text("Category", fontSize = 13.sp, color = CivicMuted)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(issueCategories) { cat ->
                    val sel = category == cat
                    FilterChip(
                        selected = sel, onClick = { category = cat },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CivicAccent.copy(0.15f),
                            selectedLabelColor = CivicAccent,
                            containerColor = CivicDeepBlue, labelColor = CivicMuted
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true, selected = sel,
                            selectedBorderColor = CivicAccent.copy(0.4f), borderColor = CivicBorder
                        )
                    )
                }
            }

            Surface(color = CivicDeepBlue, shape = RoundedCornerShape(14.dp),
                border = BorderStroke(0.5.dp, CivicBorder), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Default.LocationOn, null, tint = CivicAccent, modifier = Modifier.size(20.dp))
                    Column {
                        Text("Location", fontSize = 11.sp, color = CivicMuted)
                        Text(address, fontSize = 13.sp, color = Color.White, maxLines = 2)
                    }
                }
            }

            AnimatedVisibility(error != null) {
                Text(error ?: "", color = StatusRejected, fontSize = 13.sp)
            }

            GradientButton(
                text = if (isLoading) "Submitting..." else "Submit Report",
                enabled = !isLoading && title.isNotBlank() && description.isNotBlank(),
                onClick = { vm.reportIssue(title, description, category, lat, lng, address, imageFile) }
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun ImageSourceOption(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Surface(onClick = onClick, color = CivicBlue, shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, null, tint = CivicAccent, modifier = Modifier.size(22.dp))
            Text(label, color = Color.White, fontSize = 15.sp)
        }
    }
}

fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val stream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { stream.copyTo(it) }
        file
    } catch (e: Exception) { null }
}

@Suppress("MissingPermission")
fun getLastLocation(context: Context, onResult: (Double, Double, String) -> Unit) {
    try {
        val client = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context)
        client.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                val addr = try {
                    android.location.Geocoder(context)
                        .getFromLocation(loc.latitude, loc.longitude, 1)
                        ?.firstOrNull()?.getAddressLine(0) ?: "Unknown location"
                } catch (e: Exception) { "Unknown location" }
                onResult(loc.latitude, loc.longitude, addr)
            }
        }
    } catch (e: Exception) { }
}
