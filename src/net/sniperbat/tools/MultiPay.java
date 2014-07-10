package net.sniperbat.tools;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import cn.cmgame.billing.api.BillingResult;
import cn.cmgame.billing.api.GameInterface;
import cn.cmgame.billing.api.GameInterface.IPayCallback;
import cn.egame.terminal.paysdk.EgamePay;
import cn.egame.terminal.paysdk.EgamePayListener;

import com.unicom.dcLoader.Utils;
import com.unicom.dcLoader.Utils.UnipayPayResultListener;

public class MultiPay {
	private static String TAG = "MultiPay";
	
	public final static int CARRIER_CMCC = 0;
	public final static int CARRIER_TELCOM = 1;
	public final static int CARRIER_UNICOM = 2;
	
	public static int carrier;
	private static Activity activity;

	public static void init(Activity activity) {
		MultiPay.activity = activity;
		TelephonyManager teleMgr = (TelephonyManager)activity.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = teleMgr.getSubscriberId();
		if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
			carrier = CARRIER_CMCC;
			GameInterface.initializeApp(activity);
		} else if (imsi.startsWith("46001")) {
			carrier = CARRIER_UNICOM;
			Utils.getInstances().initSDK(activity, 0);
		} else if (imsi.startsWith("46003")) {
			carrier = CARRIER_TELCOM;
		}
	}
	
	private static void unicomPay(String billingCode){
		Utils.getInstances().pay(MultiPay.activity, billingCode, new UnipayPayResultListener() {
			@Override
			public void PayResult(String payCode, int flag, String desc) {
				if (flag == Utils.SUCCESS_SMS) {
					Log.d(TAG, "unicom pay via sms successed");
					MultiPay.listener.onPaySuccess();
				} else if (flag == Utils.FAILED) {
					Log.d(TAG, "unicom pay failed: "+desc);
					MultiPay.listener.onPayFailed();
				} else if (flag == Utils.CANCEL) {
					Log.d(TAG, "unicom pay canceled");
					MultiPay.listener.onPayCancel();
				}
			}
		});
	}
	
	private static void egamePay(String billingCode){
		EgamePay.pay(MultiPay.activity, billingCode, new EgamePayListener() {
			@Override
			public void paySuccess(String alias) {
				Log.d(TAG, "telcom pay successed");
				MultiPay.listener.onPaySuccess();
			}
			@Override
			public void payFailed(String alias, int errorCode) {
				Log.d(TAG, "telcom pay failed: " + errorCode);
				MultiPay.listener.onPayFailed();
			}
			@Override
			public void payCancel(String alias) {
				Log.d(TAG, "telcom pay canceled");
				MultiPay.listener.onPayCancel();
			}
		});
	}
	
	private static void cmccPay(String billingCode) {
		GameInterface.doBilling(MultiPay.activity, true, true, billingCode, null, new IPayCallback() {
			@Override
			public void onResult(int resultCode, String billingIndex, Object arg) {
				if (resultCode == BillingResult.SUCCESS) {
					Log.d(TAG, "cmcc pay success");
					MultiPay.listener.onPaySuccess();
				} else if (resultCode == BillingResult.FAILED) {
					Log.d(TAG, "cmcc pay failed");
					MultiPay.listener.onPayFailed();
				} else if (resultCode == BillingResult.CANCELLED) {
					Log.d(TAG, "cmcc pay canceled");
					MultiPay.listener.onPayCancel();
				}
			}
		});
	}
	private static MultiPayListener listener;
	public static void pay(String billingCode, MultiPayListener listener) {
		MultiPay.listener = listener;
		switch (carrier) {
		case CARRIER_UNICOM:
			MultiPay.unicomPay(billingCode);
		break;
		case CARRIER_TELCOM:
			MultiPay.egamePay(billingCode);
		break;
		case CARRIER_CMCC:
			MultiPay.cmccPay(billingCode);
		break;
		}
	}

	
}
