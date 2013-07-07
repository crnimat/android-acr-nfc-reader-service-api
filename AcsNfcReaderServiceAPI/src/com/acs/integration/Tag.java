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

import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.nfc.tech.TagTechnology;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * NFC Tag representation forked from the Android open source project.
 * 
 * @author Thomas Rorvik Skjolberg
 *
 */

public class Tag implements Parcelable {

    /**
     * This technology is an instance of {@link NfcA}.
     * <p>Support for this technology type is mandatory.
     * @hide
     */
    public static final int NFC_A = 1;

    /**
     * This technology is an instance of {@link NfcB}.
     * <p>Support for this technology type is mandatory.
     * @hide
     */
    public static final int NFC_B = 2;

    /**
     * This technology is an instance of {@link IsoDep}.
     * <p>Support for this technology type is mandatory.
     * @hide
     */
    public static final int ISO_DEP = 3;

    /**
     * This technology is an instance of {@link NfcF}.
     * <p>Support for this technology type is mandatory.
     * @hide
     */
    public static final int NFC_F = 4;

    /**
     * This technology is an instance of {@link NfcV}.
     * <p>Support for this technology type is mandatory.
     * @hide
     */
    public static final int NFC_V = 5;

    /**
     * This technology is an instance of {@link Ndef}.
     * <p>Support for this technology type is mandatory.
     * @hide
     */
    public static final int NDEF = 6;

    /**
     * This technology is an instance of {@link NdefFormatable}.
     * <p>Support for this technology type is mandatory.
     * @hide
     */
    public static final int NDEF_FORMATABLE = 7;

    /**
     * This technology is an instance of {@link MifareClassic}.
     * <p>Support for this technology type is optional. If a stack doesn't support this technology
     * type tags using it must still be discovered and present the lower level radio interface
     * technologies in use.
     * @hide
     */
    public static final int MIFARE_CLASSIC = 8;

    /**
     * This technology is an instance of {@link MifareUltralight}.
     * <p>Support for this technology type is optional. If a stack doesn't support this technology
     * type tags using it must still be discovered and present the lower level radio interface
     * technologies in use.
     * @hide
     */
    public static final int MIFARE_ULTRALIGHT = 9;

    final byte[] mId;
    final int[] mTechList;
    final String[] mTechStringList;
	
	public Tag(byte[] mId, int[] techList) {
		this.mId = mId;
		this.mTechList = techList;
		
		this.mTechStringList = generateTechStringList(techList);
	}
	
    private String[] generateTechStringList(int[] techList) {
        final int size = techList.length;
        String[] strings = new String[size];
        for (int i = 0; i < size; i++) {
            switch (techList[i]) {
                case ISO_DEP:
                    strings[i] = IsoDep.class.getName();
                    break;
                case MIFARE_CLASSIC:
                    strings[i] = MifareClassic.class.getName();
                    break;
                case MIFARE_ULTRALIGHT:
                    strings[i] = MifareUltralight.class.getName();
                    break;
                case NDEF:
                    strings[i] = Ndef.class.getName();
                    break;
                case NDEF_FORMATABLE:
                    strings[i] = NdefFormatable.class.getName();
                    break;
                case NFC_A:
                    strings[i] = NfcA.class.getName();
                    break;
                case NFC_B:
                    strings[i] = NfcB.class.getName();
                    break;
                case NFC_F:
                    strings[i] = NfcF.class.getName();
                    break;
                case NFC_V:
                    strings[i] = NfcV.class.getName();
                    break;
                default:
                    throw new IllegalArgumentException("Unknown tech type " + techList[i]);
            }
        }
        return strings;
    }
    
    /**
     * Get the Tag Identifier (if it has one).
     * <p>The tag identifier is a low level serial number, used for anti-collision
     * and identification.
     * <p> Most tags have a stable unique identifier
     * (UID), but some tags will generate a random ID every time they are discovered
     * (RID), and there are some tags with no ID at all (the byte array will be zero-sized).
     * <p> The size and format of an ID is specific to the RF technology used by the tag.
     * <p> This function retrieves the ID as determined at discovery time, and does not
     * perform any further RF communication or block.
     * @return ID as byte array, never null
     */

    public byte[] getId() {
        return mId;
    }

	   /**
     * Get the technologies available in this tag, as fully qualified class names.
     * <p>
     * A technology is an implementation of the {@link TagTechnology} interface,
     * and can be instantiated by calling the static <code>get(Tag)</code>
     * method on the implementation with this Tag. The {@link TagTechnology}
     * object can then be used to perform advanced, technology-specific operations on a tag.
     * <p>
     * Android defines a mandatory set of technologies that must be correctly
     * enumerated by all Android NFC devices, and an optional
     * set of proprietary technologies.
     * See {@link TagTechnology} for more details.
     * <p>
     * The ordering of the returned array is undefined and should not be relied upon.
     * @return an array of fully-qualified {@link TagTechnology} class-names.
     */

    public String[] getTechList() {
        return mTechStringList;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        writeBytesWithNull(dest, mId);
        dest.writeInt(mTechList.length);
        dest.writeIntArray(mTechList);
    }

    public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel in) {
            // Tag fields
            byte[] id = Tag.readBytesWithNull(in);
            int[] techList = new int[in.readInt()];
            in.readIntArray(techList);

            return new Tag(id, techList);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    /*package*/ static byte[] readBytesWithNull(Parcel in) {
        int len = in.readInt();
        byte[] result = null;
        if (len >= 0) {
            result = new byte[len];
            in.readByteArray(result);
        }
        return result;
    }

    /*package*/ static void writeBytesWithNull(Parcel out, byte[] b) {
        if (b == null) {
            out.writeInt(-1);
            return;
        }
        out.writeInt(b.length);
        out.writeByteArray(b);
    }
}
