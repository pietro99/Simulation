package Simulation;

/**
 *	A source of products
 *	This class implements CProcess so that it can execute events.
 *	By continuously creating new events, the source keeps busy.
 *	@author Joel Karel
 *	@version %I%, %G%
 */
public class RegularSource implements CProcess
{
	/** Eventlist that will be requested to construct events */
	private CEventList list;
	/** Queue that buffers products for the machine */
	private Queue queue;
	private Queue queue_gpu;

	/** Name of the source */
	private String name;
	/** Mean interarrival time */
	private double meanArrTime;
	/** Interarrival times (in case pre-specified) */
	private double[] interarrivalTimes;
	/** Interarrival time iterator */
	private int interArrCnt;

	/**
	*	Constructor, creates objects
	*        Interarrival times are exponentially distributed with mean 33
	*	@param q	The receiver of the products
	*	@param l	The eventlist that is requested to construct events
	*	@param n	Name of object
	*/
	public RegularSource(ProductAcceptor q,CEventList l,String n)
	{
		list = l;
		queue = (Queue) q;
		name = n;
		meanArrTime=33;
		// put first event in list for initialization
		list.add(this,0,drawNonstationaryPoissonProcess(l.getTime(), meanArrTime)); //target,type,time
	}

	/**
	*	Constructor, creates objects
	*        Interarrival times are exponentially distributed with specified mean
	*	@param q	The receiver of the products
	*	@param l	The eventlist that is requested to construct events
	*	@param n	Name of object
	*	@param m	Mean arrival time
	*/
	public RegularSource(ProductAcceptor q, ProductAcceptor q2,CEventList l,String n,double m)
	{
		list = l;
		queue = (Queue) q;
		queue_gpu = (Queue) q2;
		name = n;
		meanArrTime=m;
		// put first event in list for initialization
		list.add(this,0,drawNonstationaryPoissonProcess(l.getTime(), meanArrTime)); //target,type,time
	}

	/**
	*	Constructor, creates objects
	*        Interarrival times are prespecified
	*	@param q	The receiver of the products
	*	@param l	The eventlist that is requested to construct events
	*	@param n	Name of object
	*	@param ia	interarrival times
	*/
	public RegularSource(ProductAcceptor q,CEventList l,String n,double[] ia)
	{
		list = l;
		queue = (Queue) q;
		name = n;
		meanArrTime=-1;
		interarrivalTimes=ia;
		interArrCnt=0;
		// put first event in list for initialization
		list.add(this,0,interarrivalTimes[0]); //target,type,time
	}
	
        @Override
	public void execute(int type, double tme)
	{
		// show arrival
		System.out.println("Regular Job Arrival at time = " + tme);
		// give arrived product to queue
		Product p = new Product(0);
		p.stamp(tme,"Creation",name);
		if(queue.getQueueLength()>= queue_gpu.getQueueLength()) {
			queue_gpu.giveProduct(p);
		}
		else 
			queue.giveProduct(p);
		
		// generate duration
		
		if(meanArrTime>0)
		{
			double duration = drawNonstationaryPoissonProcess(list.getTime(), meanArrTime);
			// Create a new event in the eventlist
			list.add(this,0,tme+duration); //target,type,time
		}
		else
		{
			interArrCnt++;
			if(interarrivalTimes.length>interArrCnt)
			{
				list.add(this,0,tme+interarrivalTimes[interArrCnt]); //target,type,time
			}
			else
			{
				list.stop();
			}
		}
	}
	
    public static double drawNonstationaryPoissonProcess(double currtime, double meantime)    
    {

        	double lambda = (0.8*24)*Math.sin(currtime*(2*Math.PI/24*60))+meantime;

        	double time = -Math.log(1 - Math.random()) / (1/lambda); 
            System.out.println("time = "+ time);

        	return  time;    
    }
}