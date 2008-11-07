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
		int cycles = 0;
		int index = 0;
		
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
			and( nextByte() );
			break;
		// zero-page
		case 0x25:
			and( memory[nextByte()] );
			break;
		// zero-page, x
		case 0x35:
			and( memory[addBytes(nextByte(), xindex)] );
			break;
		// absolute	
		case 0x2D:
			and( memory[next2Bytes()] );
			break;
		// absolute, x
		case 0x3D:
			and( memory[next2Bytes()+xindex] );
			break;
		// absolute, y	
		case 0x39:
			and( memory[next2Bytes()+yindex] );
			break;
		// indirect, x
		case 0x21:
			index = memory[addBytes(nextByte(),xindex)];
			and( memory[get2Bytes(index)] );
			break;
		// indirect, y	
		case 0x31:
			index = memory[addBytes(nextByte(),yindex)];
			and( memory[get2Bytes(index)] );
			break;
			
		// ASL (Shift Left One Bit)
		// accumulator
		case 0x0A:
			accumulator = asl(accumulator);
			break;
		// zero page
		case 0x06:
			index = nextByte();
			memory[index] = asl( memory[index] );
			break;
		// zero page, x
		case 0x16:
			index = addBytes(nextByte(), xindex);
			memory[index] = asl( memory[index] );
			break;
		// absolute
		case 0x0E:
			index = next2Bytes();
			memory[index] = asl( memory[index] );
			break;
		// absolute, x
		case 0x1E:
			index = next2Bytes() + xindex;
			memory[index] = asl( memory[index] );
			break;
		default:
			break;
		}
	}
	
	private int next2Bytes()
	{
		int b1 = memory[pc];
		pc++;
		int b2 = memory[pc];
		pc++;
		return b1 | (b2<<8);
	}
	
	private int get2Bytes(int address)
	{
		return memory[address] | (memory[address+1]<<8);
	}
	
	private int nextByte()
	{
		return memory[pc++];
	}
	
	private int addBytes(int byte1, int byte2)
	{
		return (byte1 + byte2) % 256;
	}
	
	private int addXIndex(int byteval)
	{
		return (byteval+xindex) & 0xFF;
	}
	
	private int addYIndex(int byteval)
	{
		return (byteval+yindex) & 0xFF;
	}
	
	// N Z C I D V
	// / / / _ _ /
	void adc(int arg)
	{
		int result = accumulator + arg + (carryFlag ? 1 : 0);
		
		adjustCarryFlag(result);
		adjustSignFlag(result);
		adjustOverFlowFlag(result);
		adjustZeroFlag(result);
		
		setAccumulator(result);
	}
	
	void and(int operand)
	{
		setAccumulator(accumulator & operand);
		setZeroFlag(accumulator);
		setSignFlag(accumulator);
	}
	
	// Shift Left One Bit
	int asl(int arg)
	{
		carryFlag = (arg & 0x80) != 0;
		arg = (arg<<1) & 0xFF;
		setZeroFlag(arg);
		setSignFlag(arg);
		return arg;
	}
	
	void bcc()
	{
		if(!carryFlag)
			branch(nextByte());
	}
	
	void bcs()
	{
		if(carryFlag)
			branch(nextByte());
	}
	
	
	
	private int signedByteValue(int byteval)
	{
		boolean isNegative = (byteval&0x00000080) != 0;
		byteval &=0x0000007F;
		return isNegative ? -byteval : byteval;
	}
	
	private void branch(int arg)
	{
		pc += signedByteValue(arg);
	}
	
	private void adjustZeroFlag(int result)
	{
		result &= 0xFF;
		zeroFlag = (result == 0);
	}

	private void adjustOverFlowFlag(int result)
	{
		overflowFlag = ((result & 0xFFFFFF00) != 0);
	}

	private void adjustSignFlag(int result)
	{
		negativeFlag = (result & 0x80)
	}

	private void adjustCarryFlag(int result)
	{
		// TODO Auto-generated method stub
		
	}
	
	void setAccumulator(int value)
	{
		accumulator = value & 0xFF;
	}
}
