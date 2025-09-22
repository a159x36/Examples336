@file:Suppress("DEPRECATION")

package nz.massey.jobsworth

import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.app.JobIntentService
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.jvm.java

val soundplaying= mutableStateOf(false)
@Composable
fun Main(modifier: Modifier = Modifier, startIntent:()->Unit, startJobIntent:()->Unit,
         startJobService:()->Unit, playSound:()->Unit, startWorkManager:()->Unit,
         startFgService:()->Unit, startPop:()->Unit) {
    Column(modifier = modifier) {
        Button(onClick = startIntent ) {Text("Start Intent Service")}
        Button(onClick = startFgService ) {Text("Start Foreground Service")}
        Button(onClick = startJobIntent ) {Text("Start Job Intent Service")}
        Button(onClick = startJobService ) {Text("Start Job Service")}
        Button(onClick = startWorkManager ) {Text("Start Work Manager")}
        Button(onClick = playSound ) {
            if(soundplaying.value) Text ("Pause Sound") else Text("Play Sound")
        }
        Button(onClick = startPop ) { Text ("Pop") }
    }
}

class MainActivity : ComponentActivity() {
    val soundPool= SoundPool(4, AudioManager.STREAM_NOTIFICATION,1)
    lateinit var mMp: MediaPlayer
    var soundID=0
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            Scaffold { padding ->
                Main(
                    modifier = Modifier.padding(padding), startIntent = {
                    startService(
                        Intent(
                            this,
                            MyIntentService::class.java
                        )
                    )
                },
                    startJobIntent = {
                        JobIntentService.enqueueWork(
                            this,
                            MyJobIntentService::class.java,
                            159336,
                            Intent()
                        )
                    },
                    startJobService = {
                        MyJobService.scheduleJob(this)
                    },
                    playSound = {
                        soundplaying.value = !soundplaying.value
                        CoroutineScope(Dispatchers.Main).launch {
                            if (!soundplaying.value) {
                                mMp.pause()
                            } else {
                                mMp.start()
                            }
                        }


                    },
                    startWorkManager = {
                        val workRequest = OneTimeWorkRequestBuilder<MyWorker>().build()
                        WorkManager.getInstance(this).enqueue(workRequest)
                    },
                    startFgService = {
                        startForegroundService(Intent(this, MyFgService::class.java))
                    }, startPop = {
                        soundPool.play(soundID, 1f, 1f, 0, 0, 1f)
                    }
                )
            }
        }
        if(Build.VERSION.SDK_INT>=33) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf<String>(android.Manifest.permission.POST_NOTIFICATIONS), 0)
            }
        }

        mMp = MediaPlayer.create(this, R.raw.example)
        mMp.isLooping = true

        soundPool.setOnLoadCompleteListener{ soundPool, sampleId, status -> Log.i("Job","Sound loaded") }
        soundID = soundPool.load(this, R.raw.pop, 1)
    }

    protected override fun onResume() {
        super.onResume()
        if (soundplaying.value) mMp.start()
    }

    protected override fun onPause() {
        super.onPause()
        if(soundplaying.value) mMp.pause()
    }
}