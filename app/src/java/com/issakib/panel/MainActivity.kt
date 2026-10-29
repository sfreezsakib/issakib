package com.issakib.panel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PanelTheme { Root() } }
    }
}

private fun canOverlay(c: Context) = Settings.canDrawOverlays(c)
private fun hasNotif(c: Context) =
    if (Build.VERSION.SDK_INT >= 33)
        NotificationManagerCompat.from(c).areNotificationsEnabled()
    else true
private fun allFiles(c: Context) =
    if (Build.VERSION.SDK_INT >= 30) android.os.Environment.isExternalStorageManager() else true

@Composable
private fun Root() {
    val ctx = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var overlay by remember { mutableStateOf(canOverlay(ctx)) }
    var notif by remember { mutableStateOf(hasNotif(ctx)) }
    var files by remember { mutableStateOf(allFiles(ctx)) }
    var running by remember { mutableStateOf(OverlayService.running) }

    DisposableEffect(lifecycle) {
        val obs = LifecycleEventObserver { _, e ->
            if (e == Lifecycle.Event.ON_RESUME) {
                overlay = canOverlay(ctx); notif = hasNotif(ctx); files = allFiles(ctx)
                running = OverlayService.running
            }
        }
        lifecycle.addObserver(obs)
        onDispose { lifecycle.removeObserver(obs) }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF050A14), Color(0xFF0A1424), Color(0xFF050A14))))
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Header()
            Spacer(Modifier.height(22.dp))

            Text("SETUP", color = TextDim, fontSize = 11.sp, letterSpacing = 3.sp)
            Spacer(Modifier.height(10.dp))

            PermCard(
                title = "Draw over apps",
                desc = "ক্রসহেয়ার আর প্যানেল গেমের উপরে দেখাতে লাগবে",
                granted = overlay
            ) {
                ctx.startActivity(
                    Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${ctx.packageName}"))
                )
            }
            PermCard(
                title = "Notifications",
                desc = "ব্যাকগ্রাউন্ডে সার্ভিস চালু রাখতে লাগবে",
                granted = notif
            ) {
                if (Build.VERSION.SDK_INT >= 33) {
                    (ctx as? MainActivity)?.requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 12)
                }
            }
            PermCard(
                title = "All files access",
                desc = "শুধু গেমের সেন্সিটিভিটি ফোল্ডার পড়তে লাগবে (অপশনাল)",
                granted = files
            ) {
                if (Build.VERSION.SDK_INT >= 30) {
                    runCatching {
                        ctx.startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    if (running) {
                        ctx.stopService(Intent(ctx, OverlayService::class.java))
                        running = false
                    } else {
                        ContextCompat.startForegroundService(ctx, Intent(ctx, OverlayService::class.java))
                        running = true
                    }
                },
                enabled = overlay,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (running) Color(0xFFFF5252) else Neon,
                    contentColor = Bg
                )
            ) {
                Icon(if (running) Icons.Rounded.Stop else Icons.Rounded.PlayArrow, null)
                Spacer(Modifier.width(8.dp))
                Text(if (running) "STOP PANEL" else "START PANEL", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
            if (!overlay) {
                Text(
                    "শুরু করার আগে overlay পারমিশন দাও",
                    color = Gold, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(26.dp))
            Text("SENSITIVITY PRESETS", color = TextDim, fontSize = 11.sp, letterSpacing = 3.sp)
            Text(
                "কপি করে গেমে হাতে বসাও — ফোন বদলালে ভ্যালুও বদলাবে",
                color = TextDim.copy(0.6f), fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
            )
            PRESETS.forEach { p -> PresetCard(p) }

            Spacer(Modifier.height(26.dp))
            Text("ABOUT", color = TextDim, fontSize = 11.sp, letterSpacing = 3.sp)
            Spacer(Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Surface1.copy(0.7f)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("IS SAKIB PANEL", color = Neon, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "ক্রসহেয়ার একটা ভিজ্যুয়াল রেফারেন্স, এটা গেমের এইম নড়ায় না। " +
                        "সেন্সিটিভিটি প্রিসেট গুলো ফোনভেদে ভিন্ন ফল দেবে — নিজের হাতে টিউন করে নাও।",
                        color = TextDim, fontSize = 12.sp, lineHeight = 18.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    Text("Channel: IS SAKIB", color = Gold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun Header() {
    Column {
        Box(
            Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, Brush.linearGradient(listOf(Neon.copy(0.6f), Violet.copy(0.4f))), RoundedCornerShape(24.dp))
        ) {
            Image(
                painter = painterResource(R.drawable.is_sakib_logo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Bg.copy(0.85f))))
            )
            Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                Text("IS SAKIB", color = Neon, fontSize = 26.sp, fontWeight = FontWeight.Black, letterSpacing = 4.sp)
                Text("PANEL", color = TextDim, fontSize = 12.sp, letterSpacing = 8.sp)
            }
        }
    }
}

@Composable
private fun PermCard(title: String, desc: String, granted: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        enabled = !granted,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (granted) Surface1.copy(0.5f) else Surface1
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, if (granted) Color(0xFF00E676).copy(0.4f) else Neon.copy(0.25f)
        )
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, color = TextMain, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(desc, color = TextDim, fontSize = 11.sp, lineHeight = 15.sp)
            }
            if (granted) {
                Icon(Icons.Rounded.CheckCircle, null, tint = Color(0xFF00E676))
            } else {
                Text("ALLOW", color = Neon, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
private fun PresetCard(p: SensiPreset) {
    val ctx = LocalContext.current
    var copied by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Surface1.copy(0.7f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(p.name, color = TextMain, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Text(
                    p.tag, color = Gold, fontSize = 9.sp, letterSpacing = 1.sp,
                    modifier = Modifier
                        .background(Gold.copy(0.12f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            p.values.entries.chunked(3).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    row.forEach { (k, v) ->
                        Column(
                            Modifier
                                .weight(1f)
                                .background(Surface2, RoundedCornerShape(8.dp))
                                .padding(vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(k, color = TextDim, fontSize = 9.sp)
                            Text(v.toString(), color = Neon, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (row.size < 3) Spacer(Modifier.weight((3 - row.size).toFloat()))
                }
                Spacer(Modifier.height(6.dp))
            }
            Spacer(Modifier.height(6.dp))
            TextButton(
                onClick = {
                    val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.setPrimaryClip(ClipData.newPlainText("preset", p.asText()))
                    copied = true
                },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(if (copied) "COPIED ✓" else "COPY VALUES", color = Neon, fontSize = 11.sp, letterSpacing = 1.sp)
            }
        }
    }
}