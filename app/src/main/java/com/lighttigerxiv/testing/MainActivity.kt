package com.lighttigerxiv.testing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lighttigerxiv.testing.ui.theme.TestingTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMotionApi::class, ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestingTheme {

                val sliderValue = remember { mutableStateOf(0f) }
                val context = LocalContext.current
                val playerSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
                val playerSheetScaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = playerSheetState)
                val navController = rememberNavController()


                fun goToScreen(screen: String){
                    navController.navigate(screen) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }


                LaunchedEffect(playerSheetState){
                    snapshotFlow{playerSheetState.progress.fraction}
                        .collect{

                            if(it in 0.1f..1f){
                                if(playerSheetState.targetValue == BottomSheetValue.Expanded){
                                    sliderValue.value = 0 + it
                                } else{
                                    sliderValue.value = 1 - it
                                }
                            }

                        }
                }





                    MotionLayout(
                        modifier = Modifier.defaultMinSize(minHeight = 55.dp),
                        motionScene = MotionScene(
                            content = context.resources
                                .openRawResource(R.raw.hide_navbar_motion)
                                .readBytes()
                                .decodeToString()
                        ),
                        progress = sliderValue.value
                    ) {

                        val playerProperties = motionProperties(id = "player")
                        val navbarProperties = motionProperties(id = "navbar")

                        BottomSheetScaffold(
                            modifier = Modifier
                                .fillMaxSize()
                                .layoutId("player"),
                            scaffoldState = playerSheetScaffoldState,
                            sheetContent = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(8.dp)
                                ) {

                                    if(playerSheetState.isCollapsed && playerSheetState.progress.fraction == 1f && playerSheetState.targetValue == BottomSheetValue.Collapsed){
                                        Text(text = "Player")
                                    }
                                    else{
                                        Text(text = "Big Player")
                                    }
                                }
                            },
                            sheetPeekHeight = 110.dp
                        ) {scaffoldPadding->

                            NavHost(
                                navController = navController,
                                startDestination = "home",
                                modifier = Modifier.padding(scaffoldPadding)
                            ){

                                composable("home"){

                                    ScreenWithText(text = "HOME")
                                }

                                composable("settings"){

                                    ScreenWithText(text = "SETTINGS")
                                }

                                composable("add"){

                                    ScreenWithText(text = "ADD")
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .layoutId("navbar")
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {

                            Button(onClick = {goToScreen("home")}) {
                                Text(text = "home")
                            }

                            Button(onClick = {goToScreen("add")}) {
                                Text(text = "add")
                            }

                            Button(onClick = {goToScreen("settings")}) {
                                Text(text = "settings")
                            }
                        }
                    }

            }
        }
    }
}

@Composable
fun ScreenWithText(text: String){

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = text, fontSize = 40.sp)
    }
}