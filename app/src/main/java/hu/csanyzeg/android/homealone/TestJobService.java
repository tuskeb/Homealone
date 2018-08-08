package hu.csanyzeg.android.homealone;

import android.app.job.JobParameters;
import android.app.job.JobService;

/**
 * Created by tanulo on 2018. 08. 02..
 */

public class TestJobService extends JobService {
    private static final int JOB_ID = 1;
    private static final int ONE_MIN = 60 * 1000;


    @Override
    public boolean onStartJob(JobParameters params) {
        doMyWork();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // whether or not you would like JobScheduler to automatically retry your failed job.
        return false;
    }

    private void doMyWork() {
        // I am on the main thread, so if you need to do background work,
        // be sure to start up an AsyncTask, Thread, or IntentService!
    }
}