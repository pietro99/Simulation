/**
 *	Example program for using eventlists
 *	@author Joel Karel
 *	@version %I%, %G%
 */

package Simulation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;   

public class Simulation {

    public CEventList list;
    public Queue queue;
    public Source source;
    public Sink sink;
    public Machine mach;
    public ArrayList<Integer> gpu_deleys; 
    public ArrayList<Integer> regular_deleys;
    public static int runs = 1;
    public static int sim_length = 100000000;

	

        /**
     * @param args the command line arguments
         * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
    	int counter = 0;
    	double[][] output = new double[runs][6];
    	while(counter < runs) {
    	
		    ArrayList<Double> gpu_deleys = new ArrayList<Double>();
		    ArrayList<Double> regular_deleys = new ArrayList<Double>();
		    ArrayList<Double> all_deleys = new ArrayList<Double>();
		
		
		    	// Create an eventlist
			CEventList l = new CEventList();
			
			// A queue for the machine
			Queue GPU_q = new Queue();
			Queue regular_q = new Queue();
		
			// A source
			GPUSource gpu_s = new GPUSource(GPU_q,l,"GPU Source", 270);
			RegularSource regular_s = new RegularSource(regular_q,GPU_q, l, "Regular Source", 30);
		
			// A sink
			Sink GPU_si = new Sink("GPU sink");
			Sink regular_si = new Sink("Regular sink");
		
			
			// gpu server
			Machine GPU_server1 = new ServerGPU(GPU_q,regular_si,GPU_si,l,"GPU Server 1", 240, 50);
			Machine GPU_server2 = new ServerGPU(GPU_q,regular_si,GPU_si,l,"GPU Server 2", 240, 50);
		
			Machine regular_server1 = new Server(regular_q,regular_si,l,"regular Server 1", 145, 42);
			Machine regular_server2 = new Server(regular_q,regular_si,l,"regular Server 2", 145, 42);
			Machine regular_server3 = new Server(regular_q,regular_si,l,"regular Server 3", 145, 42);
			Machine regular_server4 = new Server(regular_q,regular_si,l,"regular Server 4", 145, 42);
			Machine regular_server5 = new Server(regular_q,regular_si,l,"regular Server 5", 145, 42);
			Machine regular_server6 = new Server(regular_q,regular_si,l,"regular Server 6", 145, 42);
		
			l.start(sim_length); 
			
			String[] events = regular_si.getEvents();
			double[] times = regular_si.getTimes();
			String[]stations = regular_si.getStations();
			int[] numbers = regular_si.getNumbers();
			int counter2 = 0;
			for(int i = 0; i< times.length; i++) {
				/*
				System.out.println("number = " + numbers[i]);
				System.out.println("event = " + events[i]);
				System.out.println("time = " + times[i]);
				System.out.println("station = " + stations[i]);
				System.out.println();
				 */
				if(counter2 == 2) {
					double deley = times[i-(counter2-1)] - times[i-counter2];
					regular_deleys.add(deley);
					all_deleys.add(deley);
					counter2 = 0;
				}
				else counter2 ++;
			}
		
			String[] gpu_events = GPU_si.getEvents();
			double[] gpu_times = GPU_si.getTimes();
			String[] gpu_stations = GPU_si.getStations();
			int[] gpu_numbers = GPU_si.getNumbers();
			int gpu_counter = 0;
			for(int i = 0; i< gpu_times.length; i++) {
				/*
				System.out.println("number = " + gpu_numbers[i]);
				System.out.println("event = " + gpu_events[i]);
				System.out.println("time = " + gpu_times[i]);
				System.out.println("station = " + gpu_stations[i]);
				System.out.println();
				*/
				if(gpu_counter == 2) {
					double deley = gpu_times[i-(gpu_counter-1)] - gpu_times[i-gpu_counter];
					gpu_deleys.add(deley);
					all_deleys.add(deley);
					gpu_counter = 0;
				}
				else gpu_counter ++;
			}
			
			
			//calculate averages
			
			double gpu_temp = 0;
			double gpu_average;
			for(int i=0; i<gpu_deleys.size(); i++) {
				gpu_temp += gpu_deleys.get(i);
			}
			gpu_average = gpu_temp/gpu_deleys.size();
			
			
			double regular_temp = 0;
			double regular_average;
			for(int i=0; i<regular_deleys.size(); i++) {
				regular_temp += regular_deleys.get(i);
			}
			regular_average = regular_temp/regular_deleys.size();
			
			
			double all_temp = 0;
			double all_average;
			for(int i=0; i<all_deleys.size(); i++) {
				all_temp += all_deleys.get(i);
			}
			all_average = all_temp/all_deleys.size();
			
			
			System.out.println("GPU average deley: "+gpu_average);
			System.out.println("regular average deley: "+regular_average);
			System.out.println("all average deley: "+all_average);

			
			
			StringBuilder sb1 = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			StringBuilder sb3 = new StringBuilder();

    		for (int i = 0; i<gpu_deleys.size(); i++) {
	    		sb1.append(gpu_deleys.get(i));
	    		sb1.append(",");
	    	}
    		for (int i = 0; i<regular_deleys.size(); i++) {
	    		sb2.append(regular_deleys.get(i));
	    		sb2.append(",");
	    	}
    		sb1.append("\n");
    		for (int i = 0; i<all_deleys.size(); i++) {
	    		sb3.append(all_deleys.get(i));
	    		sb3.append(",");
	    	}	
	    	    
	    	BufferedWriter br = new BufferedWriter(new FileWriter("batch_means_gpu_improved.csv"));
			br.write(sb1.toString());
	    	br.close();
	    	
	    	BufferedWriter br2 = new BufferedWriter(new FileWriter("batch_means_regular_improved.csv"));
			br2.write(sb2.toString());
	    	br2.close();
	    	
	    	BufferedWriter br3 = new BufferedWriter(new FileWriter("batch_means_all_improved.csv"));
			br3.write(sb3.toString());
	    	br3.close();
	  
			counter++;
    	}
    }
}