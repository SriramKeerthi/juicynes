package juicynes.hardware;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Cpu 
{
	// registers
	int accumulator = 0;
	int xindex = 0;
	int yindex = 0;
	int pc = 0xC000;
	int stackPointer = 0xFD;
	
	// Status Register   7  6  5  4  3  2  1  0
	//                   S  V     B  D  I  Z  C
	boolean carryFlag = false;
	boolean zeroFlag = false;
	boolean interruptDisabledFlag = true;
	boolean decimalModeFlag = false;
	boolean breakFlag = false;
	//      empty = true;
	boolean overflowFlag = false;
	boolean negativeFlag = false;
	
	CpuMemoryMap memory;
	
	int cycles;
	int opcode;
	boolean pageCrossed;
	
	public Cpu(CpuMemoryMap memory)
	{
		this.memory = memory;
	}
	
	public void run() throws IOException
	{
		BufferedReader br = new BufferedReader(
				new FileReader("test/nestest.log"));
		
		int i = 1;
		while(true)
		{
			String line = br.readLine();
			System.out.println("[" + (i++) + "]");
			System.out.println(line);
			
			System.out.format("%1$-4s  %2$-2s %3$-2s %4$-2s",
					Integer.toHexString(pc),
					Integer.toHexString(memory.read(pc)),
					Integer.toHexString(memory.read(pc+1)),
					Integer.toHexString(memory.read(pc+2)));
			
			System.out.print("                                  ");
			System.out.format("A:%1$-2s X:%2$-2s Y:%3$-2s P:%4$-2s SP:%5$-2s", 
					Integer.toHexString(accumulator),
					Integer.toHexString(xindex),
					Integer.toHexString(yindex),
					Integer.toHexString(statusFlag()),
					Integer.toHexString(stackPointer));
															 
			System.out.println();
			
			opcode = memory.read(pc++);
			
			int expectedPc = Integer.decode("0x" + line.substring(0,4)).intValue();
			int expectedOpcode = Integer.decode("0x" + line.substring(6,8)).intValue();
			int expectedAccumulator = Integer.decode("0x" + line.substring(50,52)).intValue();
			int expectedXindex = Integer.decode("0x" + line.substring(55,57)).intValue();
			int expectedYindex = Integer.decode("0x" + line.substring(60,62)).intValue();
			int expectedStatus = Integer.decode("0x" + line.substring(65,67)).intValue();
			int expectedSp = Integer.decode("0x" + line.substring(71,73)).intValue();
			
			if( !(pc-1 == expectedPc && opcode == expectedOpcode && 
				accumulator == expectedAccumulator && xindex == expectedXindex &&
				yindex == expectedYindex && statusFlag() == expectedStatus &&
				stackPointer == expectedSp) )
				System.out.println("Error!");
			
			switch (opcode)
			{
			// (Load/Store Operations)
			case 0xA9: lda(immediate()); cycles += 2; break;
			case 0xA5: lda(zeropage());  cycles += 3; break;
			case 0xB5: lda(zeropageX()); cycles += 4; break;
			case 0xAD: lda(absolute());  cycles += 4; break;
			case 0xBD: lda(absoluteX()); cycles += pageCrossed?5:4; break;
			case 0xB9: lda(absoluteY()); cycles += pageCrossed?5:4; break;
			case 0xA1: lda(indirectX()); cycles += 6; break;
			case 0xB1: lda(indirectY()); cycles += pageCrossed?6:5; break;
			
			case 0xA2: ldx(immediate()); cycles += 2; break;
			case 0xA6: ldx(zeropage());  cycles += 3; break;
			case 0xB6: ldx(zeropageY()); cycles += 4; break;
			case 0xAE: ldx(absolute());  cycles += 4; break;
			case 0xBE: ldx(absoluteY()); cycles += pageCrossed?5:4; break;
			
			case 0xA0: ldy(immediate()); cycles += 2; break;
			case 0xA4: ldy(zeropage());  cycles += 3; break;
			case 0xB4: ldy(zeropageX()); cycles += 4; break;
			case 0xAC: ldy(absolute());  cycles += 4; break;
			case 0xBC: ldy(absoluteX()); cycles += pageCrossed?5:4; break;
			
			case 0x85: sta(zeropage());  cycles += 3; break;
			case 0x95: sta(zeropageX()); cycles += 4; break;
			case 0x8D: sta(absolute());  cycles += 4; break;
			case 0x9D: sta(absoluteX()); cycles += 5; break;
			case 0x99: sta(absoluteY()); cycles += 5; break;
			case 0x81: sta(indirectX()); cycles += 6; break;
			case 0x91: sta(indirectY()); cycles += 6; break;
			
			case 0x86: stx(zeropage());  cycles += 3; break;
			case 0x96: stx(zeropageY()); cycles += 4; break;
			case 0x8E: stx(absolute());  cycles += 4; break;
			
			case 0x84: sty(zeropage());  cycles += 3; break;
			case 0x94: sty(zeropageX()); cycles += 4; break;
			case 0x8C: sty(absolute());  cycles += 4; break;
			
			case 0xAA: tax(); cycles += 2; break; 
			case 0xA8: tay(); cycles += 2; break;
			case 0x8A: txa(); cycles += 2; break;
			case 0x98: tya(); cycles += 2; break;
			
			// (Stack Operations)
			case 0xBA: tsx(); cycles += 2; break;
			case 0x9A: txs(); cycles += 2; break;
			case 0x48: pha(); cycles += 3; break; 
			case 0x08: php(); cycles += 3; break;
			case 0x68: pla(); cycles += 4; break;
			case 0x28: plp(); cycles += 4; break;
			
			// (Logical Operations)
			case 0x29: and(immediate()); cycles += 2; break;
			case 0x25: and(zeropage());  cycles += 3; break;
			case 0x35: and(zeropageX()); cycles += 4; break;
			case 0x2D: and(absolute());  cycles += 4; break;
			case 0x3D: and(absoluteX()); cycles += pageCrossed?5:4; break;
			case 0x39: and(absoluteY()); cycles += pageCrossed?5:4; break;
			case 0x21: and(indirectX()); cycles += 6; break;
			case 0x31: and(indirectY()); cycles += pageCrossed?6:5; break;
		
			case 0x49: eor(immediate()); cycles += 2; break;
			case 0x45: eor(zeropage());  cycles += 3; break;
			case 0x55: eor(zeropageX()); cycles += 4; break;
			case 0x4D: eor(absolute());  cycles += 4; break;
			case 0x5D: eor(absoluteX()); cycles += pageCrossed?5:4; break;
			case 0x59: eor(absoluteY()); cycles += pageCrossed?5:4; break;
			case 0x41: eor(indirectX()); cycles += 6; break;
			case 0x51: eor(indirectY()); cycles += pageCrossed?6:5; break;
			
			case 0x09: ora(immediate()); cycles += 2; break;
			case 0x05: ora(zeropage());  cycles += 3; break;
			case 0x15: ora(zeropageX()); cycles += 4; break;
			case 0x0D: ora(absolute());  cycles += 4; break;
			case 0x1D: ora(absoluteX()); cycles += pageCrossed?5:4; break;
			case 0x19: ora(absoluteY()); cycles += pageCrossed?5:4; break;
			case 0x01: ora(indirectX()); cycles += 6; break;
			case 0x11: ora(indirectY()); cycles += 5; break;
			
			case 0x24: bit(zeropage()); cycles += 3; break;
			case 0x2C: bit(absolute()); cycles += 4; break;
			
			// (Arithmetic Operations)
			case 0x69: adc(immediate()); cycles += 2; break;
			case 0x65: adc(zeropage());  cycles += 3; break;
			case 0x75: adc(zeropageX()); cycles += 4; break;
			case 0x6D: adc(absolute());  cycles += 4; break;
			case 0x7D: adc(absoluteX()); cycles += pageCrossed?5:4; break;
			case 0x79: adc(absoluteY()); cycles += pageCrossed?5:4; break;
			case 0x61: adc(indirectX()); cycles += 6; break;
			case 0x71: adc(indirectY()); cycles += pageCrossed?6:5; break;
			
			case 0xE9: sbc(immediate()); cycles += 2; break;
			case 0xE5: sbc(zeropage());  cycles += 3; break;
			case 0xF5: sbc(zeropageX()); cycles += 4; break;
			case 0xED: sbc(absolute());  cycles += 4; break;
			case 0xFD: sbc(absoluteX()); cycles += pageCrossed?5:4; break;
			case 0xF9: sbc(absoluteY()); cycles += pageCrossed?5:5; break;
			case 0xE1: sbc(indirectX()); cycles += 6; break;
			case 0xF1: sbc(indirectY()); cycles += pageCrossed?6:5; break;
			
			case 0xC9: cmp(immediate()); cycles += 2; break;
			case 0xC5: cmp(zeropage());  cycles += 3; break;
			case 0xD5: cmp(zeropageX()); cycles += 4; break;
			case 0xCD: cmp(absolute());  cycles += 4; break;
			case 0xDD: cmp(absoluteX()); cycles += pageCrossed?5:4; break;
			case 0xD9: cmp(absoluteY()); cycles += pageCrossed?5:4; break;
			case 0xC1: cmp(indirectX()); cycles += 6; break;
			case 0xD1: cmp(indirectY()); cycles += pageCrossed?6:5; break;
			
			case 0xE0: cpx(immediate()); cycles += 2; break;
			case 0xE4: cpx(zeropage());  cycles += 3; break;
			case 0xEC: cpx(absolute());  cycles += 4; break;
			
			case 0xC0: cpy(immediate()); cycles += 2; break;
			case 0xC4: cpy(zeropage());  cycles += 3; break;
			case 0xCC: cpy(absolute());  cycles += 4; break;
			
			// (Increment/Decrement Operations)
			case 0xE6: inc(zeropage());  cycles += 5; break;
			case 0xF6: inc(zeropageX()); cycles += 6; break;
			case 0xEE: inc(absolute());  cycles += 6; break;
			case 0xFE: inc(absoluteX()); cycles += 7; break;
			
			case 0xE8: inx(); cycles += 2; break;			
			case 0xC8: iny(); cycles += 2; break;
			
			case 0xC6: dec(zeropage());  cycles += 5; break;
			case 0xD6: dec(zeropageX()); cycles += 6; break;
			case 0xCE: dec(absolute());  cycles += 6; break;
			case 0xDE: dec(absoluteX()); cycles += 7; break;
			
			case 0xCA: dex(); cycles += 2; break;			
			case 0x88: dey(); cycles += 2; break;
			
			// (Shift Operations)
			case 0x0A: asl_accumulator(); cycles += 2; break;
			case 0x06: asl(zeropage());	  cycles += 5; break;
			case 0x16: asl(zeropageX());  cycles += 6; break;
			case 0x0E: asl(absolute());   cycles += 6; break;
			case 0x1E: asl(absoluteX());  cycles += 7; break;
			
			case 0x4A: lsr_accumulator(); cycles += 2; break;
			case 0x46: lsr(zeropage());   cycles += 5; break;
			case 0x56: lsr(zeropageX());  cycles += 6; break;
			case 0x4E: lsr(absolute());   cycles += 6; break;
			case 0x5E: lsr(absoluteX());  cycles += 7; break;
			
			case 0x2A: rol_accumulator(); cycles += 2; break;
			case 0x26: rol(zeropage());   cycles += 5; break;
			case 0x36: rol(zeropageX());  cycles += 6; break;
			case 0x2E: rol(absolute());   cycles += 6; break;
			case 0x3E: rol(absoluteX());  cycles += 7; break;
			
			case 0x6A: ror_accumulator(); cycles += 2; break;
			case 0x66: ror(zeropage());   cycles += 5; break;
			case 0x76: ror(zeropageX());  cycles += 6; break;
			case 0x6E: ror(absolute());   cycles += 6; break;
			case 0x7E: ror(absoluteX());  cycles += 7; break;
			
			// (Jumps and Calls Operations)
			case 0x4C: jmp(absolute()); cycles += 3; break;
			case 0x6C: jmp(indirect()); cycles += 5; break;
			
			case 0x20: jsr(absolute()); cycles += 6; break;
			
			case 0x60: rts(); cycles += 6; break;
			
			// (Branch Operations)
			case 0x90: bcc(); cycles += 2; break;
			case 0xB0: bcs(); cycles += 2; break;
			case 0xF0: beq(); cycles += 2; break;
			case 0x30: bmi(); cycles += 2; break;
			case 0xD0: bne(); cycles += 2; break;
			case 0x10: bpl(); cycles += 2; break;
			case 0x50: bvc(); cycles += 2; break;
			case 0x70: bvs(); cycles += 2; break;
			
			// (Status Flag Operations)
			case 0x18: clc(); cycles += 2; break;
			case 0xD8: cld(); cycles += 2; break;
			case 0x58: cli(); cycles += 2; break;
			case 0xB8: clv(); cycles += 2; break;
			case 0x38: sec(); cycles += 2; break;
			case 0xF8: sed(); cycles += 2; break;
			case 0x78: sei(); cycles += 2; break;
			
			// (System Function Operations)
			case 0x00: brk(); cycles += 7; break;
			case 0xEA: nop(); cycles += 2; break;
			case 0x40: rti(); cycles += 6; break;
			
			// Catch bad opcodes
			default:
				System.out.println("Illegal opcode: " + opcode);
			break;
			}
			
			if(memory.read(0x02) != 0)
				System.out.println("0x02: 0x" + Integer.toHexString(memory.read(0x02)) );
			if(memory.read(0x03) != 0)
				System.out.println("0x03: 0x" + Integer.toHexString(memory.read(0x03)) );
		}
	}
	
	
	// (Load/Store Operations)
	
	void lda(int address)
	{
		int val = memory.read(address);
		adjustZeroFlag(val);
		adjustSignFlag(val);
		accumulator = val;
	}
	
	void ldx(int address)
	{
		int val = memory.read(address);
		adjustZeroFlag(val);
		adjustSignFlag(val);
		xindex = val;
	}
	
	void ldy(int address)
	{
		int val = memory.read(address);
		adjustZeroFlag(val);
		adjustSignFlag(val);
		yindex = val;
	}
	
	void sta(int address)
	{
		memory.write(address, accumulator);
	}
	
	void stx(int address)
	{
		memory.write(address, xindex);
	}
	
	void sty(int address)
	{
		memory.write(address, yindex);
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
		push(accumulator);
	}
	
	void php()
	{
		breakFlag = true;
		push(statusFlag());
		breakFlag = false;
	}

	void pla()
	{
		accumulator = pull();
		adjustZeroFlag(accumulator);
		adjustSignFlag(accumulator);
	}
	
	void plp()
	{
		int value = pull();
		
		carryFlag = (value & 0x01) == 0x01;
		zeroFlag = (value & 0x02) == 0x02;
		interruptDisabledFlag = (value & 0x04) == 0x04;
		decimalModeFlag = (value & 0x08) == 0x08;
		breakFlag = false;
		overflowFlag = (value & 0x40) == 0x40;
		negativeFlag = (value & 0x80) == 0x80;
	}
	
	// (Logical Operations)
	void and(int address)
	{
		accumulator &= memory.read(address);
		adjustZeroFlag(accumulator);
		adjustSignFlag(accumulator);
	}
	
	void eor(int address)
	{
		accumulator ^= memory.read(address);
		adjustZeroFlag(accumulator);
		adjustSignFlag(accumulator);
	}
	
	void ora(int address)
	{
		accumulator |= memory.read(address);
		adjustZeroFlag(accumulator);
		adjustSignFlag(accumulator);
	}
	
	void bit(int address)
	{
		int value = memory.read(address);
		adjustZeroFlag(accumulator & value);
		adjustSignFlag(value);
		overflowFlag = ((value & 0x40) == 0x40);
	}
	
	// (Arithmetic Operations)
	
	void adc(int address)
	{
		int value = memory.read(address);
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
		int value = memory.read(address);
		int result = accumulator - value - (carryFlag ? 0 : 1);
		
		carryFlag = result >= 0;
		overflowFlag = (((accumulator^result) & 0x80) != 0) && 
		               (((accumulator^value) & 0x80) != 0);
		
		result &= 0xFF;
		adjustZeroFlag(result);
		adjustSignFlag(result);
		
		accumulator = result;
	}
	
	void cmp(int address)
	{
		int value = memory.read(address);
		carryFlag = accumulator >= value;
		zeroFlag = accumulator == value;
		adjustSignFlag(accumulator - value);
	}
	
	void cpx(int address)
	{
		int value = memory.read(address);
		carryFlag = xindex >= value;
		zeroFlag = xindex == value;
		adjustSignFlag(xindex - value);
	}
	
	void cpy(int address)
	{
		int value = memory.read(address);
		carryFlag = yindex >= value;
		zeroFlag = yindex == value;
		adjustSignFlag(yindex - value);
	}
	
	// (Increment/Decrement Operations)
	
	void inc(int address)
	{
		int result = (memory.read(address) + 1) & 0xFF;
		adjustZeroFlag(result);
		adjustSignFlag(result);
		memory.write(address, result);
	}
	
	void inx()
	{
		xindex = (xindex + 1) & 0xFF;
		adjustZeroFlag(xindex);
		adjustSignFlag(xindex);
	}
	
	void iny()
	{
		yindex = (yindex + 1) & 0xFF;
		adjustZeroFlag(yindex);
		adjustSignFlag(yindex);
	}
	
	void dec(int address)
	{
		int result = (memory.read(address) - 1) & 0xFF;
		adjustZeroFlag(result);
		adjustSignFlag(result);
		memory.write(address, result);
	}
	
	void dex()
	{
		xindex = (xindex - 1) & 0xFF;
		adjustZeroFlag(xindex);
		adjustSignFlag(xindex);
	}
	
	void dey()
	{
		yindex = (yindex - 1) & 0xFF;
		adjustZeroFlag(yindex);
		adjustSignFlag(yindex);
	}
	
	// (Shift Operations)
	void asl(int address)
	{
		int val = memory.read(address);
		
		carryFlag = (val & 0x80) == 0x80;
		val <<= 1;
		val &= 0xFF;
		adjustZeroFlag(val);
		adjustSignFlag(val);
		
		memory.write(address, val);
	}
	
	void asl_accumulator()
	{
		carryFlag = (accumulator & 0x80) == 0x80;
		accumulator <<= 1;
		accumulator &= 0xFF;
		adjustZeroFlag(accumulator);
		adjustSignFlag(accumulator);
	}
	
	void lsr(int address)
	{
		int val = memory.read(address);
		
		carryFlag = (val & 0x01) == 0x01;
		val >>= 1;
		adjustZeroFlag(val);
		adjustSignFlag(val);
		
		memory.write(address, val);
	}
	
	void lsr_accumulator()
	{
		carryFlag = (accumulator & 0x01) == 0x01;
		accumulator >>= 1;
		adjustZeroFlag(accumulator);
		adjustSignFlag(accumulator);
	}
	
	void rol(int address)
	{
		int val = memory.read(address);
		
		boolean newCarryFlag = (val & 0x80) == 0x80;
		val <<= 1;
		val &= 0xFF;
		if(carryFlag) 
			val |= 0x01;
		
		carryFlag = newCarryFlag;
		adjustZeroFlag(val);
		adjustSignFlag(val);
		
		memory.write(address, val);
	}
	
	void rol_accumulator()
	{
		boolean newCarryFlag = (accumulator & 0x80) == 0x80;
		accumulator <<= 1;
		accumulator &= 0xFF;
		if(carryFlag) 
			accumulator |= 0x01;
		
		carryFlag = newCarryFlag;
		adjustZeroFlag(accumulator);
		adjustSignFlag(accumulator);
	}
	
	void ror(int address)
	{
		int val = memory.read(address);
		
		boolean newCarryFlag = (val & 0x01) == 0x01;
		val >>= 1;
		if(carryFlag) 
			val |= 0x80;
		
		carryFlag = newCarryFlag;
		adjustZeroFlag(val);
		adjustSignFlag(val);
		
		memory.write(address, val);
	}
	
	void ror_accumulator()
	{
		boolean newCarryFlag = (accumulator & 0x01) == 0x01;
		accumulator >>= 1;
		if(carryFlag) 
			accumulator |= 0x80;
		
		carryFlag = newCarryFlag;
		adjustZeroFlag(accumulator);
		adjustSignFlag(accumulator);
	}
	
	// (Jump and Call Operations)
	void jmp(int address)
	{
		pc = address;
	}
	
	void jsr(int address)
	{
		pc--;
		push((pc>>8) & 0xFF);
		push(pc & 0xFF);
		pc = address;
	}
	
	void rts()
	{
		pc = pull();
		pc += (pull()<<8);
		pc += 1;
		pc &= 0xFFFF;
	}
	
	// (Branch Operations)
	void bcc()
	{
		int val = nextByte();
		if(!carryFlag)
			branch(val);
	}
	
	void bcs()
	{
		int val = nextByte();
		if(carryFlag)
			branch(val);
	}
	
	void beq()
	{
		int val = nextByte();
		if(zeroFlag)
			branch(val);
	}
	
	void bmi()
	{
		int val = nextByte();
		if(negativeFlag)
			branch(val);
	}
	
	void bne()
	{
		int val = nextByte();
		if(!zeroFlag)
			branch(val);
	}
	
	void bpl()
	{
		int val = nextByte();
		if(!negativeFlag)
			branch(val);
	}
	
	
	void bvc()
	{
		int val = nextByte();
		if(!overflowFlag)
			branch(val);
	}
	
	void bvs()
	{
		int val = nextByte();
		if(overflowFlag)
			branch(val);
	}
	
	// (Status Flag Change Operations)
	void clc()
	{
		carryFlag = false;
	}
	
	void cld()
	{
		decimalModeFlag = false;
	}
	
	void cli()
	{
		interruptDisabledFlag = false;
	}
	
	void clv()
	{
		overflowFlag = false;
	}
	
	void sec()
	{
		carryFlag = true;
	}
	
	void sed()
	{
		decimalModeFlag = true;
	}
	
	void sei()
	{
		interruptDisabledFlag = true;
	}
	
	// (System Functions)
	void brk()
	{
		push(pc);
		push(statusFlag());
		pc = irqVector();
		breakFlag = true;
	}
	
	void nop()
	{
	}
	
	void rti()
	{
		int statusFlags = pull();
		int p1 = pull();
		int p2 = pull();
		pc = p1 | (p2<<8);
		
		carryFlag = (statusFlags & 0x01) == 0x01;
		zeroFlag = (statusFlags & 0x02) == 0x02;
		interruptDisabledFlag = (statusFlags & 0x04) == 0x04;
		decimalModeFlag = (statusFlags & 0x08) == 0x08;
		breakFlag = (statusFlags & 0x10) == 0x10;
		overflowFlag = (statusFlags & 0x40) == 0x40;
		negativeFlag = (statusFlags & 0x80) == 0x80;
	}
	
	
	// --- Address Modes --- //
	// All functions return the address to data
	// rather than the actual data.
	private int immediate()
	{
		return pc++;
	}
	
	private int zeropage()
	{
		return memory.read(pc++);
	}
	
	private int zeropageX()
	{
		return memory.read( addBytes(pc++, xindex) );
	}
	
	private int zeropageY()
	{
		return memory.read( addBytes(pc++, yindex) );
	}
	
	private int absolute()
	{
		return next2Bytes();
	}
	
	private int absoluteX()
	{
		int base = next2Bytes();
		pageCrossed = ((base & 0xFF) + xindex) > 0xFF;
		return base+xindex;
	}
	
	private int absoluteY()
	{
		int base = next2Bytes();
		pageCrossed = ((base & 0xFF) + yindex) > 0xFF;
		return base+yindex;
	}
	
	private int indirect()
	{
		return get2Bytes( next2Bytes() );
	}
	
	private int indirectX()
	{
		return get2Bytes(addBytes(nextByte(), xindex));
	}
	
	private int indirectY()
	{
		int base = get2Bytes( memory.read(nextByte()) );
		pageCrossed = ((base & 0xFF) + yindex) > 0xFF;
		return base+yindex;
	}
	
	private int next2Bytes()
	{
		int b1 = memory.read(pc++);
		int b2 = memory.read(pc++);
		return b1 | (b2<<8);
	}
	
	private int get2Bytes(int address)
	{
		return memory.read(address) | (memory.read(address+1)<<8);
	}
	
	private int nextByte()
	{
		return memory.read(pc++);
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
		status |= 0x20;
		if(overflowFlag)
			status |= 0x40;
		if(negativeFlag)
			status |= 0x80;
		
		return status;
	}
	
	private int addBytes(int byte1, int byte2)
	{
		return (byte1 + byte2) & 0xFF;
	}
	
	private int signedByteValue(int byteval)
	{
		boolean isNegative = (byteval&0x80) == 0x80;
		byteval &=0x7F;
		return isNegative ? -byteval : byteval;
	}
	
	private void branch(int arg)
	{
		cycles += 1;
		int offset = signedByteValue(arg);
		pageCrossed = ((pc & 0xFF) + offset) > 0xFF;
		if(pageCrossed)
			cycles += 1;
		
		pc += offset;
	}
	
	private void adjustZeroFlag(int result)
	{
		result &= 0xFF;
		zeroFlag = (result == 0);
	}

	private void adjustSignFlag(int result)
	{
		negativeFlag = (result & 0x80) == 0x80;
	}
	
	// Stack Helper Functions
	void push(int val)
	{
		memory.write(stackPointer + 0x100, val);
		
		stackPointer--;
		stackPointer = stackPointer&0xFF;
	}
	
	int pull()
	{
		stackPointer++;
		stackPointer = stackPointer&0xFF;
		
		return memory.read(stackPointer + 0x100);
	}
	
	int nmiVector()
	{
		return get2Bytes(0xFFFA);
	}
	
	int resetVector()
	{
		return get2Bytes(0xFFFC);
	}
	
	int irqVector()
	{
		return get2Bytes(0xFFFE);
	}
}
