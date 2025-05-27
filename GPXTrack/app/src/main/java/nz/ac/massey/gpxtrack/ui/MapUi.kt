package nz.ac.massey.gpxtrack.ui

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nz.ac.massey.gpxtrack.MapViewModel
import nz.ac.massey.gpxtrack.R
import nz.ac.massey.gpxtrack.SettingsActivity
import nz.ac.massey.gpxtrack.theme.ui.AppTheme
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.JointType
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline

@Composable
fun MapIcon(resource:Int, contentdescription:Int,onclick:()->Unit={}) {
  IconButton(onClick = onclick, modifier=Modifier.padding(0.dp)) {
    Icon(painter=painterResource(resource),
      tint = Color.LightGray,
      contentDescription = stringResource(contentdescription), modifier = Modifier.size(32.dp).padding(0.dp))
  }
}
@Composable
fun MapContent(viewModel: MapViewModel,uistate: MapViewModel.MapUiState) {
  val mapdrawingstate = viewModel.drawingstate.collectAsState()

  if(uistate.colours) {
    Polyline(
      points = mapdrawingstate.value.points,
      spans = mapdrawingstate.value.spans,
      width = uistate.width,
      jointType = JointType.ROUND
    )
  } else {
    Polyline(
      points = mapdrawingstate.value.points,
      width = uistate.width,
      jointType = JointType.ROUND,
      color = Color.Green
    )
  }

    if(uistate.showmaxspeed) {
      Marker(
        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
        state = uistate.maxspeedmarkerstate,
        title = stringResource(R.string.max_speed)
      )
      uistate.maxspeedmarkerstate.showInfoWindow()
    }
    Marker(
      icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
      state = uistate.startmarkerstate,
      title = stringResource(R.string.start)
    )

}

@Preview
@Composable
fun MapUi(modifier: Modifier=Modifier,viewModel: MapViewModel= MapViewModel(), shareFn:()->Unit={}) {

  val uistate = viewModel.mapuistate.collectAsState().value
  val context = LocalContext.current

  AppTheme {
    Scaffold(
      modifier = modifier,
    ) { innerPadding ->
      Box(modifier.fillMaxSize().background(colorResource(R.color.sea_green1))) {
        if (uistate.showmap) {
          GoogleMap(
            cameraPositionState = uistate.camerapositionstate,
            modifier = Modifier.matchParentSize(),
            properties = uistate.mapproperties,
            uiSettings = uistate.mapuisettings,
          ) {
            MapContent(viewModel, uistate)
          }
        }
        Card(
          modifier = modifier.padding(top = innerPadding.calculateTopPadding() + 8.dp, end = 8.dp)
            .align(Alignment.TopEnd),
          colors = CardDefaults.cardColors(
            containerColor = Color(0, 0, 0, 0x40),
          )
        ) {
          if (uistate.showicons)
            Row {
              MapIcon(R.drawable.share_24dp_5f6368_fill0_wght400_grad0_opsz24, R.string.content_share, shareFn)
              MapIcon(R.drawable.settings_24dp_5f6368_fill0_wght400_grad0_opsz24, R.string.content_settings) { context.startActivity(Intent(context, SettingsActivity::class.java)) }
            }
        }
        Box(modifier.padding(innerPadding.calculateBottomPadding()).wrapContentSize(align = Alignment.BottomStart).align(Alignment.BottomStart)) {
          Column(Modifier.fillMaxWidth()) {
            Row {
              if (uistate.showlegend) {
                Image(
                  painter = painterResource(R.drawable.legend),
                  contentDescription = stringResource(R.string.content_legend),
                  modifier = Modifier.padding(8.dp)
                )
              }
              if (uistate.msg != "" && uistate.showdetails) {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  colors = CardDefaults.cardColors(
                    containerColor = Color(0, 0, 0, 0x40),
                  )
                ) {
                  Text(
                    text = uistate.msg, modifier = Modifier.padding(8.dp), color = Color.White,
                    style = TextStyle(
                      fontSize = 18.sp,
                      shadow = Shadow(
                        color = Color.Black, offset = Offset(3.0f, 3.0f)
                      )
                    )
                  )
                }
              }
            }

            Slider(
              modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding() + 16.dp)
                , value = uistate.sliderval, onValueChange = {
                viewModel.sliderchange(it)
              }, onValueChangeFinished = { viewModel.sliderchangedone() }
            )
          }
        }
        }
      }
    }
}
