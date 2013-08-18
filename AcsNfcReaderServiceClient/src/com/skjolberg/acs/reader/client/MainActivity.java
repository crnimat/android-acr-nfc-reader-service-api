/***************************************************************************
 * 
 * This file is part of the 'Android ACR NFC Reader Service API ' project at
 * https://github.com/skjolber/android-acr-nfc-reader-service-api
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 ****************************************************************************/

package com.skjolberg.acs.reader.client;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.Locale;

import org.ndeftools.Message;
import org.ndeftools.Record;
import org.ndeftools.UnsupportedRecord;
import org.ndeftools.wellknown.TextRecord;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.acs.integration.Broadcast;
import com.acs.integration.Tag;
import com.skjolberg.acs.reader.client.R;

public class MainActivity extends Activity {

    protected static final String TAG = MainActivity.class.getName();
    
	private final BroadcastReceiver serviceStatusReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

        	String action = intent.getAction();
        	
            if (Broadcast.ACTION_SERVICE_STARTED.equals(action)) {

            	Log.d(TAG, "ACS NFC Reader service started");
                
            	setServiceStarted(true);
            } else if (Broadcast.ACTION_SERVICE_STOPPED.equals(action)) {

            	Log.d(TAG, "ACS NFC Reader service stopped");
            	
            	setServiceStarted(false);
            } else throw new IllegalArgumentException("Unexpected action " + action);
            
