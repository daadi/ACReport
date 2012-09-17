package com.artisztikum.ac.ac.adapters;

import static com.artisztikum.ac.ac.TicketPriority.High;
import static com.artisztikum.ac.ac.TicketPriority.Highest;
import static com.artisztikum.ac.ac.TicketPriority.Low;
import static com.artisztikum.ac.ac.TicketPriority.Lowest;
import static com.artisztikum.ac.ac.TicketPriority.Normal;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.artisztikum.ac.ac.TicketPriority;

public class TicketPriorityAdapter extends XmlAdapter<Integer, TicketPriority> {

	@Override
	public Integer marshal(TicketPriority data) throws Exception {
		switch (data) {
		case Highest:
			return 2;
		case High:
			return 1;
		case Normal:
			return 0;
		case Low:
			return -1;
		case Lowest:
			return -2;
		default:
			return null;

		}
	}

	@Override
	public TicketPriority unmarshal(Integer xmlData) throws Exception {
		switch (xmlData) {
		case 2:
			return Highest;
		case 1:
			return High;
		case 0:
			return Normal;
		case -1:
			return Low;
		case -2:
			return Lowest;
		default:
			return null;

		}
	}

}
