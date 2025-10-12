package com.example.playlistmaker.settingsjc.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import androidx.compose.material.Switch as SwitchM2
import androidx.compose.material.SwitchDefaults as SwitchDefaultsM2
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.HeadsetMic
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon as IconM3

private val YSDisplay = FontFamily(
    Font(R.font.ys_display_regular, FontWeight.Normal),
    Font(R.font.ys_display_medium,  FontWeight.Medium)
)

@Composable
fun SettingsScreen(
    darkThemeEnabled: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit,
    onShareClick: () -> Unit,
    onSupportClick: () -> Unit,
    onAgreementClick: () -> Unit,
) {
    val bg = colorResource(R.color.color_background)
    val textPrimary = colorResource(R.color.black_setting)
    val rowStart = dimensionResource(R.dimen.margin_start)
    val rowV = dimensionResource(R.dimen.layout_margin_setting)
    val iconSize = dimensionResource(R.dimen.margin_bottom_setting)
    val rowEnd = dimensionResource(R.dimen.margin_setting_end)
    val headerBottom = dimensionResource(R.dimen.margin_bottom_setting)
    val blue = colorResource(R.color.blue)
    val gray = colorResource(R.color.color_gray)
    val white = colorResource(R.color.white)
    val iconTint = if (darkThemeEnabled)
        colorResource(R.color.black_setting)
    else
        colorResource(R.color.color_gray)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .navigationBarsPadding()
    ) {
        Text(
            text = stringResource(R.string.settings),
            style = TextStyle(
                fontFamily = YSDisplay,
                fontWeight = FontWeight.Medium,
                fontSize = 22.sp,
                lineHeight = 22.sp,
                color = textPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = rowStart, top = 16.dp, bottom = 61.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleDarkTheme(!darkThemeEnabled) }
                .padding(start = rowStart, end = rowEnd, bottom = 42.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.black_theme),
                style = TextStyle(
                    fontFamily = YSDisplay,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,          // TextStyle_menu
                    lineHeight = 16.sp,
                    color = textPrimary
                ),
                modifier = Modifier.weight(1f)
            )
            SwitchM2(
                checked = darkThemeEnabled,
                onCheckedChange = onToggleDarkTheme,
                colors = SwitchDefaultsM2.colors(
                    checkedThumbColor = colorResource(R.color.blue),
                    checkedTrackColor = gray.copy(alpha = 1f),
                    uncheckedThumbColor = colorResource(R.color.color_gray),
                    uncheckedTrackColor = gray.copy(alpha = 1f),
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onShareClick)
                .padding(start = rowStart, end = rowEnd, bottom = 42.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.to_share),
                style = TextStyle(
                    fontFamily = YSDisplay,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    color = textPrimary
                ),
                modifier = Modifier.weight(1f)
            )
            IconM3(
                imageVector = Icons.Outlined.Share,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(iconSize)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onSupportClick)
                .padding(start = rowStart, end = rowEnd, bottom = 42.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.to_write),
                style = TextStyle(
                    fontFamily = YSDisplay,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    color = textPrimary
                ),
                modifier = Modifier.weight(1f)
            )
            IconM3(
                imageVector = Icons.Outlined.HeadsetMic,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(iconSize)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onAgreementClick)
                .padding(start = rowStart, end = rowEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.agrement),
                style = TextStyle(
                    fontFamily = YSDisplay,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    color = textPrimary
                ),
                modifier = Modifier.weight(1f)
            )
            IconM3(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}