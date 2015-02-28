package io.github.jwolff52.updateschedule;

import io.github.jwolff52.updateschedule.database.Database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class UpdateSchedule {

	public static void main(String[] args) {
		Database.initDBConnection(args[0]);
		Database.getTables();
		Database.resetTable();
		try {
			URL url = new URL("https://docs.google.com/spreadsheets/d/1ymTNFmmE5P39-ifypM8u3tfEzcA60OSbns3PEdSJ980/pubhtml?gid=427413677&single=true");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				if(inputLine.contains("</script></head>")) {
					ArrayList<String> streams = getStreams(inputLine.substring(inputLine.indexOf("Sunday"), inputLine.indexOf("Monday")));
					for(String s:streams) {
						Database.addStream("sun", s);
					}
					streams = getStreams(inputLine.substring(inputLine.indexOf("Monday"), inputLine.indexOf("Tuesday")));
					for(String s:streams) {
						Database.addStream("mon", s);
					}
					streams = getStreams(inputLine.substring(inputLine.indexOf("Tuesday"), inputLine.indexOf("Wednesday")));
					for(String s:streams) {
						Database.addStream("tue", s);
					}
					streams = getStreams(inputLine.substring(inputLine.indexOf("Wednesday"), inputLine.indexOf("Thursday")));
					for(String s:streams) {
						Database.addStream("wed", s);
					}
					streams = getStreams(inputLine.substring(inputLine.indexOf("Thursday"), inputLine.indexOf("Friday")));
					for(String s:streams) {
						Database.addStream("thu", s);
					}
					streams = getStreams(inputLine.substring(inputLine.indexOf("Friday"), inputLine.indexOf("Saturday")));
					for(String s:streams) {
						System.out.println(s);
						s.indexOf("at");
						Database.addStream("fri", s);
					}
					streams = getStreams(inputLine.substring(inputLine.indexOf("Saturday"), inputLine.indexOf("<br><br><br>")));
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

	private static ArrayList<String> getStreams(String day) {
		ArrayList<String> streams = new ArrayList<>();
		day = day.substring(day.indexOf("<br>") + 41);
		while(day.contains("<br><br>")) {
			if(day.contains("<br>at")) {
				streams.add(day.substring(0, day.indexOf("<br>at")) + day.substring(day.indexOf("<br>at") + 4, day.indexOf("<br><br>")));
				day = day.substring(day.indexOf("<br><br>") + 8);
				continue;
			}
			streams.add(day.substring(0, day.indexOf("<br><br>")));
			day = day.substring(day.indexOf("<br><br>") + 8);
		}
		return streams;
	}
}
