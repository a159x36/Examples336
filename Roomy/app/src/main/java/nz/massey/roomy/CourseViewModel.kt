package nz.massey.roomy

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nz.massey.roomy.UniDatabase.Companion.getDatabase

@Suppress("UNCHECKED_CAST")
class CourseViewModelFactory(val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CourseViewModel(context) as T
    }
}

class CourseViewModel(context:Context) : ViewModel(), CourseInterface {
    val mDao: UniDao
    init {
        val db = getDatabase(context)
        mDao = db.UniDao()
    }

    override fun getAllCourses() = mDao.allCourses()

    override fun getAllLecturers() = mDao.allLecturers()

    override fun getCourseInfo(lect: String) = mDao.getCourseInfo(lect)

    override fun newOffering(lectid: Long, courseid: Long, year: Int, semester: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            mDao.insert(CourseOffering(courseId = courseid, lecturerId = lectid, year = year, semester = semester))
        }
    }

    override fun deleteOffering(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            mDao.deleteOffering(id = id)
        }
    }

    override fun allOfferings() = mDao.allOfferings()

    override fun updateOffering(id: Long, lectid: Long, courseid: Long, year: Int, semester: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            mDao.updateOffering(id = id, courseId = courseid, lecturerId = lectid, year = year, semester = semester)
        }
    }

    override fun newCourse(name: String, location: String) {
        CoroutineScope(Dispatchers.IO).launch {
            mDao.insert(Course(name = name, location = location))
        }
    }

    override fun newLecturer(name: String, phone: String, office: String) {
        CoroutineScope(Dispatchers.IO).launch {
            mDao.insert(Lecturer(name = name, phone = phone, office = office))
        }
    }

    override fun updateLecturer (id: Long, name: String, phone: String, office: String) {
        CoroutineScope(Dispatchers.IO).launch {
            mDao.updateLecturer(id = id, name = name, phone = phone, office = office)
        }
    }

    override fun updateCourse (id: Long, name: String, location: String) {
        CoroutineScope(Dispatchers.IO).launch {
            mDao.updateCourse(id = id, name = name, location = location)
        }
    }

    override fun deleteLecturer (id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            mDao.deleteLecturer(id = id)
        }
    }

    override fun deleteCourse (id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            mDao.deleteCourse(id = id)
        }
    }

}