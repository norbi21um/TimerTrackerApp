package hu.bme.aut.nagyhazi.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "timer_table")
data class Timer(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    var timerName:String,
    var timerDescription:String,
    var timer:Long,
    var startTimer:Long,
    var isCounting:Boolean
):Parcelable


