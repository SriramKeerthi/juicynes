package juicynes.hardware;

public class CpuMemoryMap
{
	// 15 PRG-ROM Upper
	// 14 PRG-ROM Upper
	// 13 PRG-ROM Upper
	// 12 PRG-ROM Upper
	// 11 PRG-ROM Lower
	// 10 PRG-ROM Lower
	// 9  PRG-ROM Lower
	// 8  PRG-ROM Lower
	// 7  SRAM
	// 6  SRAM
	// 5  
	// 4
	// 3
	// 2
	// 1
	// 0
	
	// 16 banks of 4kb
	final int BANK_NUM  = 16;
	final int BANK_SIZE = 4096;
	int[][] memory = new int[BANK_NUM][BANK_SIZE];
	
	public void loadRom(Rom rom)
	{
		// load lower prg-rom
		int[] prgRom = rom.getPrgRom(0);
		
		for(int i = 0; i < BANK_SIZE; i++)
		{
			memory[8][i]  = prgRom[i];
			memory[9][i]  = prgRom[i+BANK_SIZE];
			memory[10][i] = prgRom[i+2*BANK_SIZE];
			memory[11][i] = prgRom[i+3*BANK_SIZE];
		}
		
		// load upper prg-rom
		if(rom.getPrgRomCount() == 2)
			prgRom = rom.getPrgRom(1);
		
		for(int i = 0; i < BANK_SIZE; i++)
		{
			memory[12][i] = prgRom[i];
			memory[13][i] = prgRom[i+BANK_SIZE];
			memory[14][i] = prgRom[i+2*BANK_SIZE];
			memory[15][i] = prgRom[i+3*BANK_SIZE];
		}
	}
	
	int read(int address)
	{
		if(address < 0x1FFF)
			address &= 0x07FF;
		else if(address >= 0x2000 && address <= 0x3FFF)
			address = (address & 0x0007) | 0x2000;
		
		return memory[address/BANK_SIZE][address%BANK_SIZE];
	}
	
	void write(int address, int value)
	{
		if(address < 0x1FFF)
			address &= 0x07FF;
		else if(address >= 0x2000 && address <= 0x3FFF)
			address = (address & 0x0007) | 0x2000;
		
		memory[address/BANK_SIZE][address%BANK_SIZE] = value;
	}
	
	void setBank(int bank, int[] values)
	{
		memory[bank] = values;
	}
	
}
