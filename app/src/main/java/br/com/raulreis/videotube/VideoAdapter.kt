package br.com.raulreis.videotube

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class VideoAdapter(private val videos : List<Video>, val onClick: (Video) -> Unit) : RecyclerView.Adapter<VideoAdapter.VideoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder {
        return VideoHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_video, parent, false))
    }

    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        holder.bind(videos[position])
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    inner class VideoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(video: Video) {
            with(itemView) {
                setOnClickListener {
                    onClick(video)
                }
                Picasso.get().load(video.thumbnailUrl).into(findViewById<ImageView>(R.id.imgThumbnail))
                Picasso.get().load(video.publisher.pictureProfileUrl).into(findViewById<CircleImageView>(R.id.imgAuthor))
                findViewById<TextView>(R.id.txvTitle).text = video.title
                findViewById<TextView>(R.id.txvInfo).text = context.getString(R.string.info,
                        video.publisher.name, video.viewsCountLabel, video.publishedAt.formatted()
                    )

            }
        }
    }
}