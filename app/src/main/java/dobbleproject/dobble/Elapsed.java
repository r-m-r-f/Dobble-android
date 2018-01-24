package dobbleproject.dobble;

import android.content.Context;
import java.util.ArrayList;

public class Elapsed implements Runnable{
    private Context mContext;
    private long nStartTime;

    private ArrayList<Long> timesHistory = new ArrayList<>();

    private boolean isRunning = false;

    Elapsed(Context context){
        mContext = context;
    }

    public void start(){
        nStartTime = System.nanoTime();
        isRunning = true;
    }

    void getAverage(){
        stop();
        isRunning = false;
        long sum = 0L;
        for (final long a : timesHistory) sum += a;
        sum/=1000000000;
        double avg = (double)sum/timesHistory.size();
        ((ClientGameActivity)mContext).updateTimerText(String.format("%.2f%c", avg, 's'));
    }

    void stop(){
        timesHistory.add(System.nanoTime() - nStartTime);
    }

    @Override
    public void run() {
        while(isRunning){
            double sElapsed = (double)(System.nanoTime() - nStartTime)/1000000000;
            ((ClientGameActivity)mContext).updateTimerText(String.format("%.2f%c", sElapsed, 's'));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
