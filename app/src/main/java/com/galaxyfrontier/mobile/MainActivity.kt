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
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import kotlin.math.sqrt
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
    var shipX by remember { mutableFloatStateOf(0.5f) }
    var shipY by remember { mutableFloatStateOf(0.72f) }
    var enemyX by remember { mutableFloatStateOf(0.5f) }
    var enemyY by remember { mutableFloatStateOf(0.25f) }
    var enemyHp by remember { mutableIntStateOf(3) }
    var hull by remember { mutableFloatStateOf(1f) }
    var shield by remember { mutableFloatStateOf(1f) }
    var shots by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf("INIMIGO DETECTADO") }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(35)
            enemyX += if (enemyX < shipX) 0.0025f else -0.0025f
            enemyX = enemyX.coerceIn(0.16f, 0.84f)
            enemyY += 0.0007f
            if (enemyY > 0.58f) {
                enemyY = 0.18f
                enemyX = Random.nextFloat() * 0.68f + 0.16f
                shield = (shield - 0.08f).coerceAtLeast(0f)
                if (shield <= 0f) hull = (hull - 0.06f).coerceAtLeast(0f)
                message = if (hull <= 0f) "NAVE DANIFICADA" else "ALERTA"
            }
        }
    }

    fun fire() {
        shots++
        val distance = sqrt(
            ((shipX - enemyX) * (shipX - enemyX)) +
                ((shipY - enemyY) * (shipY - enemyY))
        )
        if (distance < 0.32f) {
            enemyHp--
            message = "IMPACTO!"
            if (enemyHp <= 0) {
                enemyHp = 3
                enemyX = Random.nextFloat() * 0.68f + 0.16f
                enemyY = 0.18f
                message = "ALVO DESTRUÍDO"
            }
        } else {
            message = "DISPARO"
        }
    }

    Box(Modifier.fillMaxSize()) {
        SpaceBackground()

        Column(Modifier.fillMaxSize().padding(14.dp)) {
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
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("SETOR 01", color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("ÓRBITA DESCONHECIDA", color = White.copy(alpha = 0.45f), fontSize = 10.sp)
                }
                Spacer(Modifier.weight(1f))
                HudChip("LV 01")
            }

            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HudBar("HULL", hull, ElectricBlue, Modifier.weight(1f))
                HudBar("SHIELD", shield, NeonCyan, Modifier.weight(1f))
                HudBar("ENERGY", 0.82f, Violet, Modifier.weight(1f))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            shipX = (shipX + dragAmount.x / size.width).coerceIn(0.12f, 0.88f)
                            shipY = (shipY + dragAmount.y / size.height).coerceIn(0.30f, 0.86f)
                        }
                    }
            ) {
                CombatField(
                    shipX = shipX,
                    shipY = shipY,
                    enemyX = enemyX,
                    enemyY = enemyY,
                    enemyHp = enemyHp
                )

                Text(
                    message,
                    color = if (message == "ALVO DESTRUÍDO") NeonCyan else White.copy(alpha = 0.72f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.8.sp,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 14.dp)
                )

                Text(
                    "ARRASTE PARA PILOTAR",
                    color = White.copy(alpha = 0.35f),
                    fontSize = 9.sp,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HudChip("TIROS $shots")
                Box(
                    modifier = Modifier
                        .size(78.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(NeonCyan.copy(alpha = 0.42f), Violet.copy(alpha = 0.30f))
                            )
                        )
                        .border(2.dp, NeonCyan.copy(alpha = 0.55f), RoundedCornerShape(26.dp))
                        .clickable { fire() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("ATIRAR", color = White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
                HudChip("ALVO x$enemyHp")
            }
        }
    }
}

@Composable
private fun CombatField(
    shipX: Float,
    shipY: Float,
    enemyX: Float,
    enemyY: Float,
    enemyHp: Int
) {
    val infinite = rememberInfiniteTransition(label = "combatFx")
    val pulse by infinite.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(550), RepeatMode.Reverse),
        label = "pulse"
    )

    Canvas(Modifier.fillMaxSize()) {
        val ship = Offset(size.width * shipX, size.height * shipY)
        val enemy = Offset(size.width * enemyX, size.height * enemyY)

        repeat(12) { i ->
            val x = size.width * (0.08f + i * 0.08f)
            drawLine(
                color = ElectricBlue.copy(alpha = 0.035f),
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1f
            )
        }

        // Energy field around the enemy.
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Violet.copy(alpha = 0.28f), Color.Transparent)
            ),
            radius = 68f * pulse,
            center = enemy
        )

        // Enemy ship.
        val enemyPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(enemy.x, enemy.y - 32f)
            lineTo(enemy.x - 27f, enemy.y + 20f)
            lineTo(enemy.x, enemy.y + 12f)
            lineTo(enemy.x + 27f, enemy.y + 20f)
            close()
        }
        drawPath(enemyPath, brush = Brush.verticalGradient(listOf(Color(0xFFFF718C), Violet)))
        drawPath(enemyPath, color = White.copy(alpha = 0.65f), style = Stroke(2f))

        // Enemy health pips.
        repeat(enemyHp) { i ->
            drawRoundRect(
                color = NeonCyan,
                topLeft = Offset(enemy.x - 25f + i * 17f, enemy.y - 48f),
                size = Size(13f, 4f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f)
            )
        }

        // Target reticle.
        drawCircle(
            color = NeonCyan.copy(alpha = 0.42f),
            radius = 43f,
            center = enemy,
            style = Stroke(1.5f)
        )
        drawLine(
            color = NeonCyan.copy(alpha = 0.65f),
            start = Offset(enemy.x - 53f, enemy.y),
            end = Offset(enemy.x - 34f, enemy.y)
        )
        drawLine(
            color = NeonCyan.copy(alpha = 0.65f),
            start = Offset(enemy.x + 34f, enemy.y),
            end = Offset(enemy.x + 53f, enemy.y)
        )

        // Player ship glow.
        drawCircle(
            brush = Brush.radialGradient(listOf(NeonCyan.copy(alpha = 0.28f), Color.Transparent)),
            radius = 38f,
            center = ship
        )

        val shipPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(ship.x, ship.y - 38f)
            lineTo(ship.x - 25f, ship.y + 25f)
            lineTo(ship.x - 5f, ship.y + 18f)
            lineTo(ship.x, ship.y + 31f)
            lineTo(ship.x + 5f, ship.y + 18f)
            lineTo(ship.x + 25f, ship.y + 25f)
            close()
        }
        drawPath(shipPath, brush = Brush.verticalGradient(listOf(White, ElectricBlue, Violet)))
        drawPath(shipPath, color = NeonCyan, style = Stroke(2f))

        // Engine trails.
        drawLine(
            color = ElectricBlue.copy(alpha = 0.8f),
            start = Offset(ship.x - 8f, ship.y + 25f),
            end = Offset(ship.x - 8f, ship.y + 48f),
            strokeWidth = 5f
        )
        drawLine(
            color = Violet.copy(alpha = 0.75f),
            start = Offset(ship.x + 8f, ship.y + 25f),
            end = Offset(ship.x + 8f, ship.y + 48f),
            strokeWidth = 5f
        )

        drawCircle(
            color = SpaceBlack,
            radius = 7f,
            center = Offset(ship.x, ship.y - 17f)
        )
        drawCircle(
            color = White,
            radius = 3.5f,
            center = Offset(ship.x, ship.y - 17f)
        )
    }
}

