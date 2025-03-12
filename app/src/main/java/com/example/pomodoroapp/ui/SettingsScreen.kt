package com.example.pomodoroapp.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.pomodoroapp.R
import com.example.pomodoroapp.ui.theme.PomodoroAppTheme

@Composable
fun SettingsScreen(modifier: Modifier = Modifier){
    LazyColumn(
        modifier = modifier,

    ) {
//        item(Setting()){}
    }
}

@Composable
fun SettingMenuItem(modifier: Modifier = Modifier,
                    imageVector: ImageVector? = null,
                    @DrawableRes drawableRes: Int? = null,
                    title: String,
                    description: String? = null,
                    value:Any,
                    isDisabled: Boolean = false,
                    onClick: ()->Unit = {},
                    isSwitch: Boolean = false){
    Row(
        modifier = modifier.fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_small))
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,

    ) {
        if(imageVector!=null){
            Image(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                    .padding(end = dimensionResource(R.dimen.padding_small))
                    ,
                imageVector = imageVector,
                contentDescription = null
            )
        }
        if(drawableRes!=null){
            Image(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                    .padding(end = dimensionResource(R.dimen.padding_small)),
                painter = painterResource(drawableRes),
                contentDescription = null
            )
        }
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                .padding(horizontal = dimensionResource(R.dimen.padding_small))
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            if(description!=null){
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            if(value !is Boolean){
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Spacer(Modifier.weight(1f))

        if(value is Boolean){
            if(isSwitch){
                Switch(
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
                    checked = value,
                    onCheckedChange = null,
                )
            }else{
                Checkbox(
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
                    checked = value,
                    onCheckedChange = null,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview(){
    PomodoroAppTheme (darkTheme = true){
//        SettingsScreen()


        var settingActive by remember{ mutableStateOf(false) }
        SettingMenuItem(
            imageVector = Icons.Filled.Email,
//            drawableRes = R.drawable.img_0818_tiger,
            modifier = Modifier,
            title = "Setting",
//            value = "ak.sh@gm.com"
            value = settingActive,
            isSwitch = true,
            onClick = { settingActive = !settingActive },
            description = "Set the Setting"
        )
    }
}