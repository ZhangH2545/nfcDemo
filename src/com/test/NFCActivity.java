package com.test;

import org.zywx.wbpalmstar.widgetone.uexNFC.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class NFCActivity extends Activity {

	private  NfcAdapter nfcAdapter;
	private String[][] techList;
	private IntentFilter[] intentFilters;
	private PendingIntent pendingIntent;
	private Tag tag;
	String tagId ;
	
	 private GifView gif1, gif2;  
	 private Context mContext;
	 private GetNfcHandler getNfcHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 setContentView(R.layout.nfc_activity); 
		 mContext=this;
//		// 获取nfc适配器
//		nfcAdapter = NfcAdapter.getDefaultAdapter(this);//这是app崩溃的原因！(如果手机不支持nfc,那么这个值就是空，会蹦！)
		 
		 getNfcHandler = new GetNfcHandler();
 	     GetNfcThread m = new GetNfcThread();
        new Thread(m).start();
		// 定义程序可以兼容的nfc协议，例子为nfca和nfcv
		// 在Intent filters里声明你想要处理的Intent，一个tag被检测到时先检查前台发布系统，
		// 如果前台Activity符合Intent filter的要求，那么前台的Activity的将处理此Intent。
		// 如果不符合，前台发布系统将Intent转到Intent发布系统。如果指定了null的Intent filters，
		// 当任意tag被检测到时，你将收到TAG_DISCOVERED intent。因此请注意你应该只处理你想要的Intent。
		techList = new String[][] {
				new String[] { android.nfc.tech.NfcV.class.getName() },
				new String[] { android.nfc.tech.NfcA.class.getName() } };
		intentFilters = new IntentFilter[] { new IntentFilter(
				NfcAdapter.ACTION_TECH_DISCOVERED), };
		// 创建一个 PendingIntent 对象, 这样Android系统就能在一个tag被检测到时定位到这个对象
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		
		
		 gif2 = (GifView) findViewById(R.id.gif1);  
	     gif2.setMovieResource(R.raw.nfc0);  
		
	}

	
	class GetNfcThread implements Runnable {
        public void run() {
        	Message msg = new Message();
        	if (hasNfc(mContext)==2) {
        	    msg.what=0;
        	    NFCActivity.this.getNfcHandler.handleMessage(msg);
        	}else if(hasNfc(mContext)==-1) {
				Intent intent = null;  
        		// 先判断当前系统版本然后进行  
        		if(android.os.Build.VERSION.SDK_INT > 10){  // 3.0以上  
        		    intent =  new Intent(Settings.ACTION_NFC_SETTINGS);   
//        		    ACTION_NFCSHARING_SETTINGS  
//        		    intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);//感兴趣可以试试这个
        		             
        		}else{  
        		    intent = new Intent();  
        		    intent.setClassName("com.android.settings", "com.android.settings.Settings");  
        		}  
        		startActivity(intent);  
			}else if (hasNfc(mContext)==1) {
				
			}
        	
        }
           
    }
	class GetNfcHandler extends Handler{
		   public GetNfcHandler() {
	        }
	        @Override
	        public void handleMessage(Message msg) {
	            super.handleMessage(msg);
	           switch (msg.what) {
			case 0:
				Looper.prepare();
				Toast.makeText(mContext, "此手机不支持NFC功能", Toast.LENGTH_LONG).show();
				Looper.loop();
				finish();
				break;
			default:
				break;
			}
	        }
	}
	//判断有没有打开nfc功能！
	public  int hasNfc(Context context){  
	    int bRet = 0;  
	    if(context==null)  
	        return bRet;  
	    NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);  
	    nfcAdapter = manager.getDefaultAdapter();  
	      // 获取nfc适配器
//	 		nfcAdapter = NfcAdapter.getDefaultAdapter(context);
	 		if (nfcAdapter==null) {
				bRet=2;//说明手机不支持NFC
			} else if(nfcAdapter.isEnabled()) {
				bRet=1;//说明手机支持NFC同时也开启了NFC
			}else if (!nfcAdapter.isEnabled()) {
				bRet=-1;//说明手机支持NFC但是尚未开启NFC功能
			}
	   
	    return bRet;  
	} 
	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		super.onResume();
		if (nfcAdapter!=null) {
			nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters,
					techList);
		} 

	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		
		tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		tagId = StringUtils.toHexString(tag.getId());
		
		Toast.makeText(this, tagId, Toast.LENGTH_SHORT).show();	
		
		if(tagId.length()!=0)
		{
			Intent in = new Intent(getIntent().getAction());
			in.putExtra("result",
					tagId);
			setResult(Activity.RESULT_OK, in);
			finish();
		}
		return;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (nfcAdapter!=null) {
			nfcAdapter.disableForegroundDispatch(this);
		} 
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent in = new Intent(getIntent().getAction());
			setResult(Activity.RESULT_CANCELED, in);
			finish();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

}
