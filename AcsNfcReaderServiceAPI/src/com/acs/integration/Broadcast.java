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

package com.acs.integration;

/**
 * 
 * Interface defining the broadcast actions which the service uses.
 * 
 * @author Thomas Rorvik Skjolberg
 *
 */

public interface Broadcast {
	
	public static final String ACTION_SERVICE_STARTED = "com.acs.integration.action.SERVICE_STARTED";
	public static final String ACTION_SERVICE_STOPPED = "com.acs.integration.action.SERVICE_STOPPED";

	public static final String ACTION_NFC_READER_OPEN = "com.acs.integration.action.NFC_READER_OPEN";
	public static final String ACTION_NFC_READER_CLOSED = "com.acs.integration.action.NFC_READER_CLOSED";

	public static final String ACTION_NFC_NDEF_DISCOVERED = "com.acs.integration.action.NFC_NDEF_DISCOVERED";
	public static final String ACTION_NFC_TAG_DISCOVERED = "com.acs.integration.action.NFC_TAG_DISCOVERED";
	public static final String ACTION_NFC_TECH_DISCOVERED = "com.acs.integration.action.NFC_TECH_DISCOVERED";

    public static final String EXTRA_NDEF_MESSAGES = "com.acs.integration.extra.EXTRA_NDEF_MESSAGES";
    public static final String EXTRA_TAG = "com.acs.integration.extra.TAG";
    public static final String EXTRA_ID = "com.acs.integration.extra.ID";

	public static final String ACTION_NFC_TAG_LEFT_FIELD = "com.acs.integration.action.NFC_TAG_LEFT_FIELD";

	/**
	 * 
	 * Request write of NDEF content to tag. The NDEF message to write must be set as {@link EXTRA_NDEF_MESSAGES} in the broadcast.
	 * 
	 */
	
	public static final String ACTION_NFC_TAG_WRITE_REQUEST = "com.acs.integration.action.NFC_TAG_WRITE_REQUEST";
	
	/**
	 * 
	 * Result from {@link ACTION_NFC_TAG_WRITE_REQUEST}. Contains a {@link EXTRA_WRITE_STATUS} status.  
	 * 
	 */
	
	public static final String ACTION_NFC_TAG_WRITE_RESULT = "com.acs.integration.action.NFC_TAG_WRITE_RESULT";
	
	public static final String EXTRA_WRITE_STATUS = "com.acs.integration.extra.WRITE_STATUS";
	
	public static final int WRITE_STATUS_SUCCESS = 0;
	public static final int WRITE_STATUS_FAILURE = 1;
	public static final int WRITE_STATUS_PRECONDITION_FAILURE = 2;

}
