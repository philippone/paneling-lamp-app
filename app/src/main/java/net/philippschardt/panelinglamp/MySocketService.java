  package net.philippschardt.panelinglamp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

  public class MySocketService extends Service {
    public MySocketService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }




}
