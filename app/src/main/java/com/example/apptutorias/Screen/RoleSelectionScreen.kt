package com.example.apptutorias.Screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.draw.shadow
import com.example.apptutorias.R

@Composable
fun RoleSelectionScreen(onRoleSelected: (String) -> Unit) {
    var isExpanding by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf(Color.White) }
    var selectedRole by remember { mutableStateOf<String?>(null) }

    val scale by animateFloatAsState(
        targetValue = if (isExpanding) 30f else 1f,
        animationSpec = tween(durationMillis = 500)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isExpanding) selectedColor else Color.Transparent)
            .scale(scale)
    ) {
        if (!isExpanding) {
            Image(
                painter = painterResource(id = R.drawable.fondo_roles),
                contentDescription = "Fondo de Roles",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RoleCard(
                    title = "Soy Estudiante",
                    emoji = "🎓",
                    backgroundColor = Color(0xFFBBDEFB),
                    onClick = {
                        selectedColor = Color(0xFFBBDEFB)
                        selectedRole = "student"
                        isExpanding = true
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
                RoleCard(
                    title = "Soy Tutor",
                    emoji = "👨‍🏫",
                    backgroundColor = Color(0xFFC8E6C9),
                    onClick = {
                        selectedColor = Color(0xFFC8E6C9)
                        selectedRole = "tutor"
                        isExpanding = true
                    }
                )
            }
        }
    }

    LaunchedEffect(isExpanding) {
        if (isExpanding && selectedRole != null) {
            delay(500) // duración de la animación
            onRoleSelected(selectedRole!!)
        }
    }
}

@Composable
fun RoleCard(
    title: String,
    emoji: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .shadow(8.dp, shape = RoundedCornerShape(16.dp))
            .background(backgroundColor, shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                color = Color.Black,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = emoji,
                fontSize = 36.sp,
                textAlign = TextAlign.End
            )
        }
    }
}
