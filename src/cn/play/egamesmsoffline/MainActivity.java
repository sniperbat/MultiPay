package cn.play.egamesmsoffline;

import net.sniperbat.tools.MultiPay;
import net.sniperbat.tools.MultiPayListener;
import cn.play.egamesmsoffline.R;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private final static String billingCodes[][] = {
		{ "001", },	//CMCC
		{ "126695", },	//EGAME
		{ "001", },	//UNICOM
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final Button btn = (Button)findViewById(R.id.payBtn);
		btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				MainActivity.pay(0);
			}
		});
		MultiPay.init(this);
	}
	
	public static void pay(int index){
		MultiPay.pay(billingCodes[MultiPay.carrier][index], new MultiPayListener() {
			@Override
			public void onPaySuccess() {
			}
	
			@Override
			public void onPayCancel() {
			}
	
			@Override
			public void onPayFailed() {
			}
		});
	}
}
