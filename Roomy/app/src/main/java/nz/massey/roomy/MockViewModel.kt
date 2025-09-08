package nz.massey.roomy

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class MockViewModel() : ViewModel(), CourseInterface {
    val lects=mutableListOf(
            Lecturer(0, "martin", "123", "ms3.88"),
            Lecturer(1, "daniel", "456", "ms3.92"))
    val courses=mutableListOf(
            Course(0, "159236", "alb"),
            Course(1, "159336", "alb"),
            Course(2, "159361", "alb"))
    val offerings=mutableListOf(
            CourseOffering(0, 0, 0, 2025, 2),
            CourseOffering(1, 1, 0, 2025, 2),
            CourseOffering(2, 2, 1, 2025, 2),)

    override fun getAllCourses()= MutableStateFlow(courses)
    override fun getAllLecturers()= MutableStateFlow(lects)
    override fun allOfferings()= MutableStateFlow(offerings)
    override fun getCourseInfo(lect: String): MutableStateFlow<List<CourseInfo>> {
        val info=mutableListOf<CourseInfo>()
        for(o in offerings) {
            if(lects[o.lecturerId.toInt()].name==lect) info.add(CourseInfo(o.id, courses[o.courseId.toInt()].name,lect,o.year,o.semester))
        }
        return MutableStateFlow(info)
    }
    override fun newLecturer(name: String, phone: String, office: String) {
        lects.add(Lecturer(lects.size.toLong(), name, phone, office))
    }
    override fun newCourse(name: String, location: String) {
        courses.add(Course(courses.size.toLong(), name, location))
    }
    override fun updateLecturer(id: Long, name: String, phone: String, office: String) {
        lects[id.toInt()]=Lecturer(id, name, phone, office)
    }

    override fun newOffering(lectid: Long, courseid: Long, year: Int, semester: Int) {
        offerings.add(CourseOffering(offerings.size.toLong(), courseid, lectid, year, semester))
    }

    override fun updateOffering(id: Long, lectid: Long, courseid: Long, year: Int, semester: Int) {
        offerings[id.toInt()]=CourseOffering(id, courseid, lectid, year, semester)
    }

    override fun deleteOffering(id: Long) {
        offerings[id.toInt()]=CourseOffering(id, 0, 0, 0, 0)
    }

    override fun deleteLecturer(id: Long) {
        lects[id.toInt()]=Lecturer(id, "Deleted", "", "")
    }

    override fun deleteCourse(id: Long) {
        courses[id.toInt()]=Course(id, "Deleted", "")
    }

    override fun updateCourse(id: Long, name: String, location: String) {
        courses[id.toInt()]=Course(id, name, location)
    }

}