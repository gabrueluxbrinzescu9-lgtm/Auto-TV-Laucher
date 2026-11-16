
package ro.autotv.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
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
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    val centerX = w / 2
                    val baseY = h * 0.68f

                    // Lumina din spatele masinii
                    drawCircle(
                        color = Color(0x443B82F6),
                        radius = w * 0.23f,
                        center = Offset(centerX, baseY)
                    )

                    // Lumina de sub masina
                    drawRoundRect(
                        color = Color(0xAA168BFF),
                        topLeft = Offset(w * 0.25f, baseY + h * 0.045f),
                        size = Size(w * 0.50f, h * 0.012f),
                        cornerRadius = CornerRadius(30f)
                    )

                    // Corpul masinii
                    val body = Path().apply {
                        moveTo(w * 0.16f, baseY)
                        lineTo(w * 0.22f, baseY - h * 0.09f)
                        lineTo(w * 0.34f, baseY - h * 0.13f)
                        lineTo(w * 0.43f, baseY - h * 0.27f)
                        lineTo(w * 0.65f, baseY - h * 0.27f)
                        lineTo(w * 0.77f, baseY - h * 0.12f)
                        lineTo(w * 0.85f, baseY - h * 0.08f)
                        lineTo(w * 0.89f, baseY)
                        lineTo(w * 0.86f, baseY + h * 0.035f)
                        lineTo(w * 0.15f, baseY + h * 0.035f)
                        close()
                    }

                    drawPath(
                        path = body,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0xFF45C6FF),
                                Color(0xFF1762D8),
                                Color(0xFF09245F)
                            )
                        )
                    )

                    // Geamurile masinii
                    val windows = Path().apply {
                        moveTo(w * 0.37f, baseY - h * 0.145f)
                        lineTo(w * 0.45f, baseY - h * 0.245f)
                        lineTo(w * 0.63f, baseY - h * 0.245f)
                        lineTo(w * 0.72f, baseY - h * 0.145f)
                        close()
                    }

                    drawPath(
                        path = windows,
                        color = Color(0xFF09182C)
                    )

                    drawPath(
                        path = windows,
                        color = Color(0xFF65D9FF),
                        style = Stroke(width = 4f)
                    )

                    // Faruri
                    drawRoundRect(
                        color = Color(0xFFE8FAFF),
                        topLeft = Offset(w * 0.78f, baseY - h * 0.055f),
                        size = Size(w * 0.065f, h * 0.018f),
                        cornerRadius = CornerRadius(12f)
                    )

                    drawRoundRect(
                        color = Color(0xFFFF344D),
                        topLeft = Offset(w * 0.17f, baseY - h * 0.055f),
                        size = Size(w * 0.055f, h * 0.018f),
                        cornerRadius = CornerRadius(12f)
                    )

                    // Roti
                    for (x in listOf(w * 0.31f, w * 0.70f)) {
                        drawCircle(
                            color = Color(0xFF05070D),
                            radius = h * 0.075f,
                            center = Offset(x, baseY + h * 0.025f)
                        )

                        drawCircle(
                            color = Color(0xFF8795A8),
                            radius = h * 0.038f,
                            center = Offset(x, baseY + h * 0.025f)
                        )

                        drawCircle(
                            color = Color(0xFF1C2A40),
                            radius = h * 0.018f,
                            center = Offset(x, baseY + h * 0.025f)
                        )
                    }

                    // Contur luminos
                    drawPath(
                        path = body,
                        color = Color(0xFF55CFFF),
                        style = Stroke(width = 3f)
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 45.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "AUTO TV",
                        color = Color.White,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "LAUNCHER",
                        color = Color(0xFF55CFFF),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "YOUR TV. YOUR STYLE.",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 35.dp),
                    color = Color(0xFF8FA9CA),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
