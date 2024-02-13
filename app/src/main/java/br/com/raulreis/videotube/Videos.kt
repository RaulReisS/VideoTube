package br.com.raulreis.videotube

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Video(
    val id: String,
    val thumbnailUrl: String,
    val title: String,
    val publishedAt: Date,
    val viewsCount: Long,
    val viewsCountLabel: String,
    val duration: Int,
    val videoUrl: String,
    val publisher: Publisher
    )

data class Publisher (
    val id: String,
    val name: String,
    val pictureProfileUrl: String
    )

data class ListVideo(
    val status: Int,
    val data: List<Video>
)

class VideoBuilder {
    var id: String = ""
    var thumbnailUrl: String = ""
    var title: String = ""
    var publishedAt: Date = Date()
    var viewsCount: Long = 0
    var viewsCountLabel: String = ""
    var duration: Int = 0
    var videoUrl: String = ""
    var publisher: Publisher = PublisherBuilder().build()

    fun build() : Video = Video (
        id, thumbnailUrl, title, publishedAt, viewsCount, viewsCountLabel, duration, videoUrl, publisher
            )

    fun publisher(block: PublisherBuilder.() -> Unit): Publisher =
        PublisherBuilder().apply(block).build()
}

class PublisherBuilder {
    var id: String = ""
    var name: String = ""
    var pictureProfileUrl: String = ""

    fun build() : Publisher {
        return Publisher(id, name, pictureProfileUrl)
    }
}

//DSL
fun video(block: VideoBuilder.() -> Unit) : Video =
    VideoBuilder().apply(block).build()

fun videos() : List<Video> {
    return arrayListOf(
        video {
            id = "PlYUZU0H5go"
            thumbnailUrl = "https://img.youtube.com/vi/cuau8E6t2QU/maxresdefault.jpg"
            title = "Relembrando Steve Jobs"
            publishedAt = "2019-08-15".toDate()
            viewsCount = 742_47
            duration = 1886
            publisher {
                id = "UCrWWMZ6GVOM5zqYAUI44XXg"
                name = "Tiago Aguiar"
                pictureProfileUrl ="https://yt3.ggpht.com/ytc/AKedOLT2VtZ3n30tTpDyjAoZGl44EfHhajN1Zy5LYm3iiA=s88-c-k-c0x00ffffff-no-rj"
            }
        },
        video {
            id = "PlYUZU0H5go"
            thumbnailUrl = "https://img.youtube.com/vi/cuau8E6t2QU/maxresdefault.jpg"
            title = "Relembrando Steve Jobs"
            publishedAt = "2019-08-15".toDate()
            viewsCount = 742_47
            duration = 1886
            publisher {
                id = "UCrWWMZ6GVOM5zqYAUI44XXg"
                name = "Tiago Aguiar"
                pictureProfileUrl ="https://yt3.ggpht.com/ytc/AKedOLT2VtZ3n30tTpDyjAoZGl44EfHhajN1Zy5LYm3iiA=s88-c-k-c0x00ffffff-no-rj"
            }
        },
        video {
            id = "PlYUZU0H5go"
            thumbnailUrl = "https://img.youtube.com/vi/cuau8E6t2QU/maxresdefault.jpg"
            title = "Relembrando Steve Jobs"
            publishedAt = "2019-08-15".toDate()
            viewsCount = 742_47
            duration = 1886
            publisher {
                id = "UCrWWMZ6GVOM5zqYAUI44XXg"
                name = "Tiago Aguiar"
                pictureProfileUrl ="https://yt3.ggpht.com/ytc/AKedOLT2VtZ3n30tTpDyjAoZGl44EfHhajN1Zy5LYm3iiA=s88-c-k-c0x00ffffff-no-rj"
            }
        }
    )
}
fun Date.formatted() : String =
    SimpleDateFormat("d MMM yyyy", Locale("pt", "BR")).format(this)

fun String.toDate() =
    SimpleDateFormat("yyyy-mm-dd", Locale("pt", "BR")).parse(this)