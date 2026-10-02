package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.CelebrationType
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

private data class CelebrationParticle(
    val angle: Float,
    val speed: Float,
    val color: Color,
    val sizePx: Float,
    val rotationSpeed: Float,
    val emoji: String? = null
)

@Composable
fun WinCelebrationOverlay(
    celebration: CelebrationType,
    onDismiss: () -> Unit,
    onContinueDoubleUp: (() -> Unit)? = null,
    onCashOutDoubleUp: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Auto-dismiss after 6 seconds if no interaction
    LaunchedEffect(celebration) {
        delay(6000L)
        onDismiss()
    }

    // Modal full-screen dialog covering entire display
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        CelebrationOverlayContent(
            celebration = celebration,
            onDismiss = onDismiss,
            onContinueDoubleUp = onContinueDoubleUp,
            onCashOutDoubleUp = onCashOutDoubleUp,
            modifier = modifier
        )
    }
}

@Composable
fun CelebrationOverlayContent(
    celebration: CelebrationType,
    onDismiss: () -> Unit,
    onContinueDoubleUp: (() -> Unit)? = null,
    onCashOutDoubleUp: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isJackpot = celebration is CelebrationType.Jackpot
    val primaryHue = if (isJackpot) Color(0xFFFFD700) else Color(0xFF00F5D4)
    val secondaryHue = if (isJackpot) Color(0xFFFF1744) else Color(0xFFA855F7)

    // Infinite transitions for rotating rays, pulsing card, and shockwaves
    val infiniteTransition = rememberInfiniteTransition(label = "celebration_rays")
    val rayRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(22000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rayRotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val shockwaveProgress by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shockwave"
    )

    // Entrance scale spring
    val cardEntranceScale = remember { Animatable(0.2f) }
    LaunchedEffect(celebration) {
        cardEntranceScale.snapTo(0.2f)
        cardEntranceScale.animateTo(
            targetValue = 1.0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    // 3D Card flip animation for Double Up
    val cardFlipAnim = remember { Animatable(0f) }
    LaunchedEffect(celebration) {
        if (!isJackpot) {
            cardFlipAnim.snapTo(0f)
            cardFlipAnim.animateTo(
                targetValue = 180f,
                animationSpec = tween(900, easing = FastOutSlowInEasing)
            )
        }
    }

    // Particle system explosion
    val particles = remember(celebration) {
        val emojis = if (isJackpot) {
            listOf("🪙", "🍌", "🎰", "💎", "⭐", "🎁", "🔥")
        } else {
            listOf("🎲", "🪙", "♥️", "♠️", "✨", "2️⃣", "✖️")
        }
        List(55) { index ->
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = Random.nextFloat() * 450f + 120f
            val color = listOf(
                Color(0xFFFFD700), Color(0xFF00F5D4), Color(0xFFFF2A85),
                Color(0xFF00E676), Color(0xFFFF9100), Color(0xFFE040FB),
                Color(0xFFFFFFFF), Color(0xFF76FF03)
            ).random()
            val size = Random.nextFloat() * 10f + 4f
            val rotationSpeed = Random.nextFloat() * 720f - 360f
            val emoji = if (index % 4 == 0) emojis.random() else null
            CelebrationParticle(angle, speed, color, size, rotationSpeed, emoji)
        }
    }

    val particleAnim = remember { Animatable(0f) }
    LaunchedEffect(celebration) {
        particleAnim.snapTo(0f)
        particleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(2800, easing = FastOutSlowInEasing)
        )
    }

    // Animated Coin Counter
    val targetCoins = when (celebration) {
        is CelebrationType.Jackpot -> celebration.winAmount
        is CelebrationType.DoubleUpSuccess -> celebration.newPool
    }
    var animatedCoins by remember { mutableLongStateOf(0L) }
    LaunchedEffect(targetCoins) {
        val duration = 1200L
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < duration) {
            val fraction = ((System.currentTimeMillis() - startTime).toFloat() / duration).coerceIn(0f, 1f)
            animatedCoins = (fraction * targetCoins).toLong()
            delay(30L)
        }
        animatedCoins = targetCoins
    }

    Box(
        modifier = modifier
            .testTag("celebration_overlay")
            .fillMaxSize()
            .background(Color(0xF2070314))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        // LAYER 1: Rotating Sunburst Rays on Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = sqrt((size.width * size.width + size.height * size.height).toDouble()).toFloat()
            val numRays = 18
            val sweepAngle = 360f / (numRays * 2f)

            rotate(rayRotation, center) {
                for (i in 0 until numRays) {
                    val startAngle = i * (360f / numRays)
                    drawArc(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                primaryHue.copy(alpha = 0.22f),
                                secondaryHue.copy(alpha = 0.08f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = maxRadius * 0.75f
                        ),
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true
                    )
                }
            }

            // Shockwave Expanding Halo Rings
            val shockwaveRadius = 120.dp.toPx() * shockwaveProgress
            val shockwaveAlpha = ((1.5f - shockwaveProgress) / 1.0f).coerceIn(0f, 0.45f)
            drawCircle(
                color = primaryHue.copy(alpha = shockwaveAlpha),
                radius = shockwaveRadius,
                center = center
            )
            drawCircle(
                color = secondaryHue.copy(alpha = shockwaveAlpha * 0.6f),
                radius = shockwaveRadius * 0.8f,
                center = center
            )
        }

        // LAYER 2: Confetti, Coin, and Sparkle Particle System
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val p = particleAnim.value

            particles.forEach { particle ->
                val distance = particle.speed * p
                val x = center.x + cos(particle.angle) * distance
                // Add gravity pulling downward over time
                val y = center.y + sin(particle.angle) * distance + (p * p * 260f)
                val alpha = (1f - (p * 0.85f)).coerceIn(0f, 1f)

                if (particle.emoji == null) {
                    // Draw rotated confetti rectangle
                    val rot = particle.rotationSpeed * p
                    rotate(rot, Offset(x, y)) {
                        drawRect(
                            color = particle.color.copy(alpha = alpha),
                            topLeft = Offset(x - particle.sizePx / 2f, y - particle.sizePx / 2f),
                            size = Size(particle.sizePx, particle.sizePx * 1.6f)
                        )
                    }
                } else {
                    // Draw glowing coin/sparkle circle
                    drawCircle(
                        color = particle.color.copy(alpha = alpha),
                        radius = (particle.sizePx + 2f),
                        center = Offset(x, y)
                    )
                }
            }
        }

        // LAYER 3: Central Celebration Victory Card
        Column(
            modifier = Modifier
                .scale(cardEntranceScale.value * pulseScale)
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF220C42),
                            Color(0xFF130628),
                            Color(0xFF090214)
                        )
                    )
                )
                .border(
                    width = 3.dp,
                    brush = Brush.sweepGradient(
                        listOf(
                            primaryHue,
                            secondaryHue,
                            Color(0xFFFFEA00),
                            Color(0xFF00E5FF),
                            primaryHue
                        )
                    ),
                    shape = RoundedCornerShape(26.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Consume clicks to prevent backdrop dismiss */ }
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Close button in top-right corner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .testTag("celebration_close_button")
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2E1065))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("✕", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            when (celebration) {
                is CelebrationType.Jackpot -> {
                    // JACKPOT HEADER
                    Text(
                        text = celebration.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFFFD700),
                        textAlign = TextAlign.Center,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = celebration.subtitle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFE2E8F0),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Giant glowing 3D Jackpot Icon
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFF6B21A8), Color(0xFF2E1065))
                                )
                            )
                            .border(3.dp, Color(0xFFFFD700), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(celebration.symbolEmoji, fontSize = 48.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = celebration.symbolName.uppercase(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )

                    if (celebration.multiplier > 0) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFF1744))
                                .padding(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "KELIPATAN x${celebration.multiplier}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Running Animated Coins Counter
                    Text(
                        text = "+$animatedCoins KOIN",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFFFD700),
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Collect button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .testTag("celebration_collect_button")
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "KLAIM JACKPOT 🪙",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }

                is CelebrationType.DoubleUpSuccess -> {
                    // DOUBLE UP HEADER
                    Text(
                        text = "🎲 2X DOUBLE UP SUCCESS! 🎲",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF00F5D4),
                        textAlign = TextAlign.Center,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "TEBAKAN KARTU TEPAT! Koin Digandakan 2x!",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC084FC),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3D Animated Flipping Card Reveal
                    val rotationY = cardFlipAnim.value
                    Card(
                        modifier = Modifier
                            .size(width = 110.dp, height = 145.dp)
                            .graphicsLayer {
                                this.rotationY = rotationY
                                cameraDistance = 12f * density
                            }
                            .border(2.5.dp, Color(0xFF00F5D4), RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = if (rotationY >= 90f) Color.White else Color(0xFF3B0764)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (rotationY >= 90f) {
                                // Face of the card (counter-rotated so text isn't mirrored)
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer { this.rotationY = 180f },
                                    contentAlignment = Alignment.Center
                                ) {
                                    val cardColor = if (celebration.isRed) Color(0xFFDC2626) else Color(0xFF1E293B)
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(celebration.cardSuit, fontSize = 28.sp)
                                        Text(
                                            text = celebration.cardRank,
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.Black,
                                            color = cardColor
                                        )
                                        Text(
                                            text = if (celebration.isRed) "MERAH ✓" else "HITAM ✓",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = cardColor
                                        )
                                    }
                                }
                            } else {
                                // Back of the card during flip
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🎲", fontSize = 32.sp)
                                    Text("2X", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF00F5D4))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Multiplier & Streak Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF065F46))
                            .border(1.dp, Color(0xFF10B981), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🔥 MENANG RONDE #${celebration.roundStreak} (2X) 🔥",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF6EE7B7)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Doubled Pool Result Display
                    Text(
                        text = "TOTAL HASIL TARUHAN:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE9D5FF)
                    )

                    Text(
                        text = "🪙 $animatedCoins KOIN",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFFFD700),
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "(+${celebration.wonAmount} Koin Bersih Ditambahkan)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF34D399)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons for Double Up: Continue vs Cash Out
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Continue Double Up (Guess again!)
                        Button(
                            onClick = {
                                onContinueDoubleUp?.invoke() ?: onDismiss()
                            },
                            modifier = Modifier
                                .testTag("celebration_continue_button")
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "GANDAKAN LAGI 🎲",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        // Cash Out (Collect coins and close double up)
                        Button(
                            onClick = {
                                onCashOutDoubleUp?.invoke() ?: onDismiss()
                            },
                            modifier = Modifier
                                .testTag("celebration_collect_button")
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "AMBIL KOIN 🪙",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
