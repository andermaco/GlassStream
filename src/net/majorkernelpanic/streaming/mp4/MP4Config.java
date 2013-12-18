/*
 * Copyright (C) 2011-2013 GUIGUI Simon, fyhertz@gmail.com
 * 
 * This file is part of Spydroid (http://code.google.com/p/spydroid-ipcamera/)
 * 
 * Spydroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package net.majorkernelpanic.streaming.mp4;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.h264.AvcConfigurationBox;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;

import android.util.Base64;
import android.util.Log;

/**
 * Finds SPS & PPS parameters in mp4 file.
 */
public class MP4Config {

	private static final String TAG = "MP4Config";

	private String mProfilLevel, mPPS, mSPS;

	public MP4Config(String profil, String sps, String pps) {
		mProfilLevel = profil;
		mPPS = pps;
		mSPS = sps;
	}

	/**
	 * Finds sps & pps parameters inside a .mp4.
	 * 
	 * @param path
	 *            Path to the file to analyze
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public MP4Config(String path) throws IOException, FileNotFoundException {
		// Get avc configuration box from movie
		Movie movie = MovieCreator.build(path);
		Track track = movie.getTracks().get(0);
		SampleDescriptionBox stsd = track.getSampleDescriptionBox();
		VisualSampleEntry vse = (VisualSampleEntry) stsd.getSampleEntry();
		AvcConfigurationBox avcConfigurationBox = vse.getBoxes(AvcConfigurationBox.class).get(0);
		
		// Get SPS bytes
		byte[] pps = getBytePPS(avcConfigurationBox);
		
		// Get PPS bytes
		byte[] sps = getByteSPS(avcConfigurationBox);
        
		// Get config values
	    mPPS = Base64.encodeToString(pps, 0, pps.length, Base64.NO_WRAP);
	    mProfilLevel = MP4Config.toHexString(sps, 1, 3);
	    mSPS = Base64.encodeToString(sps, 0, sps.length, Base64.NO_WRAP);
		
		Log.i(TAG, "PPS: " + mPPS + " SPS: " + mSPS + " ProfileLvl: "
				+ mProfilLevel);
	}
	
	private byte[] getByteSPS(AvcConfigurationBox avcConfigurationBox) {
        List<byte[]> sps = avcConfigurationBox.getSequenceParameterSets();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            for (byte[] sp : sps) {
                baos.write(sp);
            }
        } catch (IOException ex) {
            throw new RuntimeException("SPS ByteArrayOutputStream do not throw IOException ?!?!?");
        }
        return baos.toByteArray();
    }
	
	private byte[] getBytePPS(AvcConfigurationBox avcConfigurationBox) {
        List<byte[]> pps = avcConfigurationBox.getPictureParameterSets();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            for (byte[] pp : pps) {
                baos.write(pp);
            }
        } catch (IOException ex) {
            throw new RuntimeException("PPS ByteArrayOutputStream do not throw IOException ?!?!?");
        }
        return baos.toByteArray();
    }

	static private String toHexString(byte[] buffer,int start, int len) {
		String c;
		StringBuilder s = new StringBuilder();
		for (int i=start;i<start+len;i++) {
			c = Integer.toHexString(buffer[i]&0xFF);
			s.append( c.length()<2 ? "0"+c : c );
		}
		return s.toString();
	}
	
	public String getProfileLevel() {
		return mProfilLevel;
	}

	public String getB64PPS() {
		return mPPS;
	}

	public String getB64SPS() {
		return mSPS;
	}
}