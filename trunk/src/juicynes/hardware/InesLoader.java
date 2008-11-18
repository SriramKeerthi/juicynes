package juicynes.hardware;

public class InesLoader
{
	private int[][] prgRom;
	private int[] chrRom;
	private int prgRomCount;
	private int chrRomCount;
		
	public void load(int[] bytes)
	{	
		prgRomCount = bytes[4];
		chrRomCount = bytes[5];
		boolean usesVertMirroring = (bytes[6] & 0x01) == 0x01;
		boolean hasBatteryRam = (bytes[6] & 0x02) == 0x02;
		boolean hasTrainer = (bytes[6] & 0x04) == 0x04;
		boolean uses4ScreenMirroring = (bytes[6] & 0x08) == 0x03;
		int mapperNumber = (bytes[7]&0xF0) | bytes[6]>>4;
		int ramBanksCount = bytes[8];
		if(ramBanksCount == 0) ramBanksCount = 1;
		
		int index = hasTrainer ? 10+512 : 10;
		
		for(int i=0; i < prgRomCount; i++)
		{
			prgRom[i] = new int[16*1024];
			
			for(int j = 0; i < prgRom.length; j++)
				prgRom[i][j] = bytes[index++];
		}
		
		chrRom = new int[8*1024*chrRomCount];
		
		for(int i = 0; i < chrRom.length; i++)
			chrRom[i] = bytes[index++];
	}
	
	
	public int getPrgRomCount()
	{
		return prgRomCount;
	}
	
	public int[] getPrgRom(int i)
	{
		return prgRom[i];
	}
	
}
