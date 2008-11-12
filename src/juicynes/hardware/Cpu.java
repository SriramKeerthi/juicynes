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
		
		switch (opcode)
		{
		
		// (Load/Store Operations)
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
		
		case 0xAA:
			tax();
		break;
		case 0xA8:
			tay();
		break;
		case 0x8A:
			txa();
		break;
		
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
		// and
		case 0x29:
			and( immediate() );
		break;
		case 0x25:
			and( zeropage() );
		break;
		case 0x35:
			and( zeropageX() );
		break;
		case 0x2D:
			and( absolute() );
		break;
		case 0x3D:
			and( absoluteX() );
		break;
		case 0x39:
			and( absoluteY() );
		break;
		case 0x21:
			and( indirectX() );
		break;
		case 0x31:
			and( indirectY() );
		break;
	
		// eor
		case 0x49:
			eor( immediate() );
		break;
		case 0x45:
			eor( zeropage() );
		break;
		case 0x55:
			eor( zeropageX() );
		break;
		case 0x4D:
			eor( absolute() );
		break;
		case 0x5D:
			eor( absoluteX() );
		break;
		case 0x41:
			eor( indirectX() );
		break;
		case 0x51:
			eor( indirectY() );
		break;
		
		// ora
		case 0x09:
			ora( immediate() );
		break;
		case 0x05:
			ora( zeropage() );
		break;
		case 0x15:
			ora( zeropageX() );
		break;
		case 0x0D:
			ora( absolute() );
		break;
		case 0x1D:
			ora( absoluteX() );
		break;
		case 0x19:
			ora( absoluteY() );
		break;
		case 0x01:
			ora( indirectX() );
		break;
		case 0x11:
			ora( indirectY() );
		break;
		
		// bit
		case 0x24:
			bit( zeropage() );
		break;
		case 0x2C:
			bit( absolute() );
		break;
		
		// (Arithmetic Operations)
		// adc
		case 0x69:
			adc( immediate() );
		break;
		case 0x65:
			adc( zeropage() );
		break;
		case 0x75:
			adc( zeropageX() );
		break;
		case 0x6D:
			adc( absolute() );
		break;
		case 0x7D:
			adc( absoluteX() );
		break;
		case 0x79:
			adc( absoluteY() );
		break;
		case 0x61:
			adc( indirectX() );
		break;
		case 0x71:
			adc( indirectY() );
		break;
		
		// sbc
		case 0xE9:
			sbc( immediate() );
		break;
		case 0xE5:
			sbc( zeropage() );
		break;
		case 0xF5:
			sbc( zeropageX() );
		break;
		case 0xED:
			sbc( absolute() );
		break;
		case 0xFD:
			sbc( absoluteX() );
		break;
		case 0xF9:
			sbc( absoluteY() );
		break;
		case 0xE1:
			sbc( indirectX() );
		break;
		case 0xF1:
			sbc( indirectY() );
		break;
		
		// cmp
		case 0xC9:
			cmp( immediate() );
		break;
		case 0xC5:
			cmp( zeropage() );
		break;
		case 0xD5:
			cmp( zeropageX() );
		break;
		case 0xCD:
			cmp( absolute() );
		break;
		case 0xDD:
			cmp( absoluteX() );
		break;
		case 0xD9:
			cmp( absoluteY() );
		break;
		case 0xC1:
			cmp( indirectX() );
		break;
		case 0xD1:
			cmp( indirectY() );
		break;
		
		// cpx
		case 0xE0:
			cpx( immediate() );
		break;
		case 0xE4:
			cpx( zeropage() );
		break;
		case 0xEC:
			cpx( absolute() );
		break;
		
		// cpy
		case 0xC0:
			cpy( immediate() );
		break;
		case 0xC4:
			cpy( zeropage() );
		break;
		case 0xCC:
			cpy( absolute() );
		break;
		
		// (Increment/Decrement Operations)
		// inc
		case 0xE6: inc(zeropage());  break;
		case 0xF6: inc(zeropageX()); break;
		case 0xEE: inc(absolute());  break;
		case 0xFE: inc(absoluteX()); break;
		
		// inx
		case 0xE8: inx(); break;
		
		// iny
		case 0xC8: iny(); break;
		
		// dec
		case 0xC6: dec(zeropage());  break;
		case 0xD6: dec(zeropageX()); break;
		case 0xCE: dec(absolute());  break;
		case 0xDE: dec(absoluteX()); break;
		
		// dex
		case 0xCA: dex(); break;
		
		// dey
		case 0x88: dey(); break;
		
		// (Shift Operations)
		
		
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
	
	// (Logical Operations)
	void and(int address)
	{
		accumulator &= memory[address];
		adjustZeroFlag(accumulator);
		adjustSignFlag(accumulator);
	}
	
	void eor(int address)
	{
		accumulator ^= memory[address];
		adjustZeroFlag(accumulator);
		adjustSignFlag(accumulator);
	}
	
	void ora(int address)
	{
		accumulator |= memory[address];
		adjustZeroFlag(accumulator);
		adjustSignFlag(accumulator);
	}
	
	void bit(int address)
	{
		int value = memory[address];
		adjustZeroFlag(accumulator & value);
		adjustSignFlag(value);
		overflowFlag = ((value & 0x20) == 0x20);
	}
	
	// (Arithmetic Operations)
	
	void adc(int address)
	{
		int value = memory[address];
		int result = accumulator + value + (carryFlag ? 1 : 0);
		
		carryFlag = result > 0xFF;
		overflowFlag = (((accumulator^value) & 0x80) == 0) && 
		               (((accumulator^result) & 0x80) != 0);
		result &= 0xFF;
		adjustZeroFlag(result);
		adjustSignFlag(result);
		
		accumulator = result;
	}
	
	void sbc(int address)
	{
		int value = memory[address];
		int result = accumulator - value - (carryFlag ? 0 : 1);
		
		carryFlag = result < 0;
		overflowFlag = (((accumulator^result) & 0x80) != 0) && 
		               (((accumulator^value) & 0x80) != 0);
		
		result &= 0xFF;
		adjustZeroFlag(result);
		adjustSignFlag(result);
		
		accumulator = result;
	}
	
	void cmp(int address)
	{
		int value = memory[address];
		carryFlag = accumulator >= value;
		zeroFlag = accumulator == value;
		adjustSignFlag(accumulator - value);
	}
	
	void cpx(int address)
	{
		int value = memory[address];
		carryFlag = xindex >= value;
		zeroFlag = xindex == value;
		adjustSignFlag(xindex - value);
	}
	
	void cpy(int address)
	{
		int value = memory[address];
		carryFlag = yindex >= value;
		zeroFlag = yindex == value;
		adjustSignFlag(yindex - value);
	}
	
	// (Increment/Decrement Operations)
	
	void inc(int address)
	{
		int result = (memory[address] + 1) & 0xFF;
		adjustZeroFlag(result);
		adjustSignFlag(result);
		memory[address] = result;
	}
	
	void inx()
	{
		xindex = (xindex + 1) & 0xFF;
		adjustZeroFlag(xindex);
		adjustSignFlag(xindex);
	}
	
	void iny()
	{
		xindex = (yindex + 1) & 0xFF;
		adjustZeroFlag(yindex);
		adjustSignFlag(yindex);
	}
	
	void dec(int address)
	{
		int result = (memory[address] - 1) & 0xFF;
		adjustZeroFlag(result);
		adjustSignFlag(result);
		memory[address] = result;
	}
	
	void dex()
	{
		xindex = (xindex - 1) & 0xFF;
		adjustZeroFlag(xindex);
		adjustSignFlag(xindex);
	}
	
	void dey()
	{
		xindex = (yindex - 1) & 0xFF;
		adjustZeroFlag(yindex);
		adjustSignFlag(yindex);
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
		int b1 = memory[pc++];
		int b2 = memory[pc++];
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