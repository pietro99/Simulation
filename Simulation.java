/**
 *	Example program for using eventlists
 *	@author Joel Karel
 *	@version %I%, %G%
 */

package Simulation;

public class Simulation {

    public CEventList list;
    public Queue queue;
    public Source source;
    public Sink sink;
    public Machine mach;
	

        /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	// Create an eventlist
	CEventList l = new CEventList();
	
	// A queue for the machine
	Queue GPU_q = new Queue();
	Queue regular_q = new Queue();

	// A source
	GPUSource gpu_s = new GPUSource(GPU_q,l,"GPU Source");
	RegularSource regular_s = new RegularSource(regular_q, l, "Regular Source", 300);

	// A sink
	Sink GPU_si = new Sink("GPU sink");
	Sink regular_si = new Sink("Regular sink");

	
	// gpu server
	Machine GPU_m = new ServerGPU(GPU_q, regular_q, GPU_si,l,"GPU Server", 240, 50);
	Machine GPU2_m = new Server(GPU_q,GPU_si,l,"GPU Server", 145, 42);
	l.start(2000); // 2000 is maximum time

	
	/**
	 * 
	 
	checking out the sink
	
	String[] events = GPU_si.getEvents();
	double[] times = GPU_si.getTimes();
	for(int i = 0; i< times.length; i++) {
		System.out.print(times[i]+" "+events[i]+",  ");
	}
	 **/ 

    }
    
}
