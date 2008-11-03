package juicynes.hardware;

public class Cpu 
{

	// registers
	int accumulator;
	int xindex;
	int yindex;
	int pc;
	int stackPointer;
	
	// Status Register   7  6  5  4  3  2  1  0
	//                   S  V     B  D  I  Z  C
	boolean carryFlag;
	boolean zeroFlag;
	boolean interruptDisabledFlag;
	boolean binaryCodedDecimalModeFlag;
	boolean overflowFlag;
	boolean negativeFlag;
	
	
	int[] memory;
	
	
	public void run()
	{
		
		int opcode = 0;
		int operand = 0;
		
		switch (opcode)
		{
		// ADC (Add memory to accumulator with carry)
		case 0x69:
			pc++;
			break;
			
		case 0x65:
			
			break;
		
		case 0x75:
			
			break;
		
		case 0x6D:
			
			break;
		
		// ------ AND -------- //
		// immediate
		case 0x29:
			and( getByte() );
			break;
		// zero-page
		case 0x25:
			and( memory[getByte()] );
			break;
		// zero-page, x
		case 0x35:
			operand = getByte();
			and( memory[operand+xindex]);
			break;
		// absolute	
		case 0x2D:
			pc++;
			
			break;
		// absolute, x
		case 0x3D:
			
			break;
		// absolute, y	
		case 0x39:
			
			break;
		// indirect, x
		case 0x21:
			
			break;
		// indirect, y	
		case 0x31:
			
			break;
		default:
			break;
		}
	}
	
	
	private int get16bitAddress()
	{
		int b1 = memory[pc];
		pc++;
		int b2 = memory[pc];
		pc++;
		return b1 + (b2<<8);
	}
	
	private int getByte()
	{
		return memory[pc++];
	}
	
	private int addXIndex(int byteval)
	{
		return (byteval+xindex) & 0xFF;
	}
	
	private int addYIndex(int byteval)
	{
		return (byteval+yindex) & 0xFF;
	}
	
	void adc(byte arg)
	{
		
	}
	
	void and(int operand)
	{
		setAccumulator(accumulator & operand);
		setZeroFlag(accumulator);
		setSignFlag(accumulator);
	}
	
	// Shift Left One Bit
	void asl(int operand)
	{
		carryFlag = (accumulator & 0x80) != 0;
		accumulator <<= 1;
		setZeroFlag(accumulator);
		setSignFlag(accumulator);
	}
	
	void asl_memory()
	{
		
	}
	
	void setSignFlag(int arg)
	{
		negativeFlag = (arg & 0x80) == 0x80;
	}
	
	void setZeroFlag(int arg)
	{
		zeroFlag = (arg == 0);
	}
	
	void setAccumulator(int value)
	{
		accumulator = value & 0xFF;
	}
}
