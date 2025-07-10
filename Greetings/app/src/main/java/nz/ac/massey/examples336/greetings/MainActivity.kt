package nz.ac.massey.examples336.greetings

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nz.ac.massey.examples336.greetings.ui.theme.GreetingsTheme
import nz.ac.massey.examples336.greetings.ui.theme.Typography

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
        //    Greeting30()

            GreetingsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }


        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GreetingsTheme {
        Greeting("Android")
    }
}

@Preview(showBackground = true)
@Composable
fun Greeting1(modifier: Modifier = Modifier) {
    Text(
        text = "Hello 159336!",
        modifier = modifier,
        color = Color.Red,
        style = Typography.headlineLarge
    )
}

@Preview(showBackground = true)
@Composable
fun Greeting2(modifier: Modifier = Modifier) {
    Text( text = "Hello 159336!",
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Green)
            .border(10.dp, Color.Red)
            .padding(24.dp)
            .background(Color.Yellow)
            .alpha(0.8f))
}

@Preview(showBackground = true)
@Composable
fun Greeting3( modifier: Modifier = Modifier) {
    Column(modifier = modifier.width(200.dp)) {
        Text("Hello 159336",Modifier.align(Alignment.CenterHorizontally))
        Text("More Text")
        Row {
            Text("Text 1 ")
            Text("Text 2 ")
            Text("Text 3 ")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Greeting4(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Hello 159336",modifier.align(Alignment.Start))
        Text("More Text")
        Row(modifier = modifier.height(64.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text("1",modifier.width(64.dp).align(Alignment.Top))
            Text("2",modifier.width(64.dp))
            Text("3",modifier.width(64.dp).align(Alignment.Bottom))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Greeting5() {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly) {
        Text("1")
        Text("2")
        Text("3")
    }
}

@Preview(showBackground = true)
@Composable
fun Greeting6() {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text("1",Modifier.weight(3f))
        Text("2",Modifier.weight(2f))
        Text("3",Modifier.weight(1f))
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Greetingscroll() {
val scrollstate = rememberScrollState()
    Scaffold { padding ->
        Column(Modifier.verticalScroll(scrollstate).padding(padding)) {
            for (i in 1..100) {
                Text("Row $i", modifier = Modifier.height(32.dp).fillMaxWidth())
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun Greeting7() {
    Column {
        for (j in 1..4) {
            Row(Modifier
                .fillMaxWidth()
                .weight(1f)
                .align(Alignment.CenterHorizontally)) {
                for (i in 1..4) {
                    Text("($i,$j)",Modifier
                        .padding(8.dp)
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                        textAlign = TextAlign.Center )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Greeting8() {
    Box(Modifier.fillMaxSize()) {
        Text("1", fontSize = 64.sp,
            modifier = Modifier.align(Alignment.TopCenter))
        Text("2", fontSize = 64.sp,
            modifier = Modifier.align(Alignment.BottomCenter))
        Text("3", fontSize = 64.sp,
            modifier = Modifier.align(Alignment.CenterEnd))
        Text("4", fontSize = 64.sp,
            modifier = Modifier.align(Alignment.CenterStart))
        Text("5", fontSize = 64.sp,
            modifier = Modifier.align(Alignment.Center))
    }
}

@Preview(showBackground = true)
@Composable
fun Greeting9(modifier: Modifier = Modifier) {
    val context=LocalContext.current
    Column(modifier=modifier) {
        Button(onClick ={
            context.startActivity(
					Intent(Intent.ACTION_DIAL, "tel:777".toUri()))
        }) {
            Text("Dial")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Greeting10(modifier: Modifier = Modifier) {
    Card(modifier = modifier
        .fillMaxWidth()
        .padding(32.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column {
            Text("Accept Agreement", fontSize = 24.sp, modifier =
                Modifier.align(Alignment.CenterHorizontally))
            Text("\nLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
            Button(onClick = {},modifier = Modifier.align(Alignment.End)) {Text("Ok")}
        }
    }
}

val clicksState = mutableStateOf(0)
@Preview (showBackground = true)
@Composable
fun Greeting11() {
    Column {
        Text("${clicksState.value} Clicks")
        Button(onClick = {clicksState.value++}){Text("Click Me")}
    }
}

var clicksDelegate by mutableStateOf(0)
@Preview (showBackground = true)
@Composable
fun Greeting12() {
    Column {
        Text("$clicksDelegate Clicks")
        Button(onClick = {clicksDelegate++}){Text("Click Me")}
    }
}

@Preview (showBackground = true)
@Composable
fun Greeting13() {
    var clicks by remember { mutableStateOf(0) }
    Column {
        Text("$clicks Clicks")
        Button(onClick = { clicks++ }) { Text("Click Me") }
    }
}

val clicksStateFlow= MutableStateFlow(0)
@Preview (showBackground = true)
@Composable
fun Greeting14() {
    val cl=clicksStateFlow.collectAsStateWithLifecycle().value
    Column {
        Text("$cl Clicks")
        Button(onClick = { clicksStateFlow.value++ }) { Text("Click Me") }
    }
}

data class AppState(val p1:Int,val p2:String)
var state = MutableStateFlow(AppState(0,""))
fun updateState(newp1:Int,newp2:String) {
    state.update{st->st.copy(p1=newp1,p2=newp2)}
}
@Composable
fun Clicker(onClick: () -> Unit) {
    val st=state.collectAsStateWithLifecycle().value
    Column {
        Text("${st.p1} ${st.p2}")
        Button(onClick = onClick) { Text("Click Me") }}
}
@Preview (showBackground = true)
@Composable
fun Greeting15() = Clicker {
    updateState(state.value.p1 + 1, state.value.p2 + "!")
}

@Preview (showBackground = true)
@Composable
fun Greeting16() {
    var name by remember{ mutableStateOf("") }
    Column {
        TextField(name,onValueChange={name=it})
        Button(onClick = {}) { Text("Submit") }
    }
}

@Preview (showBackground = true)
@Composable
fun Greeting17() {
    var name by remember{ mutableStateOf("") }
    Column {
        OutlinedTextField(name,onValueChange={name=it},
            label={ Text("Name")})
        Button(onClick = {}) { Text("Submit") }
    }
}

val mylist = mutableStateListOf("a","b","c")
@Preview (showBackground = true)
@Composable
fun Greeting18() {
    Column {
        mylist.forEachIndexed { i, l ->
            Button(onClick = {
                //mylist[i] += "!" // works
                mylist.remove(l) // also works
                println(mylist)
            }) { Text(l) }
        }
    }
}

var hintVisible by mutableStateOf(false)
@Preview (showBackground = true)
@Composable
fun Greeting19() {
    Column(Modifier.fillMaxSize()) {
        AnimatedVisibility(hintVisible) {
            Text("Here is a Handy Hint",
                style = Typography.headlineLarge)
        }
        Button(onClick = {hintVisible = !hintVisible}) {
            Text("Show Hint") }
    }
}

@Preview (showBackground = true)
@Composable
fun Greeting20() {
    val alpha by animateFloatAsState(targetValue = if (hintVisible) 1f else 0f, animationSpec = tween(2000))
    Column(Modifier.fillMaxSize()) {
        Text("Here is a Handy Hint",
                style = Typography.headlineLarge,
            modifier=Modifier.alpha(alpha)
        )
        Button(onClick = {hintVisible = !hintVisible}) {
            Text("Show Hint") }
    }
}

@Preview (showBackground = true)
@Composable
fun Greeting21() {
    val angle by animateFloatAsState(
        targetValue = if(hintVisible) 0f else 180f,
        animationSpec = tween(2000, easing= LinearOutSlowInEasing))
    Column(Modifier.fillMaxSize()) {
        Text("Here is a Handy Hint",
            style = Typography.headlineLarge,
            modifier=Modifier.graphicsLayer(rotationZ = angle, translationX = angle*8)
        )

        Button(onClick = {hintVisible = !hintVisible}) {
            Text("Show Hint") }
    }
}


@Preview (showBackground = true)
@Composable
fun Greeting22() {
    val offset by animateFloatAsState(
        targetValue = if(hintVisible) 0f else 1000f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow))
    Column(Modifier.fillMaxSize()) {
        Text("Here is a Handy Hint",
            style = Typography.headlineLarge,
            modifier=Modifier.graphicsLayer(translationX = -offset)
        )

        Button(onClick = {hintVisible = !hintVisible}) {
            Text("Show Hint") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview (showBackground = true)
@Composable
fun Greeting23() {
    var showmenu by remember { mutableStateOf(false) }
    GreetingsTheme {
        Scaffold(
            topBar = { TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                navigationIcon = { IconButton({/*TODO*/}) {Icon(Icons.Default.Menu,
                    contentDescription = null)}},
                actions={
                    IconButton({/*TODO*/}) {
                        Icon(Icons.Default.Share,
                            contentDescription = stringResource(R.string.share))
                    }
                    IconButton(onClick = {showmenu=!showmenu}) {
                        Icon(Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.open_menu)
                        )
                    }
                    DropdownMenu(showmenu, {showmenu=false}) {
                        DropdownMenuItem(text={Text(stringResource(R.string.delete))},
                            onClick = { /*TODO*/ showmenu=false },
                            leadingIcon = { Icon(Icons.Default.Delete,null)})
                        DropdownMenuItem(text={Text(stringResource(R.string.settings))},
                            onClick = { /*TODO*/ showmenu=false},
                            leadingIcon = { Icon(Icons.Default.Settings,null)})
                    }
                }
            )}
        ) { innerPadding ->
            Greeting(name = "159336", modifier = Modifier.padding(innerPadding))
        }
    }
}

@Preview (showBackground = true)
@Composable
fun Greeting24() {
    val context = LocalContext.current
    Button({
        Toast.makeText(context, "Here is a Toast", Toast.LENGTH_SHORT).show()
    }) { Text("Toast Me") }
}

@Preview (showBackground = true)
@Composable
fun Greeting25() {
    val scope=rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    GreetingsTheme {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { innerPadding ->
            Button({
                scope.launch {
                    val result=snackbarHostState.showSnackbar("Would you like a Snack?",
                        actionLabel = "Yes",
                        withDismissAction = true
                    )
                    if(result==SnackbarResult.ActionPerformed)
                        snackbarHostState.showSnackbar("Here is your Snack")
                }
            },modifier=Modifier.padding(innerPadding)) { Text("Snack Me") }
        }
    }
}

@Preview (showBackground = true)
@Composable
fun Greeting26() {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        AlertDialog(
            icon = { Icon(Icons.Default.Person, null) },
            title = { Text(text = "Dialog") },
            text = { Text(text = "Here is a Dialog") },
            onDismissRequest = { showDialog = false },
            confirmButton = { Button(onClick = {})
                { Text("OK") } },
            dismissButton = { Button(onClick = { showDialog = false })
                { Text("Close") } })
    }
    Button({showDialog=true}) { Text("Show Dialog") }
}

@Preview (showBackground = true, showSystemUi = true)
@Composable
fun Greeting27() {
    Scaffold { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(10000) { index ->
                Text("Item $index",modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Preview (showBackground = true, showSystemUi = true)
@Composable
fun Greeting28() {
    Scaffold { innerPadding ->
        LazyVerticalGrid(modifier = Modifier.padding(innerPadding), columns = GridCells.Fixed(10)) {
            items(Int.MAX_VALUE) { index ->
                Text("Item $index",modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Preview (showBackground = true, showSystemUi = true)
@Composable
fun Greeting29() {
    val offset = remember { mutableStateOf(IntOffset(0,0)) }
    Scaffold { innerPadding ->
        Text("Drag Me",Modifier.padding(innerPadding)
            .offset{offset.value}
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                        change.consume()
                        offset.value= IntOffset(
                            offset.value.x+dragAmount.x.toInt(),
                            offset.value.y+dragAmount.y.toInt())
                }}
            .background(Color.Yellow)
            .padding(48.dp)
        )
    }
}

@Preview (showBackground = true, showSystemUi = true)
@Composable
fun Greeting30() {
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }
    Scaffold { innerPadding ->
        Text("Drag Me",Modifier.padding(innerPadding)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotation,
                translationX = offset.x,
                translationY = offset.y
            )
            .transformable(state = state)
            .background(Color.Yellow)
            .padding(48.dp)
        )
    }
}
@Preview (showBackground = true, showSystemUi = true)
@Composable
fun Greeting31() {
    val pagerState = rememberPagerState(pageCount = { 3 }, initialPage = 1)
    val coroutineScope = rememberCoroutineScope()
    Scaffold { innerPadding ->
        Box(Modifier.padding(innerPadding).fillMaxSize().background(Color.Yellow)) {
            Column {
                Row (Modifier.fillMaxWidth()){
                    Button(modifier = Modifier.weight(1f), onClick = { coroutineScope.launch {  pagerState.animateScrollToPage(0)}}) {Text("Page 1")}
                    Button(modifier = Modifier.weight(1f), onClick = { coroutineScope.launch {  pagerState.animateScrollToPage(1)} }) {Text("Page 2")}
                    Button(modifier = Modifier.weight(1f), onClick = {coroutineScope.launch {  pagerState.animateScrollToPage(2)}}) {Text("Page 3")}
                }
                HorizontalPager(
                    state = pagerState, modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> Greeting7()
                        1 -> Greeting8()
                        2 -> Greeting10()
                    }
                }
            }
        }
    }
}
@Preview (showBackground = true)
@Composable
fun Greeting32() {
    Text("Greetings", fontSize = 64.sp, modifier = Modifier.drawBehind {
        drawCircle(Color.Green)
    })
}

@Preview(showBackground = true)
@Composable
fun Greeting33() {
    val text = "Greetings"
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = Modifier.fillMaxSize()) {
        val measuredText = textMeasurer.measure(text, style = TextStyle(fontSize = 36.sp))
        drawCircle(Color.Green, radius = 300f)
        drawText(measuredText, topLeft = Offset(size.width/2-measuredText.size.width/2,size.height/2-measuredText.size.height/2))
    }
}







