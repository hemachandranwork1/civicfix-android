package com.civicfix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civicfix.ui.theme.*

@Composable
fun CivicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(leadingIcon, null, tint = CivicMuted, modifier = Modifier.size(18.dp)) },
        trailingIcon = if (isPassword) {{
            IconButton(onClick = { onTogglePassword?.invoke() }) {
                Icon(
                    if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    null, tint = CivicMuted, modifier = Modifier.size(18.dp)
                )
            }
        }} else null,
        visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = CivicAccent,
            unfocusedBorderColor = CivicBorder,
            focusedLabelColor    = CivicAccent,
            unfocusedLabelColor  = CivicMuted,
            focusedTextColor     = Color.White,
            unfocusedTextColor   = Color.White,
            cursorColor          = CivicAccent,
            focusedContainerColor   = CivicDeepBlue,
            unfocusedContainerColor = CivicDeepBlue
        ),
        singleLine = true
    )
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (enabled)
                        Brush.horizontalGradient(listOf(CivicAccent, CivicGreen))
                    else
                        Brush.horizontalGradient(listOf(CivicMuted, CivicMuted)),
                    RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = CivicNavy)
        }
    }
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold,
        color = Color.White, modifier = modifier)
}
