package net.sniperbat.tools;

public interface MultiPayListener {
	public void onPaySuccess();
	public void onPayCancel();
	public void onPayFailed();
}
