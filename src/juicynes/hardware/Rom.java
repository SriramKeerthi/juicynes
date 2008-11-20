package juicynes.hardware;

import java.io.IOException;

public abstract class Rom 
{
	public abstract void load(String filename) throws IOException;
	
	public abstract int getPrgRomCount();
	
	public abstract int[] getPrgRom(int i);
}
