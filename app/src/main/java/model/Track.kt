package model

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String
){
    fun getFormattedTrackTime(): String {
        val seconds = (trackTimeMillis / 1000) % 60
        val minutes = (trackTimeMillis / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}