package nz.massey.roomy

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import nz.massey.roomy.theme.ui.AppTypography


@Composable
fun Course(course: CourseInfo, modifier: Modifier=Modifier, edit:()->Unit){
    Row(modifier=modifier.fillMaxWidth().clickable{edit()}) {
        Text(course.coursename.toString(),modifier=modifier.weight(0.25f))
        Text(course.lecturername.toString(),modifier=modifier.weight(0.25f))
        Text(course.year.toString(),modifier=modifier.weight(0.25f))
        Text(course.semester.toString(),modifier=modifier.weight(0.25f))
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(modifier:Modifier, navController: NavHostController) {
    var showDropDownMenu by remember { mutableStateOf(false) }
    TopAppBar(
        modifier = modifier,
        title = { Text(text = "Uni Roomy") },
        actions = {
            IconButton(onClick = { showDropDownMenu = true }) {
                Icon(Icons.Filled.MoreVert, null)
            }
            DropdownMenu(
                expanded = showDropDownMenu,
                onDismissRequest = { showDropDownMenu = false }
            ) {

                DropdownMenuItem(
                    text = { Text(text = "Lecturers") },
                    onClick = {
                        showDropDownMenu = false
                        navController.navigate("Lecturers")
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = "Courses") },
                    onClick = {
                        showDropDownMenu = false
                        navController.navigate("Courses")
                    }
                )
            }
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOffering (viewmodel: CourseViewModel, navigateBack: () -> Unit, selected: Int =0) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Course Offering") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            var selectedLecturer by remember { mutableIntStateOf(selected) }
            var selectedCourse by remember { mutableIntStateOf(0) }
            val lects = viewmodel.getAllLecturers().collectAsState(emptyList()).value
            val courses = viewmodel.getAllCourses().collectAsState(emptyList()).value
            val selectedlectname =
                if (selectedLecturer < lects.size)
                    lects[selectedLecturer].name.toString()
                else "Select Lecturer"
            Dropdown("Lecturer: ", lects.map { it.name.toString() },
                selectedlectname, modifier = Modifier.padding(16.dp)) { selectedLecturer = it }
            val selectedcoursename =
                if (selectedCourse < courses.size) courses[selectedCourse].name.toString() else "Select Course"
            Dropdown(
                "Course: ",
                courses.map { it.name.toString() },
                selectedcoursename,
                modifier = Modifier.padding(16.dp)
            ) { selectedCourse = it }
            var year by remember { mutableStateOf("2025") }
            OutlinedTextField(
                year.toString(),
                onValueChange = { year = it },
                label = { Text("Year") },
                modifier = Modifier.padding(16.dp),
            )
            var semester by remember { mutableStateOf("1") }
            OutlinedTextField(
                semester.toString(),
                onValueChange = { semester = it },
                label = { Text("Semester") },
                modifier = Modifier.padding(16.dp),
            )
            Button(onClick = {
                viewmodel.newOffering(
                    courseid = courses[selectedCourse].id,
                    lectid = lects[selectedLecturer].id,
                    year=year.toInt(),
                    semester=semester.toInt()
                )
                navigateBack()
            }
            ) { Text("Add") }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOffering (viewmodel: CourseViewModel, navigateBack: () -> Unit, offering: CourseOffering) {
    Scaffold(
        topBar = { CenterAlignedTopAppBar(
            title = { Text("Edit Course Offering",) },
            navigationIcon = { IconButton(onClick = navigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },) },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            val lects = viewmodel.getAllLecturers().collectAsState(emptyList()).value
            val courses = viewmodel.getAllCourses().collectAsState(emptyList()).value
            var selectedLecturerId by remember { mutableLongStateOf(offering.lecturer_id)}
            var selectedCourseId by remember { mutableLongStateOf(offering.course_id)}

            val selectedlect = lects.find { it.id==selectedLecturerId }
            val selectedlectname = selectedlect?.name ?:  "Select Lecturer"
            Dropdown("Lecturer: ", lects.map { it.name.toString() },
                selectedlectname, modifier = Modifier.padding(16.dp)) { selectedLecturerId = lects[it].id }

            val selectedcourse = courses.find { it.id==selectedCourseId }
            val selectedcoursename = selectedcourse?.name ?:  "Select Course"
            Dropdown(
                "Course: ",
                courses.map { it.name.toString() },
                selectedcoursename,
                modifier = Modifier.padding(16.dp)
            ) { selectedCourseId = courses[it].id }
            var year by remember { mutableStateOf(offering.year.toString()) }
            OutlinedTextField(
                year.toString(),
                onValueChange = { year = it },
                label = { Text("Year") },
                modifier = Modifier.padding(16.dp),
            )
            var semester by remember { mutableStateOf(offering.semester.toString()) }
            OutlinedTextField(
                semester.toString(),
                onValueChange = { semester = it },
                label = { Text("Semester") },
                modifier = Modifier.padding(16.dp),
            )
            Button(onClick = {
                viewmodel.updateOffering(offering.id,
                    courseid = selectedCourseId,
                    lectid = selectedLecturerId,
                    year=year.toInt(),
                    semester=semester.toInt()
                )
                navigateBack()
            }
            ) { Text("Update") }
            Button(onClick = {
                viewmodel.deleteOffering(offering.id)
                navigateBack()
            }
            ) { Text("Delete") }
        }
    }

}
@Serializable
data class AddOffering(val selected:Int)

@Composable
fun Nav( ) {
    val navController = rememberNavController()
    val viewModel = CourseViewModel(LocalContext.current)
    NavHost(navController = navController,
        startDestination = "home") {
        composable("home") { Offerings(viewmodel=viewModel, navController=navController) }
//        composable("addOffering/{selected}", arguments = listOf(navArgument("selected") { type = NavType.IntType })) {
//            args -> val selected=args.arguments?.getInt("selected")!!
//            AddOffering(viewmodel =viewModel, navigateBack = { navController.popBackStack(); }, selected = selected) }

        composable<AddOffering>{entry->
            val selected=entry.toRoute<AddOffering>().selected
            AddOffering(viewmodel =viewModel, navigateBack = { navController.popBackStack(); }, selected) }
        composable("addCourse") { AddCourse(viewmodel=viewModel, navigateBack = { navController.popBackStack(); }) }
        composable("addLecturer") { AddLecturer(viewmodel=viewModel, navigateBack = { navController.popBackStack(); }) }
        composable("Lecturers") { Lecturers(viewmodel=viewModel, navController,  navigateBack = { navController.popBackStack(); }) }
        composable("Courses") { Courses(viewmodel=viewModel, navController,  navigateBack = { navController.popBackStack(); }) }
        composable<Lecturer>{ entry ->
            val lect=entry.toRoute<Lecturer>()
            EditLecturer(viewmodel=viewModel, navigateBack = { navController.popBackStack(); }, lecturer=lect)
        }
        composable<Course>{ entry ->
            val course=entry.toRoute<Course>()
            EditCourse(viewmodel=viewModel, navigateBack = { navController.popBackStack(); }, course=course)
        }
        composable<CourseOffering>{ entry ->
            val offering=entry.toRoute<CourseOffering>()
            EditOffering(viewmodel=viewModel, navigateBack = { navController.popBackStack(); }, offering=offering)
        }
    }
}
@Composable
fun Dropdown(label:String, names: List<String>, selected: String, modifier: Modifier=Modifier, onSelected: (Int) -> Unit) {
    var showDropDownMenu by remember { mutableStateOf(false) }
    Row(modifier) {
        Text(label, style = AppTypography.titleLarge)
        Box(modifier = Modifier.fillMaxWidth()) {
            Row {
                Text(selected.toString(), modifier = Modifier.clickable(onClick = { showDropDownMenu = true }),
                    style = AppTypography.titleLarge)
                Icon(Icons.Filled.ArrowDropDown,"Select Value",
                    modifier = Modifier.padding(0.dp).clickable(true, onClick = { showDropDownMenu = true }))
            }

            DropdownMenu(
                onDismissRequest = { showDropDownMenu = false },
                expanded = showDropDownMenu
            ) {
                names.forEachIndexed { index, name ->
                    Text(name, modifier = Modifier.fillMaxWidth().clickable(onClick = {
                                    onSelected(index)
                                    showDropDownMenu = false }).padding(8.dp)
                    )
                }
            }
        }
    }
}
@Composable
fun RowScope.TitleText(t:String) {
    Text(t, style = AppTypography.titleMedium, modifier = Modifier.weight(1f))
}

@Composable
fun Offerings( modifier: Modifier=Modifier, viewmodel: CourseViewModel =CourseViewModel(LocalContext.current), navController: NavHostController) {

    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val lects = viewmodel.getAllLecturers().collectAsState(emptyList()).value
    val selected =
        if (selectedIndex < lects.size) lects[selectedIndex].name.toString() else "Select Lecturer"
    viewmodel.getCourseInfo(selected)
    val courseInfo = viewmodel.getCourseInfo(selected).collectAsState(emptyList()).value
    val offerings = viewmodel.allOfferings().collectAsState(emptyList()).value

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                modifier = modifier.padding(16.dp),
                onClick = { navController.navigate(AddOffering(selectedIndex)) },
                content = { Icon(Icons.Filled.Add, contentDescription = "Add") })
        }, topBar = {
            AppBar(modifier, navController)
        }
    ) { innerPadding ->
        Column(
            modifier = modifier.padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Dropdown("Lecturer: ", lects.map { it.name.toString() }, selected, modifier = Modifier.padding(16.dp))
            { selectedIndex = it }
            Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                TitleText("Course")
                TitleText("Lecturer")
                TitleText("Year")
                TitleText("Semester")
            }
            LazyColumn(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                items(courseInfo.size) {
                    Course(courseInfo[it], edit={navController.navigate(offerings.find({offering->offering.id==courseInfo[it].id})!!)})
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCourse (viewmodel: CourseViewModel, navigateBack: () -> Unit ) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Add Course",
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            var name by remember { mutableStateOf("") }
            var location by remember { mutableStateOf("") }

            OutlinedTextField(
                name.toString(),
                onValueChange = { name = it },
                label = { Text("Course Name") },
                modifier = Modifier.padding(16.dp),
            )
            OutlinedTextField(
                location.toString(),
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.padding(16.dp),
            )
            Button(onClick = {
                viewmodel.newCourse(name=name,location=location)
                navigateBack()
            }
            ) { Text("Add") }
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLecturer (viewmodel: CourseViewModel, navigateBack: () -> Unit ) {
    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Add Lecturer") },
                navigationIcon = { IconButton(onClick = navigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },) },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            var name by remember { mutableStateOf("") }
            var phone by remember { mutableStateOf("") }
            var office by remember { mutableStateOf("") }

            OutlinedTextField(name.toString(), onValueChange = { name = it },
                label = { Text("Lecturer Name") },
                modifier = Modifier.padding(16.dp),)
            OutlinedTextField(phone.toString(), onValueChange = { phone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.padding(16.dp),)
            OutlinedTextField(office.toString(), onValueChange = { office = it },
                label = { Text("Office") },
                modifier = Modifier.padding(16.dp),)
            Button(onClick = { viewmodel.newLecturer(name=name,phone=phone,office=office)
                navigateBack() }) { Text("Add") }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLecturer (viewmodel: CourseViewModel, navigateBack: () -> Unit, lecturer: Lecturer) {

    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Edit Lecturer") },
        navigationIcon = { IconButton(onClick = navigateBack) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },) },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            var name by remember { mutableStateOf(lecturer.name.toString()) }
            var phone by remember { mutableStateOf(lecturer.phone.toString()) }
            var office by remember { mutableStateOf(lecturer.office.toString()) }
            OutlinedTextField(name, onValueChange = { name = it },
                label = { Text("Lecturer Name") },
                modifier = Modifier.padding(16.dp),)
            OutlinedTextField(phone, onValueChange = { phone=it },
                label = { Text("Phone Number") },
                modifier = Modifier.padding(16.dp),)
            OutlinedTextField(office, onValueChange = { office = it },
                label = { Text("Office") },
                modifier = Modifier.padding(16.dp),)
            Button(onClick = { viewmodel.updateLecturer(id=lecturer.id,name=name,
                phone=phone,office=office)
                navigateBack() }) { Text("Update") }
            Button(onClick = { viewmodel.deleteLecturer(id=lecturer.id)
                navigateBack() }) { Text("Delete") }

        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCourse(viewmodel: CourseViewModel, navigateBack: () -> Unit, course: Course) {

    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Edit Course") },
        navigationIcon = { IconButton(onClick = navigateBack) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },) },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            var name by remember { mutableStateOf(course.name.toString()) }
            var location by remember { mutableStateOf(course.location.toString()) }
            OutlinedTextField(name, onValueChange = { name = it },
                label = { Text("Course Name") },
                modifier = Modifier.padding(16.dp),)
            OutlinedTextField(location, onValueChange = { location=it },
                label = { Text("Location") },
                modifier = Modifier.padding(16.dp),)

            Button(onClick = { viewmodel.updateCourse(id=course.id,name=name,
                location=location)
                navigateBack() }) { Text("Update") }
            Button(onClick = { viewmodel.deleteCourse(id=course.id)
                navigateBack() }) { Text("Delete") }

        }
    }
}
@Composable
fun Lecturer(lect: Lecturer, modifier: Modifier=Modifier, edit: (Long) -> Unit = {}) {
    Row(modifier=modifier.fillMaxWidth().clickable {edit(lect.id)})  {
        Text(lect.name.toString(),modifier=modifier.weight(1f))
        Text(lect.phone.toString(),modifier=modifier.weight(1f))
        Text(lect.office.toString(),modifier=modifier.weight(1f))
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Lecturers (viewmodel: CourseViewModel, navController: NavHostController, navigateBack: () -> Unit ) {
    val lects = viewmodel.getAllLecturers().collectAsState(emptyList()).value
    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Lecturers") },

                navigationIcon = { IconButton(onClick = navigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },)},
                      floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(16.dp),
                onClick = { navController.navigate("addLecturer") },
                content = { Icon(Icons.Filled.Add, contentDescription = "Add") })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                TitleText("Name")
                TitleText("Phone Number")
                TitleText("Office")
            }
            LazyColumn(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                items(lects.size) {
                    Lecturer(lects[it], edit= {id ->
                        navController.navigate(lects[it])})
                }
            }
        }
    }
}
@Composable
fun Course(course: Course, modifier: Modifier=Modifier, edit: () -> Unit = {}) {
    Row(modifier=modifier.fillMaxWidth().clickable {edit()})  {
        Text(course.name.toString(),modifier=modifier.weight(1f))
        Text(course.location.toString(),modifier=modifier.weight(1f))
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Courses (viewmodel: CourseViewModel, navController: NavHostController, navigateBack: () -> Unit ) {
    val courses = viewmodel.getAllCourses().collectAsState(emptyList()).value
    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Courses") },
                navigationIcon = { IconButton(onClick = navigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },)},
                      floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(16.dp),
                onClick = { navController.navigate("addCourse") },
                content = { Icon(Icons.Filled.Add, contentDescription = "Add") })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                TitleText("Name")
                TitleText("Location")
            }
            LazyColumn(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                items(courses.size) {
                    Course(courses[it], edit= { navController.navigate(courses[it])})
                }
            }
        }
    }
}
