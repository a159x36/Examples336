package nz.massey.roomy

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nz.massey.roomy.UniDatabase.Companion.getDatabase

class CourseViewModel(context:Context) : ViewModel() {
    val mDao: UniDao
    init {
        val db = getDatabase(context)
        mDao = db.UniDao()
    }


    fun getAllCourses() = mDao.allCourses()

    fun getAllLecturers() = mDao.allLecturers()


    fun getCourseInfo(lect: String) = mDao.getCourseInfo(lect)

    fun newOffering(lectid: Long, courseid: Long, year: Int, semester: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            mDao.insert(CourseOffering(course_id = courseid, lecturer_id = lectid, year = year, semester = semester))
        }
    }

    fun deleteOffering(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            mDao.deleteOffering(id = id)
        }
    }

    fun allOfferings() = mDao.allOfferings()

    fun updateOffering(id: Long, lectid: Long, courseid: Long, year: Int, semester: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            mDao.updateOffering(id = id, course_id = courseid, lecturer_id = lectid, year = year, semester = semester)
        }
    }

    fun newCourse( name: String, location: String) {
        CoroutineScope(Dispatchers.IO).launch {
            mDao.insert(Course(name = name, location = location))
        }
    }

    fun newLecturer(name: String, phone: String, office: String) {
        CoroutineScope(Dispatchers.IO).launch {
            mDao.insert(Lecturer(name = name, phone = phone, office = office))
        }
    }

    fun updateLecturer (id: Long, name: String, phone: String, office: String) {
        CoroutineScope(Dispatchers.IO).launch {
            mDao.updateLecturer(id = id, name = name, phone = phone, office = office)
        }
    }

    fun updateCourse (id: Long, name: String, location: String) {
        CoroutineScope(Dispatchers.IO).launch {
            mDao.updateCourse(id = id, name = name, location = location)
        }
    }

    fun deleteLecturer (id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            mDao.deleteLecturer(id = id)
        }
    }

    fun deleteCourse (id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            mDao.deleteCourse(id = id)
        }
    }

}