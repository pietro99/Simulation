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
    public static int runs = 50000;

	

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
			RegularSource regular_s = new RegularSource(regular_q,GPU_q, l, "Regular Source", 25);
		
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
		
			l.start(20160); // 2000 is maximum time
			//System.out.println();
		
			
			String[] events = regular_si.getEvents();
			double[] times = regular_si.getTimes();
			String[]stations = regular_si.getStations();
			int[] numbers = regular_si.getNumbers();
			int counter2 = 0;
			//System.out.println("GPU jobs \n");
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
					//System.out.println(deley);
					counter2 = 0;
				}
				else counter2 ++;
			}
			
			
			String[] gpu_events = GPU_si.getEvents();
			double[] gpu_times = GPU_si.getTimes();
			String[] gpu_stations = GPU_si.getStations();
			int[] gpu_numbers = GPU_si.getNumbers();
			int gpu_counter = 0;
			//System.out.println("GPU jobs \n");
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
					//System.out.println(deley);
					gpu_counter = 0;
				}
				else gpu_counter ++;
			}
			
			//System.out.println(gpu_numbers.length);
			//System.out.println(numbers.length);

		
			Collections.sort(gpu_deleys);
			Collections.sort(regular_deleys);
			Collections.sort(all_deleys);
		
	
			System.out.print("\n gpu deleys: [");
		
			double mean_deley_gpu = 0;
			for(int i = 0; i<gpu_deleys.size(); i++) {
				System.out.print(gpu_deleys.get(i)+ ", ");
				mean_deley_gpu += gpu_deleys.get(i);
			}
			mean_deley_gpu = mean_deley_gpu/gpu_deleys.size();
			
			System.out.print("]\n regular deleys: [");
			double mean_deley_regular = 0;
			for(int i = 0; i<regular_deleys.size(); i++) {
				System.out.print(regular_deleys.get(i)+", ");
				mean_deley_regular += regular_deleys.get(i);
			}
			mean_deley_regular = mean_deley_regular / regular_deleys.size();
		
			
			double mean_deley_all = 0;
			for(int i = 0; i<all_deleys.size(); i++) {
				mean_deley_all += all_deleys.get(i);
			}
			
			
			int index_gpu = (int) Math.ceil(90 / 100.0 * (gpu_deleys.size()-1));
			int index_regular = (int) Math.ceil(90 / 100.0 * (regular_deleys.size()-1));
			int index_all = (int) Math.ceil(90 / 100.0 * (all_deleys.size()-1));
		
			double percentile_gpu_90 = gpu_deleys.get(index_gpu);
			double percentile_regular_90 = regular_deleys.get(index_regular);
			double percentile_all_90 = all_deleys.get(index_all);
		
		
		
		
			mean_deley_all = mean_deley_all / all_deleys.size();
			
			System.out.print("]\n");
			System.out.println("mean deley GPU: "+mean_deley_gpu);
			System.out.println("mean deley regular: "+mean_deley_regular);
			System.out.println("mean deley all: "+mean_deley_all);
			System.out.println("90% percentile deley GPU: "+ percentile_gpu_90);
			System.out.println("90% percentile deley regular: "+ percentile_regular_90);
			System.out.println("90% percentile deley all: "+ percentile_all_90);
			
			output[counter][0] = mean_deley_gpu;
			output[counter][1] = mean_deley_regular;
			output[counter][2] = mean_deley_all;
			output[counter][3] = percentile_gpu_90;
			output[counter][4] = percentile_regular_90;
			output[counter][5] = percentile_all_90;
			counter++;
			
    	}
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0; i<output.length; i++) {
    		for (int j = 0; j<output[i].length; j++) {
    			sb.append(output[i][j]);
    	    	sb.append(",");
    		}
	    	sb.append("\n");
    	}

    	BufferedWriter br = new BufferedWriter(new FileWriter("myfile.csv"));
    	br.write(sb.toString());
    	br.close();


    }
    
}
