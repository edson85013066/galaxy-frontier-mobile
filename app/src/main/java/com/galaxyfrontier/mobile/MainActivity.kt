package com.galaxyfrontier.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin
import kotlin.random.Random

private enum class Screen { MENU, GAME }

private val SpaceBlack = Color(0xFF030511)
private val DeepBlue = Color(0xFF08133A)
private val ElectricBlue = Color(0xFF4CC9FF)
private val NeonCyan = Color(0xFF58F5E7)
private val Violet = Color(0xFF9D6BFF)
private val White = Color(0xFFF4F8FF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(color = SpaceBlack) {
                GalaxyFrontierApp()
            }
        }
    }
}

@Composable
private fun GalaxyFrontierApp() {
    var screen by remember { mutableStateOf(Screen.MENU) }

    AnimatedContent(
        targetState = screen,
        transitionSpec = { fadeIn(tween(350)) togetherWith fadeOut(tween(250)) },
        label = "screen"
    ) { current ->
        when (current) {
            Screen.MENU -> MainMenu(onPlay = { screen = Screen.GAME })
            Screen.GAME -> PrototypeGame(onBack = { screen = Screen.MENU })
        }
    }
}

@Composable
private fun SpaceBackground(modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "stars")
    val drift by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(12000), RepeatMode.Restart),
        label = "drift"
    )

    Canvas(modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                listOf(SpaceBlack, DeepBlue, Color(0xFF09051D))
            )
        )

        val random = Random(42)
        repeat(95) {
            val x = random.nextFloat() * size.width
            val baseY = random.nextFloat() * size.height
            val y = (baseY + drift * 70f * (1f + random.nextFloat())) % size.height
            val radius = 0.7f + random.nextFloat() * 1.5f
            val alpha = 0.25f + random.nextFloat() * 0.7f
            drawCircle(
                color = White.copy(alpha = alpha),
                radius = radius,
                center = androidx.compose.ui.geometry.Offset(x, y)
            )
        }

        val pulse = (sin(drift * Math.PI * 2).toFloat() + 1f) / 2f
        drawCircle(
            brush = Brush.radialGradient(
                listOf(
                    ElectricBlue.copy(alpha = 0.08f + pulse * 0.05f),
                    Color.Transparent
                )
            ),
            radius = size.minDimension * 0.55f,
            center = androidx.compose.ui.geometry.Offset(size.width * 0.82f, size.height * 0.18f)
        )
    }
}

@Composable
private fun MainMenu(onPlay: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        SpaceBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 42.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.weight(0.55f))

            Text(
                text = "GALAXY",
                color = White,
                fontSize = 46.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp
            )
            Text(
                text = "FRONTIER",
                color = NeonCyan,
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 5.sp
            )

            Text(
                text = "A fronteira começa onde o mapa termina.",
                color = White.copy(alpha = 0.62f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp)
            )

            Spacer(Modifier.height(38.dp))

            MenuButton(
                text = "JOGAR",
                icon = Icons.Default.PlayArrow,
                primary = true,
                onClick = onPlay
            )
            Spacer(Modifier.height(12.dp))
            MenuButton(
                text = "CONFIGURAÇÕES",
                icon = Icons.Default.Settings,
                primary = false,
                onClick = {}
            )

            Spacer(Modifier.weight(0.75f))

            Text(
                text = "MOBILE • PROTOTYPE 0.1",
                color = White.copy(alpha = 0.32f),
                fontSize = 10.sp,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
private fun MenuButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    primary: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)
    val background = if (primary) {
        Brush.horizontalGradient(listOf(ElectricBlue.copy(alpha = 0.9f), Violet.copy(alpha = 0.9f)))
    } else {
        Brush.horizontalGradient(listOf(White.copy(alpha = 0.08f), White.copy(alpha = 0.03f)))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(shape)
            .background(background)
            .border(1.dp, White.copy(alpha = if (primary) 0.35f else 0.12f), shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = White, modifier = Modifier.size(21.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                text = text,
                color = White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.6.sp
            )
        }
    }
}

@Composable
private fun PrototypeGame(onBack: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        SpaceBackground()

        Column(Modifier.fillMaxSize().padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = White,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(White.copy(alpha = 0.07f))
                        .clickable(onClick = onBack)
                        .padding(9.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("SETOR 01", color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("ÓRBITA DESCONHECIDA", color = White.copy(alpha = 0.45f), fontSize = 10.sp)
                }
                Spacer(Modifier.weight(1f))
                HudChip("LV 01")
            }

            Spacer(Modifier.height(18.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HudBar("HULL", 0.92f, ElectricBlue, Modifier.weight(1f))
                HudBar("SHIELD", 0.76f, NeonCyan, Modifier.weight(1f))
                HudBar("ENERGY", 0.64f, Violet, Modifier.weight(1f))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                ShipPreview()
            }

            Text(
                "PROTÓTIPO DE COMBATE",
                color = White.copy(alpha = 0.42f),
                fontSize = 10.sp,
                letterSpacing = 1.5.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun HudChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(White.copy(alpha = 0.07f))
            .border(1.dp, White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(text, color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun HudBar(
    label: String,
    value: Float,
    color: Color,
    modifier: Modifier
) {
    Column(modifier) {
        Text(label, color = White.copy(alpha = 0.45f), fontSize = 8.sp, letterSpacing = 1.sp)
        Spacer(Modifier.height(4.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(White.copy(alpha = 0.08f))
        ) {
            Box(
                Modifier
                    .fillMaxWidth(value)
                    .height(5.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(color)
            )
        }
    }
}

@Composable
private fun ShipPreview() {
    val infinite = rememberInfiniteTransition(label = "ship")
    val bob by infinite.animateFloat(
        initialValue = -7f,
        targetValue = 7f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "bob"
    )

    Canvas(Modifier.size(210.dp).alpha(0.98f)) {
        val cx = size.width / 2f
        val cy = size.height / 2f + bob

        drawCircle(
            brush = Brush.radialGradient(
                listOf(ElectricBlue.copy(alpha = 0.22f), Color.Transparent)
            ),
            radius = 92f,
            center = androidx.compose.ui.geometry.Offset(cx, cy + 15f)
        )

        val ship = androidx.compose.ui.geometry.Path().apply {
            moveTo(cx, cy - 76f)
            lineTo(cx - 44f, cy + 48f)
            lineTo(cx - 8f, cy + 34f)
            lineTo(cx, cy + 58f)
            lineTo(cx + 8f, cy + 34f)
            lineTo(cx + 44f, cy + 48f)
            close()
        }

        drawPath(
            ship,
            brush = Brush.verticalGradient(listOf(White, ElectricBlue, Violet))
        )
        drawPath(ship, color = ElectricBlue.copy(alpha = 0.7f), style = Stroke(2.5f))

        drawCircle(
            color = SpaceBlack,
            radius = 13f,
            center = androidx.compose.ui.geometry.Offset(cx, cy - 27f)
        )
        drawCircle(
            color = NeonCyan.copy(alpha = 0.9f),
            radius = 7f,
            center = androidx.compose.ui.geometry.Offset(cx, cy - 27f)
        )
    }
}
