package nz.massey.roomy

data class CourseInfo (
    var id: Long = 0,
    var coursename: String? = null,
    var lecturername: String? = null,
    var year: Int = 0,
    var semester: Int = 0
)
