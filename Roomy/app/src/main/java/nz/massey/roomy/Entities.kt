package nz.massey.roomy

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Lecturer(@PrimaryKey(autoGenerate = true) val id: Long=0, val name: String, val phone: String, var office: String)
@Entity
@Serializable
data class Course(@PrimaryKey(autoGenerate = true) val id: Long=0, val name: String, val location: String)
@Entity
@Serializable
data class CourseOffering(@PrimaryKey(autoGenerate = true) val id: Long=0, val course_id: Long, val lecturer_id: Long, val year: Int, val semester: Int)
