
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class RiscV {

	static int pc;
	static int reg[] = new int[32];

	
	public static void main(String[] args) throws IOException {

		System.out.println("Hello RISC-V World!");

		
		ArrayList<Integer> progr=new ArrayList<>();
        //Reading a binary file
        DataInputStream binfile = new DataInputStream(new FileInputStream("C:/Java_workspace/addlarge.bin"));
        while(binfile.available() > 0) {
            int value = binfile.readInt();
            value = Integer.reverseBytes(value);
            progr.add(value);
        }
        
		pc = 0;

		for (;;) {
            
			int instr = progr.get(pc);
			int opcode = instr & 0x7f;
			
			int funct3 = (instr >> 12) & 0x07;
			int funct7 = (instr >> 25);
			int rd = (instr >> 7) & 0x01f;
			int rs1 = (instr >> 15) & 0x01f;
			int rs2 = (instr >> 20) & 0x01f;
			int imm = 0;

			switch (opcode) {
           
			case 0x37:    //LUI
				imm = (instr >> 12);
				reg[rd] = imm;
				break;
		
			case 0x63:   // B-type
				imm = ((instr >> 7) & 0x0f) | (instr >> 20);
				switch (funct3) {
				
				case 0x00:             // BEQ
					if (reg[rs1] == reg[rs2]) {pc = imm;}
					break;
				case 0x01:             // BNE
					if (reg[rs1] != reg[rs2]) {pc = imm;}
					break;
				case 0x04:             // BLT
					if (reg[rs1] < reg[rs2]) {pc = imm;}
					break;
				case 0x05:             // BGE
					if (reg[rs1] >= reg[rs2]) {pc = imm;}
					break;
				}			
				break;
				
			case 0x13:
				imm = (instr >> 20);
			    switch (funct3) {
				
			    case 0x00:             // ADDI
			    	reg[rd] = reg[rs1] + imm;
					break;
			    case 0x02:             // SLTI
			    	if (reg[rs1] < imm) {reg[rd]=1;} else {reg[rd]=0;};
					break;
			    case 0x04:             // XORI
			    	reg[rd] = reg[rs1] ^ imm;
					break;
			    case 0x06:             // ORI
			    	reg[rd] = reg[rs1] | imm;
					break;
			    case 0x07:             // ANDI
			    	reg[rd] = reg[rs1] & imm;
					break;
			    case 0x01:             // SLLI
			    	reg[rd] = reg[rs1] << imm;
					break;
			    case 0x05:             // SRLI
			    	reg[rd] = reg[rs1] << imm;
					break;
			    }
			    break;
			case 0x33:
				switch (funct3) {
				case 0x00:	
					switch (funct7) {
					
					case 0x00:         // ADD 
						reg[rd] = reg[rs1] + reg[rs2];
						break;
					case 0x20:         // SUB
						reg[rd] = reg[rs1] - reg[rs2];
						break;
					}
					break;
				
				case 0x01:	           // SLL
					reg[rd] = reg[rs1] << reg[rs2];
					break;
				case 0x02:	           // SLT
					if (reg[rs1] < reg[rs2]) {reg[rd]=1;} else {reg[rd]=0;};
					break;
				case 0x05:	           // SRL
					reg[rd] = reg[rs1] >> reg[rs2];
					break;
				case 0x04:	           // XOR
					reg[rd] = reg[rs1] ^ reg[rs2];
					break;
				case 0x06:	           // OR
					reg[rd] = reg[rs1] | reg[rs2];
					break;
				case 0x7:	           // and
					reg[rd] = reg[rs1] & reg[rs2];
					break;
				}
				break;
				
				
			default:
				System.out.println("Opcode " + opcode + " not yet implemented");
				break;
			}
			
			for (int i = 0; i < reg.length; ++i) {
				System.out.print(reg[i] + " ");
			}
			System.out.println();
			
			++pc; // We count in 4 byte words
			
			if (pc >= progr.size()) {
				break;
			}
		}
		binfile.close();
		System.out.println("Program exit");
	}

}