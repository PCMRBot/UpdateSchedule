/*	  Update schedule database for pcmrbot
 *    Copyright (C) 2015  James Wolff
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.jwolff52.updateschedule;

import io.github.jwolff52.updateschedule.database.Database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class UpdateSchedule {

	private static ArrayList<String> streams;
	
	public static void main(String[] args) {
		Database.initDBConnection(args[0]);
		Database.getTables();
		Database.resetTable();
		try {
			URL url = new URL("https://docs.google.com/spreadsheets/d/1ymTNFmmE5P39-ifypM8u3tfEzcA60OSbns3PEdSJ980/pubhtml?gid=955381834&single=true");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				if(inputLine.contains("</script></head><body")) {
					break;
				}
			}
			String[] tokenized=inputLine.split("[<>]");
			boolean countMonday = true;
			int timezone = 0;
			for(int i = 0;i<tokenized.length;i++) {
				if(tokenized[i].equalsIgnoreCase("Sunday")) {
					timezone++;
					countMonday = false;
				} else if(tokenized[i].equalsIgnoreCase("Monday")) {
					if(countMonday) {
						timezone++;
					} else {
						countMonday = true;
					}
				}
				if(timezone == 3) {
					try {
						i = getStreams(tokenized, i, "Monday");
						for(String s:streams) {
							Database.addStream("sun", s);
						}
					} catch (StringIndexOutOfBoundsException e) {}
					i = getStreams(tokenized, i, "Tuesday");
					for(String s:streams) {
						Database.addStream("mon", s);
					}
					i = getStreams(tokenized, i, "Wednesday");
					for(String s:streams) {
						Database.addStream("tue", s);
					}
					i = getStreams(tokenized, i, "Thursday");
					for(String s:streams) {
						Database.addStream("wed", s);
					}
					i = getStreams(tokenized, i, "Friday");
					for(String s:streams) {
						Database.addStream("thu", s);
					}
					i = getStreams(tokenized, i, "Saturday");
					for(String s:streams) {
						s.indexOf("at");
						Database.addStream("fri", s);
					}
					i = getStreams(tokenized, i, "Times displayed");
					for(String s:streams) {
						Database.addStream("sat", s);
					}
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static int getStreams(String[] tokenized, int start, String endKeyWord) {
		streams = new ArrayList<>();
		for(; start < tokenized.length;start++) {
			if(!tokenized[start].toLowerCase().contains(endKeyWord.toLowerCase())) {
				if(tokenized[start].contains(":")) {
					if(tokenized[start + 2].startsWith("at")) {
						streams.add(tokenized[start] + tokenized[start + 2]);
						start+=2;
					} else {
						streams.add(tokenized[start] + tokenized[start + 4]);
						start+=4;
					}
				}
			} else {
				break;
			}
		}
		return start;
	}
}
