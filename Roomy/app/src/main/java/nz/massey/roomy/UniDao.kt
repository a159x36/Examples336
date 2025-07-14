package nz.massey.roomy

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UniDao {
    @Insert
    fun insert(course: Course): Long

    @Query("SELECT * FROM course")
    fun allCourses(): Flow<List<Course>>

    @Query("SELECT * FROM lecturer")
    fun allLecturers(): Flow<List<Lecturer>>

    @Insert
    fun insert(lect: Lecturer): Long

    @Query("DELETE FROM courseoffering")
    fun deleteAllOfferings()

    @Query("DELETE FROM courseoffering WHERE id=:id")
    fun deleteOffering(id: Long)

    @Query("SELECT * FROM courseoffering where id=:id")
    fun getOffering(id: Long): CourseOffering

    @Query("SELECT * FROM courseoffering")
    fun allOfferings(): Flow<List<CourseOffering>>

    @Insert
    fun insert(co: CourseOffering): Long

    @Query("DELETE FROM lecturer WHERE id=:id")
    fun deleteLecturer(id: Long)

    @Query("UPDATE lecturer SET name=:name, phone=:phone, office=:office where id=:id")
    fun updateLecturer(id: Long, name: String, phone: String, office: String)

    @Query("DELETE FROM course WHERE id=:id")
    fun deleteCourse(id: Long)

    @Query("UPDATE course SET name=:name, location=:location where id=:id")
    fun updateCourse(id: Long, name: String, location: String)

    @Query("SELECT courseoffering.id as id, course.name AS coursename, lecturer.name AS lecturername, courseoffering.year, courseoffering.semester FROM course,courseoffering,lecturer WHERE lecturer.name=:lect AND lecturer_id=lecturer.id AND course_id=course.id ORDER BY courseoffering.year, coursename")
    fun getCourseInfo(lect: String): Flow<List<CourseInfo>>

    @Query("UPDATE courseoffering SET lecturer_id=:lecturer_id, course_id=:course_id, year=:year, semester=:semester where id=:id")
    fun updateOffering(id: Long, lecturer_id: Long, course_id: Long, year: Int, semester: Int)

    @Query("SELECT * FROM lecturer where id=:id")
    fun getLecturer(id: Long): Flow<Lecturer>



}
