//multithreading 1.1 uppgift

public class multithreading
{
	public static void main (String[] args)
	{
		//skapar första tråden
		T1 myT1 = new T1();
		
		//väntar 5 sekunder
		try { Thread.sleep(5000); } 
		catch(InterruptedException ie) {}
		System.out.println("");
		
		//skapar andra tråden
		T2 myT2 = new T2();
		
		//väntar 5 sekunder
		try { Thread.sleep(5000); } 
		catch(InterruptedException ie) {}
		System.out.println("");
		
		//pausar tråd 2
		myT2.pause = true;
		
		//väntar 5 sekunder
		try { Thread.sleep(5000); } 
		catch(InterruptedException ie) {}
		System.out.println("");
		
		//aktiverar tråd 2
		myT2.pause = false;
		
		//väntar 5 sekunder
		try { Thread.sleep(5000); } 
		catch(InterruptedException ie) {}
		System.out.println("");
		
		//dödar första tråden
		myT1.alive = false;
		
		//väntar 5 sekunder
		try { Thread.sleep(5000); } 
		catch(InterruptedException ie) {}
		System.out.println("");
		
		//dödar andra tråden
		myT2.alive = false;
		
	}
}

class T1 extends Thread 
{
	boolean alive = true;
	boolean pause = false;
	public T1() 
	{
		start();
	}
	
	public void run() 
	{
		while(alive)
		{
			if(!pause)
			{
				System.out.println("Tråd 1");
				try { Thread.sleep(1000); } 
				catch(InterruptedException ie) {}
			}
			else
			{
				try { Thread.sleep(25); } 
				catch(InterruptedException ie) {}
			}
		}
	}
}

class T2 implements Runnable 
{
	boolean alive = true;
	boolean pause = false;
	Thread t = new Thread(this);

	public T2()
	{
		t.start();
	}

	public void run() 
	{
		while(alive)
		{
			if(!pause)
			{
				System.out.println("Tråd 2");
				try { t.sleep(1000); } 
				catch(InterruptedException ie) {}
			}
			else
			{
				try { Thread.sleep(25); } 
				catch(InterruptedException ie) {}
			}
		}
	}
}