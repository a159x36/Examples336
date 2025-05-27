package nz.massey.roomy

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Lecturer(@PrimaryKey(autoGenerate = true) val id: Long=0,
                    val name: String,
                    val phone: String,
                    val office: String)
@Entity
@Serializable
data class Course(@PrimaryKey(autoGenerate = true) val id: Long=0,
                  val name: String,
                  val location: String)
@Entity(
    foreignKeys = [ForeignKey(entity = Course::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("course_id"),
        onDelete = ForeignKey.CASCADE), ForeignKey(entity = Lecturer::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("lecturer_id"),
        onDelete = ForeignKey.CASCADE)],
    indices = [
        Index("course_id"),
        Index("lecturer_id")]
)
@Serializable
data class CourseOffering(@PrimaryKey(autoGenerate = true) val id: Long=0,
                          val course_id: Long,
                          val lecturer_id: Long,
                          val year: Int,
                          val semester: Int)
