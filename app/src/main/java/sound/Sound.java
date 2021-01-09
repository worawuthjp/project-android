package sound;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.application.myapp.R;

public class Sound {
    private SoundPool soundPool;
    private int sounds;
    private int sound2;

    public Sound(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME).build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes).build();
        }else{
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,5);
        }
        try{
            sounds = soundPool.load(context, R.raw.barcodebeep,1);
            sound2 = soundPool.load(context,R.raw.serror,1);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void playSound(int sound){
        soundPool.play(sound,1,1,0,0,1);
    }

}
