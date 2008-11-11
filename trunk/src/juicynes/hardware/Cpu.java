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
	boolean decimalModeFlag;
	boolean breakFlag;
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
			
		// --- LDA --- //
		case 0xA9:
			lda( memory[immediate()] );
		break;
		case 0xA5:
			lda( memory[zeropage()] );
		break;
		case 0xB5:
			lda( memory[zeropageX()] );
		break;
		case 0xAD:
			lda( memory[absolute()] );
		break;
		case 0xBD:
			lda( memory[absoluteX()] );
		break;
		case 0xB9:
			lda( memory[absoluteY()] );
		break;
		case 0xA1:
			lda( memory[indirectX()] );
		break;
		case 0xB1:
			lda( memory[indirectY()] );
		break;
		
		// --- LDX --- //
		case 0xA2:
			ldx( memory[immediate()] );
		break;
		case 0xA6:
			ldx( memory[zeropage()] );
		break;
		case 0xB6:
			ldx( memory[zeropageY()] );
		break;
		case 0xAE:
			ldx( memory[absolute()] );
		break;
		case 0xBE:
			ldx( memory[absoluteY()] );
		break;
		
		// --- LDY --- //
		case 0xA0:
			ldx( memory[immediate()] );
		break;
		case 0xA4:
			ldx( memory[zeropage()] );
		break;
		case 0xB4:
			ldx( memory[zeropageX()] );
		break;
		case 0xAC:
			ldx( memory[absolute()] );
		break;
		case 0xBC:
			ldx( memory[absoluteX()] );
		break;
		
		// --- STA --- //
		case 0x85:
			sta( zeropage() );
		break;
		case 0x95:
			sta( zeropageX() );
		break;
		case 0x8D:
			sta( absolute() );
		break;
		case 0x9D:
			sta( absoluteX() );
		break;
		case 0x99:
			sta( absoluteY() );
		break;
		case 0x81:
			sta( indirectX() );
		break;
		case 0x91:
			sta( indirectY() );
		break;
		
		// --- STX --- //
		case 0x86:
			stx( zeropage() );
		break;
		case 0x96:
			stx( zeropageY() );
		break;
		case 0x8E:
			stx( absolute() );
		break;
		
		// --- STY --- //
		case 0x84:
			sty( zeropage() );
		break;
		case 0x94:
			sty( zeropageX() );
		break;
		case 0x8C:
			sty( absolute() );
		break;
		
		// --- TAX --- //
		case 0xAA:
			tax();
		break;
		
		// --- TAY --- //
		case 0xA8:
			tay();
		break;
		
		// --- TXA --- //
		case 0x8A:
			txa();
		break;
		
		// --- TYA --- //
		case 0x98:
			tya();
		break;
		
		// (Stack Operations)
		case 0xBA:
			tsx();
		break;
		case 0x9A:
			txs();
		break;
		case 0x48:
			pha();
		break;
		case 0x08:
			php();
		break;
		case 0x68:
			pla();
		break;
		case 0x28:
			plp();
		break;
		
		// (Logical Operations)
		
		
		
		
		// (Arithmetic Operations)
		
		
		
		
		// Catch bad opcodes
		default:
			System.out.println("Illegal opcode: " + opcode);
		break;
		}
	}
	
	
	/// --- Load/Store Operations --- ///
	
	void lda(int value)
	{
		adjustZeroFlag(value);
		adjustSignFlag(value);
		accumulator = value;
	}
	
	void ldx(int value)
	{
		adjustZeroFlag(value);
		adjustSignFlag(value);
		xindex = value;
	}
	
	void ldy(int value)
	{
		adjustZeroFlag(value);
		adjustSignFlag(value);
		yindex = value;
	}
	
	void sta(int address)
	{
		memory[address] = accumulator;
	}
	
	void stx(int address)
	{
		memory[address] = xindex;
	}
	
	void sty(int address)
	{
		memory[address] = yindex;
	}
	
	
	/// --- Register Transfers --- ///
	
	void tax()
	{
		adjustZeroFlag(accumulator);
		adjustSignFlag(accumulator);
		xindex = accumulator;
	}
	
	void tay()
	{
		adjustZeroFlag(accumulator);
		adjustSignFlag(accumulator);
		yindex = accumulator;
	}
	
	void txa()
	{
		adjustZeroFlag(xindex);
		adjustSignFlag(xindex);
		accumulator = xindex;
	}
	
	void tya()
	{
		adjustZeroFlag(yindex);
		adjustSignFlag(yindex);
		accumulator = yindex;
	}
	
	
	// (Stack Operations)
	
	void tsx()
	{
		adjustZeroFlag(stackPointer);
		adjustSignFlag(stackPointer);
		xindex = stackPointer;
	}
	
	void txs()
	{
		stackPointer = xindex;
	}
	
	void pha()
	{
		memory[stackPointer--] = accumulator;
	}
	
	void php()
	{
		memory[stackPointer--] = statusFlag();
	}

	void pla()
	{
		accumulator = memory[stackPointer++];
		adjustZeroFlag(accumulator);
		adjustSignFlag(accumulator);
	}
	
	// 7   6   5   4   3   2   1   0
    // S   V       B   D   I   Z   C
	void plp()
	{
		int value = memory[stackPointer++];
		
		carryFlag = (value & 0x01) == 0x01;
		zeroFlag = (value & 0x02) == 0x02;
		interruptDisabledFlag = (value & 0x04) == 0x04;
		decimalModeFlag = (value & 0x08) == 0x08;
		breakFlag = (value & 0x10) == 0x10;
		overflowFlag = (value & 0x40) == 0x40;
		negativeFlag = (value & 0x80) == 0x80;
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
		//setAccumulator(accumulator & operand);
		//setZeroFlag(accumulator);
		//setSignFlag(accumulator);
	}
	
	// Shift Left One Bit
	int asl(int arg)
	{
		//carryFlag = (arg & 0x80) != 0;
		//arg = (arg<<1) & 0xFF;
		//setZeroFlag(arg);
		//setSignFlag(arg);
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
	
	void beq()
	{
		if(zeroFlag)
			branch(nextByte());
	}
	
	void bit()
	{
		
	}
	
	void bmi()
	{
		if(negativeFlag)
			branch(nextByte());
	}
	
	void bne()
	{
		if(!zeroFlag)
			branch(nextByte());
	}
	
	void bpl()
	{
		if(!negativeFlag)
			branch(nextByte());
	}
	
	void brk()
	{
		
	}
	
	void bvc()
	{
		
	}
	
	void bvs()
	{
		
	}
	
	void clc()
	{
		
	}
	
	void cld()
	{
		
	}
	
	void cli()
	{
		
	}
	
	void clv()
	{
		
	}
	
	void cmp()
	{
		
	}
	
	void cpx()
	{
		
	}
	
	void cpy()
	{
		
	}
	
	
	private int immediate()
	{
		return pc++;
	}
	
	private int zeropage()
	{
		return memory[pc++];
	}
	
	private int zeropageX()
	{
		return memory[ addBytes(pc++, xindex) ];
	}
	
	private int zeropageY()
	{
		return memory[ addBytes(pc++, yindex) ];
	}
	
	private int absolute()
	{
		return next2Bytes();
	}
	
	private int absoluteX()
	{
		return next2Bytes()+xindex;
	}
	
	private int absoluteY()
	{
		return next2Bytes()+yindex;
	}
	
	private int indirectX()
	{
		int index = memory[addBytes(nextByte(),xindex)];
		return get2Bytes(index);
	}
	
	private int indirectY()
	{
		int index = memory[nextByte()];
		return get2Bytes(index)+yindex;
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
	
	private int statusFlag()
	{
		int status = 0x0;
		if(carryFlag)
			status |= 0x01;
		if(zeroFlag)
			status |= 0x02;
		if(interruptDisabledFlag)
			status |= 0x04;
		if(decimalModeFlag)
			status |= 0x08;
		if(breakFlag)
			status |= 0x10;
		if(overflowFlag)
			status |= 0x40;
		if(negativeFlag)
			status |= 0x80;
		
		return status;
	}
	
	private int addBytes(int byte1, int byte2)
	{
		return (byte1 + byte2) % 256;
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
		negativeFlag = (result & 0x80) == 0x80;
	}

	private void adjustCarryFlag(int result)
	{
		// TODO Auto-generated method stub
		
	}
}
