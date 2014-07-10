package cn.play.egamesmsoffline;

import android.app.Application;

public class MyApplication extends Application {
  public void onCreate() {
	System.loadLibrary("megjb");
  }
}
