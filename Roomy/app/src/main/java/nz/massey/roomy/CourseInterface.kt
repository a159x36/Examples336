package nz.massey.roomy

import kotlinx.coroutines.flow.Flow

interface CourseInterface {
    fun getAllCourses(): Flow<List<Course>>
    fun getAllLecturers(): Flow<List<Lecturer>>
    fun allOfferings(): Flow<List<CourseOffering>>
    fun getCourseInfo(lect: String): Flow<List<CourseInfo>>
    fun newLecturer(name: String, phone: String, office: String)
    fun newCourse(name: String, location: String)
    fun updateLecturer(id: Long, name: String, phone: String, office: String)
    fun newOffering(lectid: Long, courseid: Long, year: Int, semester: Int)
    fun updateOffering(id: Long, lectid: Long, courseid: Long, year: Int, semester: Int)
    fun deleteOffering(id: Long)
    fun deleteLecturer(id: Long)
    fun deleteCourse(id: Long)
    fun updateCourse(id: Long, name: String, location: String)
}