        	setReaderOpen(false);
        	setTagPresent(false);
        	clearTagType();
        	clearTagId();
        	hideRecords();
        	hideWriteNdefMessage();
        }
    };
    
	private final BroadcastReceiver writeNfcTagResultReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

        	String action = intent.getAction();
        	
            if (Broadcast.ACTION_NFC_TAG_WRITE_RESULT.equals(action)) {
            	Log.d(TAG, "Write result");
            	int status = intent.getIntExtra(Broadcast.EXTRA_WRITE_STATUS, -1);
            	if(status == Broadcast.WRITE_STATUS_SUCCESS) {
                	setTextViewText(R.id.writeStatus, getString(R.string.writeNdefMessageSuccess));
            	} else if(status == Broadcast.WRITE_STATUS_FAILURE) {
                	setTextViewText(R.id.writeStatus, getString(R.string.writeNdefMessageFailure));
            	} else if(status == Broadcast.WRITE_STATUS_PRECONDITION_FAILURE) {
                	setTextViewText(R.id.writeStatus, getString(R.string.writeNdefMessagePreconditionFailure));
                } else throw new IllegalArgumentException("Unexpected status " + status);
            	
            } else throw new IllegalArgumentException("Unexpected action " + action);
        }
    };
    
	private final BroadcastReceiver readerStatusReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

        	String action = intent.getAction();
        	
            if (Broadcast.ACTION_NFC_READER_OPEN.equals(action)) {

            	Log.d(TAG, "ACS NFC Reader opened");
                
            	setReaderOpen(true);
            } else if (Broadcast.ACTION_NFC_READER_CLOSED.equals(action)) {

            	Log.d(TAG, "ACS NFC Reader closed");
            	
            	setReaderOpen(false);
            	
            } else throw new IllegalArgumentException("Unexpected action " + action);
            
            
        	setTagPresent(false);
			clearTagType();
			clearTagId();
        	hideRecords();
        	hideWriteNdefMessage();
        }

    };
    
	private final BroadcastReceiver tagStatusReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

        	setServiceStarted(true);
        	setReaderOpen(true);

        	String action = intent.getAction();
        	
            if (Broadcast.ACTION_NFC_TAG_DISCOVERED.equals(action)) {

            	Log.d(TAG, "Tag discovered");

            	setTagInfo(intent);
            	
            	setTagPresent(true);

            	hideRecords();
            	
        		if(canWritable(intent)) {
        			showWriteNdefMessage();
        		}
            } else if (Broadcast.ACTION_NFC_TECH_DISCOVERED.equals(action)) {
            	Log.d(TAG, "Tech discovered");

            	setTagPresent(true);

            	setTagInfo(intent);

            	hideRecords();
            	
        		if(canWritable(intent)) {
        			showWriteNdefMessage();
        		}
            } else if (Broadcast.ACTION_NFC_NDEF_DISCOVERED.equals(action)) {
            	Log.d(TAG, "NDEF discovered");
            	
        		Parcelable[] parcelable = intent.getParcelableArrayExtra(Broadcast.EXTRA_NDEF_MESSAGES);

    			NdefMessage[] ndefMessages = new NdefMessage[parcelable.length];
    		    for (int i = 0; i < parcelable.length; i++) {
    		        ndefMessages[i] = (NdefMessage) parcelable[i];
    		    }

		    	// read as much as possible
				Message message = new Message();
				for (int i = 0; i < ndefMessages.length; i++) {
			    	NdefMessage ndefMessage = (NdefMessage) ndefMessages[i];
			        
					for(NdefRecord ndefRecord : ndefMessage.getRecords()) {
						try {
							message.add(Record.parse(ndefRecord));
						} catch (FormatException e) {
							// if the record is unsupported or corrupted, keep as unsupported record
							message.add(UnsupportedRecord.parse(ndefRecord));
						}
					}
			    }
    		    
        		// show in log
        		// iterate through all records in message
        		Log.d(TAG, "Found " + message.size() + " NDEF records");

        		for(int k = 0; k < message.size(); k++) {
        			Record record = message.get(k);
        			
        			Log.d(TAG, "Record " + k + " type " + record.getClass().getSimpleName());
        		}

            	setTagPresent(true);
            	setTagInfo(intent);

        		// show in gui
        		showRecords(message);

        		if(canWritable(intent)) {
        			showWriteNdefMessage();
        		}
            } else if (Broadcast.ACTION_NFC_TAG_LEFT_FIELD.equals(action)) {

            	Log.d(TAG, "NFC Tag left");
            	
            	setTagPresent(false);
            	
				clearTagType();
				clearTagId();
            	hideRecords();
            	
            	hideWriteNdefMessage();
            }
        }

    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		IntentFilter serviceFilter = new IntentFilter();
		serviceFilter.addAction(Broadcast.ACTION_SERVICE_STARTED);
		serviceFilter.addAction(Broadcast.ACTION_SERVICE_STOPPED);
        registerReceiver(serviceStatusReceiver, serviceFilter);

		IntentFilter readerFilter = new IntentFilter();
		readerFilter.addAction(Broadcast.ACTION_NFC_READER_OPEN);
		readerFilter.addAction(Broadcast.ACTION_NFC_READER_CLOSED);
        registerReceiver(readerStatusReceiver, readerFilter);

		IntentFilter tagFilter = new IntentFilter();
		tagFilter.addAction(Broadcast.ACTION_NFC_NDEF_DISCOVERED);
		tagFilter.addAction(Broadcast.ACTION_NFC_TAG_DISCOVERED);
		tagFilter.addAction(Broadcast.ACTION_NFC_TECH_DISCOVERED);
		tagFilter.addAction(Broadcast.ACTION_NFC_TAG_LEFT_FIELD);
        registerReceiver(tagStatusReceiver, tagFilter);

		IntentFilter writeFilter = new IntentFilter();
		writeFilter.addAction(Broadcast.ACTION_NFC_TAG_WRITE_RESULT);
        registerReceiver(writeNfcTagResultReceiver, writeFilter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void setServiceStarted(final boolean started) {
		if(started) {
			setTextViewText(R.id.serviceStatus, R.string.serviceStatusStarted);
		} else {
			setTextViewText(R.id.serviceStatus, R.string.serviceStatusStopped);
		}
	}
	
	public void setReaderOpen(final boolean open) {
		if(open) {
			setTextViewText(R.id.readerStatus, R.string.readerStatusOpen);
		} else {
			setTextViewText(R.id.readerStatus, R.string.readerStatusClosed);
		}
	}

	public void setTagPresent(final boolean present) {
		if(present) {
			setTextViewText(R.id.tagStatus, R.string.tagStatusPresent);
		} else {
			setTextViewText(R.id.tagStatus, R.string.tagStatusAbsent);
		}
	}

	public void setTagType(final String type) {
		setTextViewText(R.id.tagType, type);
	}

	private void clearTagType() {
		setTagType(getString(R.string.tagTypeNone));			
	}

	public void setTagId(final String type) {
		setTextViewText(R.id.tagId, type);
	}

	private void clearTagId() {
		setTagId(getString(R.string.tagIdNone));			
	}

	private void setTagInfo(Intent intent) {
		if(intent.hasExtra(Broadcast.EXTRA_TAG)) {
			Tag tag = intent.getParcelableExtra(Broadcast.EXTRA_TAG);
			
			String[] techList = tag.getTechList();
			for(String tech : techList) {
				if(!tech.equals(Ndef.class.getName()) && !tech.equals(NdefFormatable.class.getName())) {
					setTagType(tech.substring(tech.lastIndexOf('.') + 1));
				}
			}
			
			byte[] id = tag.getId();
			if(id != null) {
				setTagId(toHexString(id).toUpperCase());
			} else {
				setTagId(getString(R.string.tagIdUnknown));
			}
		} else {
			setTagType(getString(R.string.tagTypeOther));
			setTagId(getString(R.string.tagIdUnknown));
		}
	}
	
	private boolean canWritable(Intent intent) {
		if(intent.hasExtra(Broadcast.EXTRA_TAG)) {
			Tag tag = intent.getParcelableExtra(Broadcast.EXTRA_TAG);
			
			if(!tag.isWritable()) {
				return false;
			}
			
			String[] techList = tag.getTechList();
			for(String tech : techList) {
				if(tech.equals(MifareClassic.class.getName()) || tech.equals(MifareUltralight.class.getName())) {
					return true;
				}
			}
		}
		return false;
	}
	
    /**
     * Converts the byte array to HEX string.
     * 
     * @param buffer
     *            the buffer.
     * @return the HEX string.
     */
    public static String toHexString(byte[] buffer) {
		StringBuilder sb = new StringBuilder();
		for(byte b: buffer)
			sb.append(String.format("%02x ", b&0xff));
		return sb.toString();
    }
	
	public void setTextViewText(final int resource, final int string) {
		setTextViewText(resource, getString(string));
	}

	public void setTextViewText(final int resource, final String string) {
		runOnUiThread(new Runnable() {
			public void run() {
				TextView textView = (TextView) findViewById(resource);
				textView.setText(string);
				textView.setVisibility(View.VISIBLE);
			}
		});
	}

	/**
	 * 
	 * Show NDEF records in the list
	 * 
	 */
	
	private void showRecords(Message message) {
		// display the message in the gui
		
		Log.d(TAG, "Show " + message.size() + " records");
		
		ListView listView = (ListView) findViewById(R.id.recordListView);
		View ndefRecords = findViewById(R.id.ndefRecords);
		View readStatus = findViewById(R.id.readStatus);
		if(!message.isEmpty()) {
			ArrayAdapter<? extends Object> adapter = new NdefRecordAdapter(this, message);
			listView.setAdapter(adapter);
			listView.setVisibility(View.VISIBLE);
			
			ndefRecords.setVisibility(View.VISIBLE);
			readStatus.setVisibility(View.GONE);
		} else {
			listView.setVisibility(View.GONE);
			
			ndefRecords.setVisibility(View.GONE);
			readStatus.setVisibility(View.VISIBLE);
		}
		
	}

	public void writeNdefMessage(View view) {
		Log.d(TAG, "Request write of NDEF Message");

		// write ndef message
		// this is a premium feature
		// so it will not work if you have not enabled the feature in the service
		
		// compose your message
		// TODO your code here
		Message message = new Message();

		TextRecord textRecord = new TextRecord();
		textRecord.setText("Timestamp: " + new Date());
		textRecord.setLocale(Locale.US);
		textRecord.setEncoding(Charset.forName("UTF-8"));
		
		message.add(textRecord);
		
		Intent intent = new Intent(Broadcast.ACTION_NFC_TAG_WRITE_REQUEST);
		intent.putExtra(Broadcast.EXTRA_NDEF_MESSAGES, message.getNdefMessage());
		sendBroadcast(intent); 
	}
	
	/**
	 * 
	 * Hide the NDEF records list.
	 * 
	 */
	
	public void hideRecords() {
		findViewById(R.id.recordListView).setVisibility(View.GONE);
		findViewById(R.id.ndefRecords).setVisibility(View.GONE);
		findViewById(R.id.readStatus).setVisibility(View.GONE);
	}

	public void hideWriteNdefMessage() {
		findViewById(R.id.writeButton).setVisibility(View.GONE);
		findViewById(R.id.writeStatus).setVisibility(View.GONE);
	}

	public void showWriteNdefMessage() {
		setTextViewText(R.id.writeStatus, R.string.writeNdefMessageFeature);
		findViewById(R.id.writeButton).setVisibility(View.VISIBLE);
	}

	@Override
	protected void onDestroy() {
		
		unregisterReceiver(serviceStatusReceiver);
		unregisterReceiver(readerStatusReceiver);
		unregisterReceiver(tagStatusReceiver);
		unregisterReceiver(writeNfcTagResultReceiver);
		
		super.onDestroy();
	}
}
