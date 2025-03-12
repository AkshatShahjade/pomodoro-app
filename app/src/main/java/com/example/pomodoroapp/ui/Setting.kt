package com.example.pomodoroapp.ui

import androidx.annotation.DrawableRes
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

data class Setting (val modifier: Modifier = Modifier,
                    val imageVector: ImageVector? = null,
                    @DrawableRes val drawableRes: Int? = null,
                    val title: String,
                    val description: String? = null,
                    val value:Any?=null,
                    val isDisabled: Boolean = false,
                    val onClick: ()->Unit = {},
                    val isSwitch: Boolean = false)