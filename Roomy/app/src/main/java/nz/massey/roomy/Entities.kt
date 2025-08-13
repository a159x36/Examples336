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
        childColumns = arrayOf("courseId"),
        onDelete = ForeignKey.CASCADE), ForeignKey(entity = Lecturer::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("lecturerId"),
        onDelete = ForeignKey.CASCADE)],
    indices = [
        Index("courseId"),
        Index("lecturerId")]
)
@Serializable
data class CourseOffering(@PrimaryKey(autoGenerate = true) val id: Long=0,
                          val courseId: Long,
                          val lecturerId: Long,
                          val year: Int,
                          val semester: Int)
