package org.moparisthebest.pageplus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import org.moparisthebest.pageplus.dto.Balance;

import static org.moparisthebest.pageplus.dto.Balance.regexStrip;

public class SMSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		if (bundle == null)
			return;

		Object[] pdus = (Object[]) bundle.get("pdus");
		for (int i = 0; i < pdus.length; ++i) {
			SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdus[i]);
			if (!"7243".equals(msg.getOriginatingAddress()))
				continue;
			// it's a message from Page Plus, but is it a balance message?
			String message = msg.getMessageBody();
			if (message == null || !message.startsWith("Balance:"))
				continue;
			// it IS a balance message, get to parsing...
			try {
				// split string by newlines
				final String[] msgLines = message.split("\n");
				final Balance balance = new Balance();
				// get balance
				balance.setBalance(msgLines[0], msgLines[1]);
				// get plan
				balance.setPlan(msgLines[2], msgLines[3]);
				// get minutes
				balance.info[2] = msgLines[4].replaceFirst(regexStrip, "");
				// get text
				balance.info[3] = msgLines[5].replaceFirst(regexStrip, "");
				// get data
				balance.info[4] = msgLines[6].replaceFirst(regexStrip, "");

				// test
				//android.widget.Toast.makeText(context, balance.toString(), android.widget.Toast.LENGTH_SHORT).show();

				//---send a broadcast intent to update the SMS received in the activity---
				Intent broadcastIntent = new Intent();
				broadcastIntent.setAction(Main.PP_BAL_RECEIVED_ACTION);
				broadcastIntent.putExtra(Main.BALANCE, balance.success().compactFormat());
				context.sendBroadcast(broadcastIntent);

/*
				// now save them
				SharedPreferences settings = Main.getMainContext().getSharedPreferences(Main.PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();

				for (int x = 0; x < PPInfo.names.length; ++x)
					editor.putString(PPInfo.names[x], info[x]);

				editor.putString(Main.DATE, new Date().toLocaleString());
				editor.remove(Main.ERROR);
				editor.commit();
				Main.getMainContext().populateData();
				*/
			} catch (Exception e) {
				//e.printStackTrace();
				//editor.putString(ERROR, e.getMessage());
			}
			//editor.commit();
		}
	}
}