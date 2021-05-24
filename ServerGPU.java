package Simulation;



/**
 *	Machine in a factory
 *	@author Joel Karel
 *	@version %I%, %G%
 */
public class ServerGPU extends Machine implements CProcess,ProductAcceptor
{
	private ProductAcceptor gpu_sink;
	private double std;


	/**
	*	Constructor
	*        Service times are exponentially distributed with mean 30
	*	@param q	Queue from which the machine has to take products
	*	@param s	Where to send the completed products
	*	@param e	Eventlist that will manage events
	*	@param n	The name of the machine
	*/
	public ServerGPU(Queue q, ProductAcceptor s,ProductAcceptor s2, CEventList e, String n)
	{
		super(q,  s,  e,  n);
		//arbitrary standard deviation
		std = 1;
		//add gpu_queue 
		gpu_sink = s2;
	}
	
	
	public ServerGPU(Queue q,ProductAcceptor s,ProductAcceptor s2, CEventList e, String n, int i, double std) {
		super(q,  s,  e,  n, i);
		
		this.std = std;
		gpu_sink = s2;
	}


	/**
	*	Let the machine accept a product and let it start handling it
	*	@param p	The product that is offered
	*	@return	true if the product is accepted and started, false in all other cases
	*/
        @Override
	public boolean giveProduct(Product p)
	{
		// Only accept something if the machine is idle
		if(status=='i')
		{
			// accept the product
			product=p;
			// mark starting time
			product.stamp(eventlist.getTime(),"Production job started",name);
			// start production
			startProduction();
			// Flag that the product has arrived
			return true;
		}
		// Flag that the product has been rejected
		else { 
			return false;
		
		}
	}
    
    public void execute(int type, double tme)
	{
		// show arrival
		System.out.println("Product finished at time = " + tme);
		// Remove product from system
		product.stamp(tme,"Production complete",name);

		//give product to the appropiate sink
		if(product.getStations().get(0) == "Regular Source")
		{
			sink.giveProduct(product);
		}
		else
		{
			gpu_sink.giveProduct(product);
		}
		
		//sink.giveProduct(product);
		product=null;
		// set machine status to idle
		status='i';
		
		// Ask the queue for products
		queue.askProduct(this);
	
	}

    private void startProduction()
    	{
    		// generate duration
    		if(meanProcTime>0)
    		{
    			double duration = drawRandomNormal(meanProcTime, std);
    			// Create a new event in the eventlist
    			double tme = eventlist.getTime();
    			eventlist.add(this,0,tme+duration); //target,type,time
    			// set status to busy
    			status='b';
    		}
    		else
    		{
    			if(processingTimes.length>procCnt)
    			{
    				eventlist.add(this,0,eventlist.getTime()+processingTimes[procCnt]); //target,type,time
    				// set status to busy
    				status='b';
    				procCnt++;
    			}
    			else
    			{
    				eventlist.stop();
    			}
    		}
    	}

    
  	public static double drawRandomNormal(double mean, double std)
  	{
  	  double r, x, y;
        do {
           x = 2.0 * Math.random() - 1.0;
           y = 2.0 * Math.random() - 1.0;
           r = x*x + y*y;
        } 
        while (r > 1 || r == 0);
        double z = x * Math.sqrt(-2.0 * Math.log(r) / r);
        double norm = z*std + mean;
        
        if (norm <1)
      	  norm = 1;
        
        return norm;
     }
}