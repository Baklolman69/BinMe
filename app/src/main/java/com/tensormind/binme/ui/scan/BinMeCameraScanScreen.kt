package com.tensormind.binme.ui.scan

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.tensormind.binme.data.remote.GroqVisionRepository
import com.tensormind.binme.data.remote.ScanResultData
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BinMeCameraScanScreen(
    onScanCompleted: (ScanResultData) -> Unit = {},
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (cameraPermissionState.status.isGranted) {
        CameraPreviewContent(
            onScanCompleted = onScanCompleted,
            onBackClick = onBackClick,
            modifier = modifier
        )
    } else {
        // Permission Request Fallback State
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = Color(0xFF22C55E),
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Camera Permission Required",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Please allow camera access to scan and classify waste items using AI.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFCBD5E1)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { cameraPermissionState.launchPermissionRequest() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = CircleShape
                ) {
                    Text("Grant Permission", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = {
                    val repository = GroqVisionRepository()
                    onScanCompleted(repository.getDefaultScanResult())
                }) {
                    Text("Use Demo Scan Instead", color = Color(0xFF86EFAC))
                }
            }
        }
    }
}

@Composable
private fun CameraPreviewContent(
    onScanCompleted: (ScanResultData) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cloudflareRepo = remember { com.tensormind.binme.data.remote.CloudflareVisionRepository() }

    var isScanning by remember { mutableStateOf(false) }
    var textureViewRef by remember { mutableStateOf<TextureView?>(null) }
    var useFrontCamera by remember { mutableStateOf(false) }
    var cameraModeToggle by remember { mutableStateOf(0) } // 0: OBS Camera / Live Cam, 1: Animated Demo Mode

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            isScanning = true
            scope.launch {
                val result = cloudflareRepo.classifyImageWithCloudflare(
                    context = context,
                    imageUriStr = uri.toString()
                )
                isScanning = false
                onScanCompleted(result)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (cameraModeToggle == 0) {
            // Live Camera2 via TextureView Surface (Compatible with BlueStacks OBS Virtual Camera)
            AndroidView(
                factory = { ctx ->
                    val textureView = TextureView(ctx)
                    textureViewRef = textureView

                    val bgThread = android.os.HandlerThread("CameraBg").apply { start() }
                    val bgHandler = Handler(bgThread.looper)

                    textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                        var cameraDevice: CameraDevice? = null

                        override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
                            val manager = ctx.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                            try {
                                val cameraIdList = manager.cameraIdList
                                val cameraId = if (useFrontCamera && cameraIdList.size > 1) {
                                    cameraIdList.last()
                                } else {
                                    cameraIdList.firstOrNull() ?: "0"
                                }

                                surfaceTexture.setDefaultBufferSize(1280, 720)
                                val surface = Surface(surfaceTexture)
                                if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                                        override fun onOpened(camera: CameraDevice) {
                                            cameraDevice = camera
                                            try {
                                                val builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                                                builder.addTarget(surface)

                                                camera.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                                                    override fun onConfigured(session: CameraCaptureSession) {
                                                        try {
                                                            builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
                                                            session.setRepeatingRequest(builder.build(), null, bgHandler)
                                                        } catch (e: Exception) {
                                                            Log.e("CameraScan", "Repeating request error", e)
                                                        }
                                                    }
                                                    override fun onConfigureFailed(session: CameraCaptureSession) {
                                                        Log.e("CameraScan", "Configure session failed")
                                                    }
                                                }, bgHandler)
                                            } catch (e: Exception) {
                                                Log.e("CameraScan", "Capture session setup error", e)
                                            }
                                        }
                                        override fun onDisconnected(camera: CameraDevice) {
                                            camera.close()
                                        }
                                        override fun onError(camera: CameraDevice, error: Int) {
                                            camera.close()
                                        }
                                    }, bgHandler)
                                }
                            } catch (e: Exception) {
                                Log.e("CameraScan", "Camera open error", e)
                            }
                        }

                        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
                        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                            cameraDevice?.close()
                            bgThread.quitSafely()
                            return true
                        }
                        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
                    }
                    textureView
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Demo Animated Feed View
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F172A)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = Color(0xFF22C55E),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Demo Waste Scan Mode Active",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap Shutter button to simulate AI scan",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        // Viewfinder Frame Brackets [ ] Overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val frameW = size.width * 0.72f
            val frameH = size.height * 0.45f
            val left = (size.width - frameW) / 2f
            val top = (size.height - frameH) / 2.2f
            val right = left + frameW
            val bottom = top + frameH

            val cornerLen = 32f
            val strokeW = 6f
            val cornerColor = Color(0xFF22C55E)

            // Top-Left
            drawLine(cornerColor, Offset(left, top), Offset(left + cornerLen, top), strokeW, StrokeCap.Round)
            drawLine(cornerColor, Offset(left, top), Offset(left, top + cornerLen), strokeW, StrokeCap.Round)

            // Top-Right
            drawLine(cornerColor, Offset(right, top), Offset(right - cornerLen, top), strokeW, StrokeCap.Round)
            drawLine(cornerColor, Offset(right, top), Offset(right, top + cornerLen), strokeW, StrokeCap.Round)

            // Bottom-Left
            drawLine(cornerColor, Offset(left, bottom), Offset(left + cornerLen, bottom), strokeW, StrokeCap.Round)
            drawLine(cornerColor, Offset(left, bottom), Offset(left, bottom - cornerLen), strokeW, StrokeCap.Round)

            // Bottom-Right
            drawLine(cornerColor, Offset(right, bottom), Offset(right - cornerLen, bottom), strokeW, StrokeCap.Round)
            drawLine(cornerColor, Offset(right, bottom), Offset(right, bottom + cornerLen), strokeW, StrokeCap.Round)
        }

        var scanTypeToggle by remember { mutableIntStateOf(0) } // 0: AI Photo Vision, 1: Barcode UPC Scan
        var barcodeInputText by remember { mutableStateOf("") }

        // Top Bar Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Mode Selector Pill (Photo AI vs Barcode UPC)
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(3.dp)
            ) {
                Surface(
                    color = if (scanTypeToggle == 0) Color(0xFF16A34A) else Color.Transparent,
                    shape = CircleShape,
                    modifier = Modifier.clickable { scanTypeToggle = 0 }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Photo AI",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }

                Surface(
                    color = if (scanTypeToggle == 1) Color(0xFF16A34A) else Color.Transparent,
                    shape = CircleShape,
                    modifier = Modifier.clickable { scanTypeToggle = 1 }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Barcode UPC",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Center Instruction Badge
        Surface(
            color = Color.Black.copy(alpha = 0.7f),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 220.dp)
        ) {
            Text(
                text = when {
                    isScanning -> "✨ Processing scan with AI..."
                    scanTypeToggle == 1 -> "Align product barcode in center frame"
                    else -> "Align waste item inside frame & tap shutter"
                },
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = if (isScanning) Color(0xFF86EFAC) else Color.White,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Barcode Quick Sample Chips (If Barcode Mode)
        if (scanTypeToggle == 1) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tap to simulate scanning product barcode:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Surface(
                        onClick = {
                            val result = com.tensormind.binme.data.LocalRecyclingDatabase.lookupBarcode("012000000133")
                            onScanCompleted(result)
                        },
                        shape = CircleShape,
                        color = Color(0xFF16A34A)
                    ) {
                        Text(
                            text = "🥤 Pepsi Bottle",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Surface(
                        onClick = {
                            val result = com.tensormind.binme.data.LocalRecyclingDatabase.lookupBarcode("049000028904")
                            onScanCompleted(result)
                        },
                        shape = CircleShape,
                        color = Color(0xFF2563EB)
                    ) {
                        Text(
                            text = "🥫 Soda Can",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Surface(
                        onClick = {
                            val result = com.tensormind.binme.data.LocalRecyclingDatabase.lookupBarcode("041331021432")
                            onScanCompleted(result)
                        },
                        shape = CircleShape,
                        color = Color(0xFFDC2626)
                    ) {
                        Text(
                            text = "🔋 AA Battery",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Bottom Controls Row (Gallery | Camera Shutter | Camera Switch)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp, start = 32.dp, end = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gallery Picker Button
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { galleryLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = "Pick Image from Gallery",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Camera Shutter Button
            if (isScanning) {
                CircularProgressIndicator(
                    color = Color(0xFF22C55E),
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(72.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF16A34A))
                            .clickable {
                                isScanning = true
                                scope.launch {
                                    if (scanTypeToggle == 1) {
                                        // Barcode Scan simulation
                                        val result = com.tensormind.binme.data.LocalRecyclingDatabase.lookupBarcode("012000000133")
                                        isScanning = false
                                        onScanCompleted(result)
                                    } else {
                                        val bitmap = textureViewRef?.bitmap
                                        var capturedUriStr: String? = null
                                        try {
                                            if (bitmap != null) {
                                                val file = File(context.cacheDir, "scan_capture_${System.currentTimeMillis()}.jpg")
                                                FileOutputStream(file).use { out ->
                                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
                                                }
                                                capturedUriStr = Uri.fromFile(file).toString()
                                            }
                                        } catch (e: Exception) {
                                            Log.e("CameraScan", "Failed to capture preview frame bitmap", e)
                                        }

                                        val result = cloudflareRepo.classifyImageWithCloudflare(
                                            context = context,
                                            imageUriStr = capturedUriStr,
                                            bitmap = bitmap
                                        )
                                        isScanning = false
                                        onScanCompleted(result)
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (scanTypeToggle == 1) Icons.Default.QrCodeScanner else Icons.Default.CameraAlt,
                            contentDescription = "Capture",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // Camera Switch Button (Toggle Front/Back Camera)
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { useFrontCamera = !useFrontCamera },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Switch Camera",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
