package juicynes.hardware;

import java.io.IOException;

import juicynes.Util;

public class InesRom extends Rom
{
	protected int[][] prgRom;
	protected int[] chrRom;
	protected int prgRomCount;
	protected int chrRomCount;
	
	final int PRG_ROM_BANK_SIZE = 16*1024;
	
	public InesRom(String filename) throws IOException
	{
		load(filename);
	}
	
	@Override
	public void load(String filename) throws IOException
	{
		int[] bytes = Util.bytesFromFile(filename);
		
		prgRomCount = bytes[4];
		chrRomCount = bytes[5];
		boolean usesVertMirroring = (bytes[6] & 0x01) == 0x01;
		boolean hasBatteryRam = (bytes[6] & 0x02) == 0x02;
		boolean hasTrainer = (bytes[6] & 0x04) == 0x04;
		boolean uses4ScreenMirroring = (bytes[6] & 0x08) == 0x03;
		int mapperNumber = (bytes[7]&0xF0) | bytes[6]>>4;
		int ramBanksCount = bytes[8];
		if(ramBanksCount == 0) ramBanksCount = 1;
		
		int index = hasTrainer ? 16+512 : 16;
		
		prgRom = new int[prgRomCount][];
		
		for(int i=0; i < prgRomCount; i++)
		{
			prgRom[i] = new int[PRG_ROM_BANK_SIZE];
			
			for(int j = 0; j < PRG_ROM_BANK_SIZE; j++)
				prgRom[i][j] = bytes[index++];
		}
		
		chrRom = new int[8*1024*chrRomCount];
		
		for(int i = 0; i < chrRom.length; i++)
			chrRom[i] = bytes[index++];
	}
	
	@Override
	public int getPrgRomCount()
	{
		return prgRomCount;
	}
	
	@Override
	public int[] getPrgRom(int i)
	{
		return prgRom[i];
	}
}
