package slaves;

public class SecondaryScanner implements Runnable{

	@Override
	public void run() {
		SlaveMasterClient masterclient=new SlaveMasterClient();
		while(true){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}

}
