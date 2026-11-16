package ro.autotv.launcher

import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.focus.focusable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.tv.material3.Text

data class AppInfo(
    val name: String,
    val packageName: String,
    val activityName: String,
    val icon: ImageBitmap
)

class MainActivity : ComponentActivity() {

    private fun loadApps(): List<AppInfo> {
        val found = linkedMapOf<String, AppInfo>()

        val queries = listOf(
            Intent(Intent.ACTION_MAIN).addCategory(
                Intent.CATEGORY_LEANBACK_LAUNCHER
            ),
            Intent(Intent.ACTION_MAIN).addCategory(
                Intent.CATEGORY_LAUNCHER
            )
        )

        for (query in queries) {
            val results = packageManager.queryIntentActivities(
                query,
                PackageManager.MATCH_DEFAULT_ONLY
            )

            for (result in results) {
                val activity = result.activityInfo ?: continue
                val pkg = activity.packageName

                if (pkg == packageName || found.containsKey(pkg)) continue

                try {
                    val icon = result.loadIcon(packageManager)
                    val bitmap = drawableToBitmap(icon)

                    found[pkg] = AppInfo(
                        name = result.loadLabel(packageManager).toString(),
                        packageName = pkg,
                        activityName = activity.name,
                        icon = bitmap
                    )
                } catch (_: Exception) {
                }
            }
        }

        return found.values.sortedBy { it.name.lowercase() }
    }

    private fun drawableToBitmap(drawable: Drawable): ImageBitmap {
        val width = drawable.intrinsicWidth.coerceIn(1, 256)
        val height = drawable.intrinsicHeight.coerceIn(1, 256)

        val bitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888
        )

        val canvas = android.graphics.Canvas(bitmap)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)

        return bitmap.asImageBitmap()
    }

    private fun loadHidden(): Set<String> {
        return getSharedPreferences("launcher", MODE_PRIVATE)
            .getStringSet("hidden", emptySet())
            ?.toSet() ?: emptySet()
    }

    private fun saveHidden(hidden: Set<String>) {
        getSharedPreferences("launcher", MODE_PRIVATE)
            .edit()
            .putStringSet("hidden", hidden.toSet())
            .apply()
    }

    private fun openApp(app: AppInfo) {
        try {
            val intent = Intent().apply {
                setClassName(app.packageName, app.activityName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (_: Exception) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var apps by remember { mutableStateOf(loadApps()) }
            var hidden by remember { mutableStateOf(loadHidden()) }
            var settings by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF101B35),
                                Color(0xFF090D18),
                                Color(0xFF02040A)
                            )
                        )
                    )
            ) {
                CarBackground()

                if (settings) {
                    SettingsScreen(
                        apps = apps,
                        hidden = hidden,
                        onBack = { settings = false },
                        onToggle = { app, show ->
                            val updated = if (show) {
                                hidden - app.packageName
                            } else {
                                hidden + app.packageName
                            }

                            hidden = updated
                            saveHidden(updated)
                        }
                    )
                } else {
                    HomeScreen(
                        apps = apps.filter {
                            it.packageName !in hidden
                        },
                        onOpen = { openApp(it) },
                        onSettings = { settings = true },
                        onRefresh = { apps = loadApps() }
                    )
                }
            }
        }
    }
}

@Composable
fun CarBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val baseY = h * 0.84f

        drawCircle(
            color = Color(0x443B82F6),
            radius = w * 0.22f,
            center = Offset(w / 2, baseY)
        )

        drawRoundRect(
            color = Color(0xAA168BFF),
            topLeft = Offset(w * 0.25f, baseY),
            size = Size(w * 0.5f, h * 0.012f),
            cornerRadius = CornerRadius(30f)
        )

        val body = Path().apply {
            moveTo(w * 0.16f, baseY)
            lineTo(w * 0.22f, baseY - h * 0.07f)
            lineTo(w * 0.34f, baseY - h * 0.12f)
            lineTo(w * 0.43f, baseY - h * 0.23f)
            lineTo(w * 0.65f, baseY - h * 0.23f)
            lineTo(w * 0.77f, baseY - h * 0.12f)
            lineTo(w * 0.88f, baseY - h * 0.07f)
            lineTo(w * 0.89f, baseY)
            close()
        }

        drawPath(
            body,
            brush = Brush.verticalGradient(
                listOf(
                    Color(0xFF45C6FF),
                    Color(0xFF1762D8),
                    Color(0xFF09245F)
                )
            )
        )

        drawPath(
            body,
            color = Color(0xFF55CFFF),
            style = Stroke(width = 3f)
        )

        for (x in listOf(w * 0.31f, w * 0.70f)) {
            drawCircle(
                color = Color(0xFF05070D),
                radius = h * 0.055f,
                center = Offset(x, baseY)
            )
            drawCircle(
                color = Color(0xFF8795A8),
                radius = h * 0.027f,
                center = Offset(x, baseY)
            )
        }
    }
}

@Composable
fun HomeScreen(
    apps: List<AppInfo>,
    onOpen: (AppInfo) -> Unit,
    onSettings: () -> Unit,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "AUTO TV",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "PREMIUM LAUNCHER",
                    color = Color(0xFF55CFFF),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionButton("REÎNNOIEȘTE", onRefresh)
                ActionButton("SETĂRI", onSettings)
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "Aplicațiile mele",
            color = Color.White,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        if (apps.isEmpty()) {
            Text(
                "Nu au fost găsite aplicații compatibile.",
                color = Color.White
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 135.dp),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(apps, key = { it.packageName }) { app ->
                    AppTile(app = app) {
                        onOpen(app)
                    }
                }
            }
        }

        Text(
            "YOUR TV. YOUR STYLE.",
            color = Color(0xFF8FA9CA),
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun AppTile(
    app: AppInfo,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(125.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xDD17243D))
            .border(
                1.dp,
                Color(0xFF31527D),
                RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .focusable()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            bitmap = app.icon,
            contentDescription = app.name,
            modifier = Modifier.size(55.dp)
        )

        Spacer(Modifier.height(9.dp))

        Text(
            app.name,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
fun ActionButton(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1762D8))
            .clickable { onClick() }
            .focusable()
            .padding(horizontal = 18.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SettingsScreen(
    apps: List<AppInfo>,
    hidden: Set<String>,
    onBack: () -> Unit,
    onToggle: (AppInfo, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp)
    ) {
        Text(
            "GESTIONARE APLICAȚII",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Alege ce aplicații vrei să apară în meniu.",
            color = Color(0xFF8FA9CA),
            fontSize = 14.sp
        )

        Spacer(Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 220.dp),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(apps, key = { it.packageName }) { app ->
                val visible = app.packageName !in hidden

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF17243D))
                        .clickable {
                            onToggle(app, !visible)
                        }
                        .focusable()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        bitmap = app.icon,
                        contentDescription = app.name,
                        modifier = Modifier.size(36.dp)
                    )

                    Spacer(Modifier.width(10.dp))

                    Column(Modifier.weight(1f)) {
                        Text(
                            app.name,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2
                        )
                        Text(
                            if (visible) "AFIȘATĂ" else "ASCUNSĂ",
                            color = if (visible) Color(0xFF55CFFF)
                            else Color(0xFFFF7B7B),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        ActionButton("ÎNAPOI LA MENIU", onBack)
    }
}
